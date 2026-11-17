param(
    [ValidateSet('database', 'connectionpool', 'postgres', 'all')][string]$Group = 'database',
    [ValidateSet('all','operations','transactions','crud','binding','batch','queries','constraints','boundaries','transfer','savepoint','isolation','optimistic','locking','spring','reuse','timeout','transaction','capacity','mvcc','phantom','row-locks','table-locks','lock-monitor','serializable')][string]$Lesson = 'all',
    [string]$Distribution = 'Ubuntu-24.04',
    [switch]$PrepareOnly
)
$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$choices = @{
    database = @('all','operations','transactions','crud','binding','batch','queries','constraints','boundaries','transfer','savepoint','isolation','optimistic','locking','spring')
    connectionpool = @('all','reuse','timeout','transaction','capacity')
    postgres = @('all','mvcc','phantom','row-locks','table-locks','lock-monitor','serializable')
}
if ($Group -eq 'all' -and $Lesson -ne 'all') { throw '-Group all requires -Lesson all' }
if ($Group -ne 'all' -and $Lesson -notin $choices[$Group]) { throw "Lesson $Lesson does not belong to $Group" }
function Invoke-LabWsl([string[]]$Command) {
    # WSL emits an unrelated localhost-proxy warning on stderr even on success.
    # Windows PowerShell must use the native exit status, including when callers redirect logs.
    $previousPreference = $ErrorActionPreference
    try {
        $ErrorActionPreference = 'Continue'
        $output = & wsl @Command 2>$null
        $nativeExit = $LASTEXITCODE
    } finally { $ErrorActionPreference = $previousPreference }
    if ($nativeExit -ne 0) { throw "WSL PostgreSQL command failed (exit $nativeExit); check the selected distribution, pod and credential files" }
    return $output
}
function WslJson([string[]]$Command) {
    $output = Invoke-LabWsl -Command (@('-d',$Distribution,'-u','root','--','kubectl') + $Command)
    return ($output | Out-String | ConvertFrom-Json)
}
function PodFile([string]$Path) {
    $output = Invoke-LabWsl -Command @('-d',$Distribution,'-u','root','--','kubectl','exec','-n','base','postgresql-0','--','cat',$Path)
    return ($output | Out-String).TrimEnd("`r", "`n")
}
$pod = WslJson @('get','pod','postgresql-0','-n','base','-o','json')
$service = WslJson @('get','service','postgresql-external','-n','base','-o','json')
$hostAddress = $service.status.loadBalancer.ingress[0].ip
if (!$hostAddress) { throw 'PostgreSQL external service has no reachable IP' }
$files = @{}
foreach ($entry in $pod.spec.containers[0].env) { $files[$entry.name] = $entry.value }
foreach ($name in @('POSTGRES_USER_FILE','POSTGRES_PASSWORD_FILE')) {
    if (!$files[$name]) { throw "Missing PostgreSQL file-based setting: $name" }
}
# Only fixed database kuma_lab is provisioned; no global settings, existing databases or roles are changed.
$provision = [IO.File]::ReadAllText((Join-Path $PSScriptRoot 'prepare-lab-postgresql.sh')).Replace("`r", '')
$encodedProvision = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($provision))
Invoke-LabWsl -Command @('-d',$Distribution,'-u','root','--','kubectl','exec','-n','base','postgresql-0','--','sh','-c',"echo $encodedProvision | base64 -d | sh")
if ($PrepareOnly) { return }
$settings = @{
    KUMA_LAB_DATABASE_JDBC_URL = "jdbc:postgresql://${hostAddress}:5432/kuma_lab"
    KUMA_LAB_DATABASE_JDBC_USER = (PodFile $files['POSTGRES_USER_FILE'])
    KUMA_LAB_DATABASE_JDBC_PASSWORD = (PodFile $files['POSTGRES_PASSWORD_FILE'])
}
$previous = @{}
try {
    foreach ($name in $settings.Keys) {
        $previous[$name] = [Environment]::GetEnvironmentVariable($name, 'Process')
        [Environment]::SetEnvironmentVariable($name, $settings[$name], 'Process')
    }
    $taskArgs = @('-p', $repoRoot, '--console=plain', '-PdatabaseEngine=postgresql')
    if ($Group -in @('database','all')) {
        $taskArgs += @(':kuma-project:kuma-project-lab:databaseLab', "-PdatabaseLesson=$Lesson")
    }
    if ($Group -in @('connectionpool','all')) {
        $taskArgs += @(':kuma-project:kuma-project-lab:javaConnectionPoolLab', "-PconnectionPoolLesson=$Lesson")
    }
    if ($Group -in @('postgres','all')) {
        $taskArgs += @(':kuma-project:kuma-project-lab:postgresConcurrencyLab', "-PpostgresLesson=$Lesson")
    }
    & (Join-Path $repoRoot 'gradlew.bat') @taskArgs
    if ($LASTEXITCODE -ne 0) { throw "PostgreSQL experiments failed (exit $LASTEXITCODE)" }
} finally {
    foreach ($name in $previous.Keys) { [Environment]::SetEnvironmentVariable($name, $previous[$name], 'Process') }
    $settings.Clear()
}
