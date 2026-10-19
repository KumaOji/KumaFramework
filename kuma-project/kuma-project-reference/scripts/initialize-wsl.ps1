param([string]$Distribution = 'Ubuntu-24.04')
$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'wsl-helper.ps1')
$scriptPath = Join-Path $PSScriptRoot 'bootstrap-wsl-postgres.py'
$wslPath = Invoke-ReferenceWsl -Arguments @('-d', $Distribution, '--exec', 'wslpath', '-u', $scriptPath)
Invoke-ReferenceWsl -Arguments @('-d', $Distribution, '-u', 'root', '--exec', 'python3', $wslPath)
