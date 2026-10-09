param(
    [ValidateSet('otel', 'skywalking')][string]$Telemetry = 'otel',
    [ValidateSet('postgresql', 'mysql')][string]$Database = 'postgresql',
    [switch]$Export,
    [switch]$Test,
    [string]$AgentJar = 'build/tools/skywalking-agent/skywalking-agent.jar',
    [string]$Distribution = 'Ubuntu-24.04'
)
$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
function WslJson([string[]]$Command) {
    $output = & wsl -d $Distribution -u root -- @Command
    if ($LASTEXITCODE -ne 0) { throw 'WSL kubectl inspection failed' }
    return ($output | Out-String | ConvertFrom-Json)
}
function PodFile([string]$Path) {
    $output = & wsl -d $Distribution -u root -- kubectl exec -n base $podName -- cat $Path
    if ($LASTEXITCODE -ne 0) { throw 'Could not read database pod credentials' }
    return ($output | Out-String).TrimEnd("`r", "`n")
}
$podName = "$Database-0"
$pod = WslJson @('kubectl', 'get', 'pod', $podName, '-n', 'base', '-o', 'json')
$service = WslJson @('kubectl', 'get', 'service', "$Database-external", '-n', 'base', '-o', 'json')
$hostAddress = $service.status.loadBalancer.ingress[0].ip
if (!$hostAddress) { throw 'Database service has no reachable IP; configure JDBC environment variables manually' }
$files = @{}
foreach ($entry in $pod.spec.containers[0].env) { $files[$entry.name] = $entry.value }
if ($Database -eq 'postgresql') {
    foreach ($name in @('POSTGRES_USER_FILE', 'POSTGRES_PASSWORD_FILE', 'POSTGRES_DB_FILE')) {
        if (!$files[$name]) { throw "Missing PostgreSQL file-based setting: $name" }
    }
    $settings = @{
        KUMA_LAB_OBSERVABILITY_JDBC_URL = "jdbc:postgresql://${hostAddress}:5432/$(PodFile $files['POSTGRES_DB_FILE'])"
        KUMA_LAB_OBSERVABILITY_JDBC_USER = (PodFile $files['POSTGRES_USER_FILE'])
        KUMA_LAB_OBSERVABILITY_JDBC_PASSWORD = (PodFile $files['POSTGRES_PASSWORD_FILE'])
    }
} else {
    $password = $files['MYSQL_ROOT_PASSWORD']
    if (!$password) {
        $reference = ($pod.spec.containers[0].env | Where-Object name -eq 'MYSQL_ROOT_PASSWORD').valueFrom.secretKeyRef
        if (!$reference) { throw 'Missing MySQL root password setting' }
        $secret = WslJson @('kubectl', 'get', 'secret', $reference.name, '-n', 'base', '-o', 'json')
        $password = [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($secret.data.PSObject.Properties[$reference.key].Value))
    }
    $settings = @{
        KUMA_LAB_OBSERVABILITY_JDBC_URL = "jdbc:mysql://${hostAddress}:3306/?useSSL=false&allowPublicKeyRetrieval=true"
        KUMA_LAB_OBSERVABILITY_JDBC_USER = 'root'
        KUMA_LAB_OBSERVABILITY_JDBC_PASSWORD = $password
    }
}
$previous = @{}
try {
    foreach ($name in $settings.Keys) {
        $previous[$name] = [Environment]::GetEnvironmentVariable($name, 'Process')
        [Environment]::SetEnvironmentVariable($name, $settings[$name], 'Process')
    }
    $taskArgs = @(':kuma-project:kuma-project-lab:otelDatabaseLab', '--console=plain')
    if ($Telemetry -eq 'skywalking') {
        if ($Export) { throw '-Export is for OTel; SkyWalking Agent exports natively' }
        $taskArgs = @(':kuma-project:kuma-project-lab:skywalkingDatabaseLab', "-PskywalkingAgent=$AgentJar", '--console=plain')
    } elseif ($Export) { $taskArgs += '-PotelExport=true' }
    if ($Test) {
        if ($Telemetry -ne 'otel' -or $Export) { throw '-Test runs database integration tests without Agent/export' }
        $taskArgs = @(':kuma-project:kuma-project-lab:test', '--tests', 'com.kuma.cloud.lab.observability.*', '--console=plain')
    }
    & (Join-Path $repoRoot 'gradlew.bat') -p $repoRoot @taskArgs
    if ($LASTEXITCODE -ne 0) { throw "Database experiment failed (exit $LASTEXITCODE)" }
} finally {
    foreach ($name in $previous.Keys) { [Environment]::SetEnvironmentVariable($name, $previous[$name], 'Process') }
    $settings.Clear()
}
