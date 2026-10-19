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
$mysqlSecretJson = Invoke-ReferenceWsl -Arguments @('-d', $Distribution, '-u', 'root', '--exec', 'kubectl', 'get', 'secret', 'kuma-reference-mysql', '-n', 'base', '-o', 'json')
$mysqlSecretData = ($mysqlSecretJson -join "`n" | ConvertFrom-Json).data
$env:REFERENCE_MYSQL_HOST = Invoke-ReferenceWsl -Arguments @('-d', $Distribution, '-u', 'root', '--exec', 'kubectl', 'get', 'service', 'mysql-external', '-n', 'base', '-o', 'jsonpath={.status.loadBalancer.ingress[0].ip}')
if (-not $env:REFERENCE_MYSQL_HOST) { throw 'Cannot detect WSL MySQL service address.' }
$env:REFERENCE_MYSQL_PORT = '3306'
$env:REFERENCE_MYSQL_DATABASE = [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($mysqlSecretData.database))
$env:REFERENCE_MYSQL_USERNAME = [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($mysqlSecretData.username))
$env:REFERENCE_MYSQL_PASSWORD = [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($mysqlSecretData.password))
$mysqlSecretData = $null
$mysqlSecretJson = $null
$secretData = $null
$secretJson = $null
