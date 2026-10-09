param(
    [ValidateSet('all', 'loki', 'prometheus', 'alertmanager', 'otel', 'skywalking', 'grafana')]
    [string]$Component = 'all',
    [string]$Distribution = 'Ubuntu-24.04'
)
$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$passwordName = 'KUMA_LAB_MONITORING_GRAFANA_PASSWORD'
$previous = [Environment]::GetEnvironmentVariable($passwordName, 'Process')
try {
    if ($Component -in @('all', 'grafana') -and !$previous) {
        $password = & wsl -d $Distribution -u root -- cat /home/kuma/.config/kuma-observability/grafana-admin-password
        if ($LASTEXITCODE -ne 0) { throw 'Cannot read the existing local Grafana credentials.' }
        [Environment]::SetEnvironmentVariable($passwordName, ($password | Out-String).Trim(), 'Process')
    }
    & (Join-Path $repoRoot 'gradlew.bat') -p $repoRoot :kuma-project:kuma-project-lab:monitoringLab "-PmonitoringComponent=$Component" --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Middleware experiment failed. Check local forwards and service readiness.' }
} finally {
    [Environment]::SetEnvironmentVariable($passwordName, $previous, 'Process')
}
