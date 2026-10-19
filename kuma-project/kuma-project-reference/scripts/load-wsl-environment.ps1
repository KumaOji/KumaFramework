param([string]$Distribution = 'Ubuntu-24.04')
$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'wsl-helper.ps1')
$secretJson = Invoke-ReferenceWsl -Arguments @('-d', $Distribution, '-u', 'root', '--exec', 'kubectl', 'get', 'secret', 'kuma-reference-db', '-n', 'base', '-o', 'json')
$secretData = ($secretJson -join "`n" | ConvertFrom-Json).data
function Read-ReferenceSecret([string]$Name) {
    [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($secretData.$Name))
}
$env:REFERENCE_PG_HOST = (Invoke-ReferenceWsl -Arguments @('-d', $Distribution, '--exec', 'hostname', '-I')).Split(' ')[0]
if (-not $env:REFERENCE_PG_HOST) { throw 'Cannot detect WSL IP.' }
$env:REFERENCE_PG_PORT = '5432'
$env:REFERENCE_PG_DATABASE = Read-ReferenceSecret 'database'
$env:REFERENCE_PG_USERNAME = Read-ReferenceSecret 'username'
$env:REFERENCE_PG_PASSWORD = Read-ReferenceSecret 'password'
$secretData = $null
$secretJson = $null
