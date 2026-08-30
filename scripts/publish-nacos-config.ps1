# 将 .nacos-tmp/base 下的配置发布到 Nacos
# 用法: .\scripts\publish-nacos-config.ps1 [-Namespace base] [-Server 172.23.89.45:8848]

param(
    [string]$Namespace = "base",
    [string]$Server = "172.23.89.45:8848",
    [string]$Username = "nacos",
    [string]$Password = "123456",
    [string]$Group = "DEFAULT_GROUP"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$configDir = Join-Path $root ".nacos-tmp\base"

if (-not (Test-Path $configDir)) {
    throw "Config directory not found: $configDir"
}

# 登录获取 accessToken
$loginBody = "username=$Username&password=$Password"
$loginResp = Invoke-RestMethod -Method Post -Uri "http://$Server/nacos/v1/auth/login" -Body $loginBody -ContentType "application/x-www-form-urlencoded"
$token = $loginResp.accessToken
if (-not $token) {
    throw "Nacos login failed: $($loginResp | ConvertTo-Json -Compress)"
}

# 查询 namespace id
$nsList = Invoke-RestMethod -Method Get -Uri "http://$Server/nacos/v1/console/namespaces" -Headers @{ accessToken = $token }
$tenant = $null
foreach ($ns in $nsList.data) {
    if ($ns.namespaceShowName -eq $Namespace -or $ns.namespace -eq $Namespace) {
        $tenant = $ns.namespace
        break
    }
}
if (-not $tenant) {
    Write-Warning "Namespace '$Namespace' not found, using public namespace"
    $tenant = ""
}

$files = Get-ChildItem -Path $configDir -Filter "*.yaml"
foreach ($file in $files) {
    $dataId = $file.Name
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
    $body = @{
        dataId  = $dataId
        group   = $Group
        content = $content
        type    = "yaml"
        tenant  = $tenant
    }
    $result = Invoke-RestMethod -Method Post -Uri "http://$Server/nacos/v1/cs/configs" -Headers @{ accessToken = $token } -Body $body
    if ($result -eq "true") {
        Write-Host "[OK] Published $dataId (tenant=$tenant)"
    } else {
        throw "Failed to publish $dataId : $result"
    }
}

Write-Host "All configs published to Nacos $Server namespace=$Namespace"
