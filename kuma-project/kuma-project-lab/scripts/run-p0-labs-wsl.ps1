param(
    [ValidateSet('all', 'mysql', 'redis', 'threadpool')][string]$Group = 'all',
    [ValidateSet('all','indexes','isolation','locks','deadlock','propagation','optimistic','cache','avalanche','consistency','lua','fencing','queue','exceptions','cancellation','starvation','context','bulkhead')][string]$Lesson = 'all',
    [switch]$Test,
    [string]$Distribution = 'Ubuntu-24.04'
)
$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$choices = @{
    mysql = @('all','indexes','isolation','locks','deadlock','propagation','optimistic')
    redis = @('all','cache','avalanche','consistency','locks','lua','fencing')
    threadpool = @('all','queue','exceptions','cancellation','starvation','context','bulkhead')
}
if ($Group -eq 'all' -and $Lesson -ne 'all') { throw '-Group all requires -Lesson all' }
if ($Group -ne 'all' -and $Lesson -notin $choices[$Group]) { throw "Lesson $Lesson does not belong to $Group" }
if ($Test -and $Lesson -ne 'all') { throw '-Test requires -Lesson all' }
function WslJson([string[]]$Arguments) {
    $output = & wsl -d $Distribution -u root -- kubectl @Arguments
    if ($LASTEXITCODE -ne 0) { throw 'WSL Kubernetes query failed' }
    return ($output | Out-String | ConvertFrom-Json)
}
function DecodeSecret($Secret, [string]$Key) {
    $encoded = $Secret.data.PSObject.Properties[$Key].Value
    if (!$encoded) { throw "Missing required secret key: $Key" }
    return [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($encoded))
}
$settings = @{}
$previous = @{}
try {
    if ($Group -ne 'threadpool') {
        $running = ((& wsl --list --running --quiet) | Out-String).Replace([string][char]0, '')
        if ($LASTEXITCODE -ne 0 -or $running -notmatch [regex]::Escape($Distribution)) {
            throw 'The selected WSL distribution must already be running'
        }
        # Inspect existing resources only. Credentials stay in this process and child JVMs.
        $services = WslJson @('get','services','mysql-external','redis-external','-n','base','-o','json')
        $mysqlService = $services.items | Where-Object { $_.metadata.name -eq 'mysql-external' }
        $redisService = $services.items | Where-Object { $_.metadata.name -eq 'redis-external' }
        $mysqlAddress = $mysqlService.status.loadBalancer.ingress[0].ip
        $redisAddress = $redisService.status.loadBalancer.ingress[0].ip
        if (!$mysqlAddress -or !$redisAddress) { throw 'Missing external service IP; configure P0 environment variables manually' }
        $pod = WslJson @('get','pod','mysql-0','-n','base','-o','json')
        $entry = $pod.spec.containers[0].env | Where-Object { $_.name -eq 'MYSQL_ROOT_PASSWORD' }
        $password = $entry.value
        if (!$password) {
            $reference = $entry.valueFrom.secretKeyRef
            if (!$reference) { throw 'MySQL root password is not configured' }
            $secret = WslJson @('get','secret',$reference.name,'-n','base','-o','json')
            $password = DecodeSecret $secret $reference.key
        }
        $settings['KUMA_LAB_P0_JDBC_URL'] = "jdbc:mysql://${mysqlAddress}:3306/?useSSL=false&allowPublicKeyRetrieval=true"
        $settings['KUMA_LAB_P0_JDBC_USER'] = 'root'
        $settings['KUMA_LAB_P0_JDBC_PASSWORD'] = $password
        if ($Group -in @('all','redis')) {
            $secret = WslJson @('get','secret','redis-auth','-n','base','-o','json')
            $settings['KUMA_LAB_P0_REDIS_HOST'] = $redisAddress
            $settings['KUMA_LAB_P0_REDIS_PORT'] = '6379'
            $settings['KUMA_LAB_P0_REDIS_PASSWORD'] = DecodeSecret $secret 'password'
        }
    }
    foreach ($name in $settings.Keys) {
        $previous[$name] = [Environment]::GetEnvironmentVariable($name, 'Process')
        [Environment]::SetEnvironmentVariable($name, $settings[$name], 'Process')
    }
    if ($Test) {
        $filter = if ($Group -eq 'threadpool') { 'com.kuma.cloud.lab.p0.ThreadPoolLearningDemoTest' } else { 'com.kuma.cloud.lab.p0.*' }
        $taskArgs = @(':kuma-project:kuma-project-lab:test', '--tests', $filter, '--console=plain')
    } else {
        $tasks = @{mysql='mysqlAdvancedLab';redis='redisAdvancedLab';threadpool='threadPoolLab'}
        $groups = if ($Group -eq 'all') { @('mysql','redis','threadpool') } else { @($Group) }
        $taskArgs = @($groups | ForEach-Object { ':kuma-project:kuma-project-lab:' + $tasks[$_] })
        $taskArgs += @("-Pp0Lesson=$Lesson", '--console=plain')
    }
    & (Join-Path $repoRoot 'gradlew.bat') -p $repoRoot @taskArgs
    if ($LASTEXITCODE -ne 0) { throw "P0 experiments failed (exit $LASTEXITCODE)" }
} finally {
    foreach ($name in $previous.Keys) { [Environment]::SetEnvironmentVariable($name, $previous[$name], 'Process') }
    $settings.Clear()
    $password = $null
    $secret = $null
}
