$ErrorActionPreference = 'Stop'
$pidPath = Join-Path $PSScriptRoot 'build\console.pid'
if (-not (Test-Path -LiteralPath $pidPath)) { Write-Host 'No launcher-managed Console process found.'; exit 0 }
$consoleProcessId = [int](Get-Content -LiteralPath $pidPath)
$process = Get-CimInstance Win32_Process -Filter "ProcessId=$consoleProcessId"
$expectedJar = (Join-Path $PSScriptRoot 'build\libs\kuma-console.jar')
$runtimeJarRoot = (Join-Path $PSScriptRoot 'build\runtime\kuma-console-')
if ($process -and ($process.CommandLine.Contains($expectedJar) -or $process.CommandLine.Contains($runtimeJarRoot)) -and $process.Name -match '^java(w)?\.exe$') {
    Stop-Process -Id $consoleProcessId
    Write-Host 'Kuma Console stopped.'
} elseif ($process) { throw 'PID belongs to a different process. It was not stopped.' }
Remove-Item -LiteralPath $pidPath
