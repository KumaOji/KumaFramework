param([ValidateSet('run', 'test')][string]$Action = 'run', [string]$Distribution = 'Ubuntu-24.04')
$ErrorActionPreference = 'Stop'
$workspace = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..\..\..')).Path
$names = @('REFERENCE_PG_HOST', 'REFERENCE_PG_PORT', 'REFERENCE_PG_DATABASE', 'REFERENCE_PG_USERNAME', 'REFERENCE_PG_PASSWORD',
    'REFERENCE_MYSQL_HOST', 'REFERENCE_MYSQL_PORT', 'REFERENCE_MYSQL_DATABASE', 'REFERENCE_MYSQL_USERNAME', 'REFERENCE_MYSQL_PASSWORD')
$previous = @{}
foreach ($name in $names) { $previous[$name] = [Environment]::GetEnvironmentVariable($name, 'Process') }
Push-Location $workspace
try {
    . (Join-Path $PSScriptRoot 'load-wsl-environment.ps1') -Distribution $Distribution
    if ($Action -eq 'test') {
        & .\gradlew.bat :kuma-project:kuma-project-reference:wslTest --console=plain
    } else {
        & .\gradlew.bat :kuma-project:kuma-project-reference:bootRun --console=plain
    }
    if ($LASTEXITCODE -ne 0) { throw "Reference $Action failed." }
} finally {
    foreach ($name in $names) { [Environment]::SetEnvironmentVariable($name, $previous[$name], 'Process') }
    Pop-Location
}
