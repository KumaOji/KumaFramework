[CmdletBinding()]
param(
    [string[]]$Service = @('all'),
    [switch]$Reveal,
    [switch]$AsJson,
    [switch]$Initialize,
    [string]$Distribution = 'Ubuntu-24.04',
    [string]$PrivateFile = (Join-Path ([Environment]::GetFolderPath('UserProfile')) '.kuma-console\credentials.json')
)

$ErrorActionPreference = 'Stop'
$credentialWorkspace = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
. (Join-Path $PSScriptRoot 'credential-project-sources.ps1')
$PrivateFile = [IO.Path]::GetFullPath($PrivateFile)
if ($PrivateFile.StartsWith($credentialWorkspace + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) {
    throw 'The private credential file must be outside this repository.'
}
$credentialParent = Split-Path -Parent $PrivateFile
$existingParent = $credentialParent
while (!(Test-Path -LiteralPath $existingParent)) { $existingParent = Split-Path -Parent $existingParent }
$gitPreference = $ErrorActionPreference
try {
    $ErrorActionPreference = 'Continue'
    $gitRoot = & git -C $existingParent rev-parse --show-toplevel 2>$null
    $gitExit = $LASTEXITCODE
} finally { $ErrorActionPreference = $gitPreference }
if ($gitExit -eq 0 -and $gitRoot) { throw 'The private credential file must be outside every Git working tree.' }

function Protect-PrivateFile([string]$Path) {
    if ($env:OS -eq 'Windows_NT') {
        $credentialSid = [Security.Principal.WindowsIdentity]::GetCurrent().User
        $credentialAcl = New-Object Security.AccessControl.FileSecurity
        $credentialAcl.SetOwner($credentialSid)
        $credentialAcl.SetAccessRuleProtection($true, $false)
        $credentialAcl.AddAccessRule((New-Object Security.AccessControl.FileSystemAccessRule($credentialSid, 'FullControl', 'Allow')))
        $credentialSystem = New-Object Security.Principal.SecurityIdentifier('S-1-5-18')
        $credentialAcl.AddAccessRule((New-Object Security.AccessControl.FileSystemAccessRule($credentialSystem, 'FullControl', 'Allow')))
        Set-Acl -LiteralPath $Path -AclObject $credentialAcl
    }
}

if ($Initialize) {
    if (!(Test-Path -LiteralPath $PrivateFile)) {
        [IO.Directory]::CreateDirectory($credentialParent) | Out-Null
        [IO.File]::WriteAllText($PrivateFile, '', [Text.UTF8Encoding]::new($false))
        Protect-PrivateFile $PrivateFile
        [IO.File]::WriteAllText($PrivateFile, "{`n  `"version`": 1,`n  `"services`": {}`n}`n", [Text.UTF8Encoding]::new($false))
    } else { Protect-PrivateFile $PrivateFile }
    [pscustomobject]@{ privateFile = $PrivateFile; outsideGit = $true }
    return
}

$privateServices = $null
if (Test-Path -LiteralPath $PrivateFile) {
    try {
        $privateRegistry = Get-Content -LiteralPath $PrivateFile -Raw -Encoding UTF8 | ConvertFrom-Json
        if ($privateRegistry.version -ne 1) { throw 'Unsupported version' }
        $privateServices = $privateRegistry.services
    } catch { throw 'Invalid private credential file. Expected version 1 and a services object; contents were not printed.' }
}

function Invoke-CredentialWsl([string[]]$Arguments) {
    $previousPreference = $ErrorActionPreference
    try {
        # Capture stdout only. Never print command output or upstream errors on failure.
        $ErrorActionPreference = 'Continue'
        $captured = @(& wsl.exe @Arguments 2>$null)
        $nativeExit = $LASTEXITCODE
    } finally { $ErrorActionPreference = $previousPreference }
    if ($nativeExit -ne 0) { throw 'Credential source unavailable.' }
    return (($captured -join "`n").Replace([string][char]0, ''))
}

$wslChecked = $false
$wslAvailable = $false
$secretCache = @{}
function Read-CredentialWsl([string[]]$Arguments) {
    if (!$script:wslChecked) {
        $running = Invoke-CredentialWsl @('--list', '--running', '--quiet')
        $script:wslAvailable = @($running -split "`n" | ForEach-Object { $_.Trim() }) -contains $Distribution
        $script:wslChecked = $true
    }
    if (!$script:wslAvailable) { throw 'WSL is not running; no distribution was started.' }
    return Invoke-CredentialWsl (@('--distribution', $Distribution, '--user', 'root', '--exec') + $Arguments)
}

function Read-SecretField([string]$Name, [string]$Field) {
    if (!$secretCache.ContainsKey($Name)) {
        $raw = Read-CredentialWsl @('k3s', 'kubectl', '--request-timeout=5s', '-n', 'base', 'get', 'secret', $Name, '-o', 'json')
        $secretCache[$Name] = ($raw | ConvertFrom-Json).data
    }
    $encoded = $secretCache[$Name].PSObject.Properties[$Field].Value
    if (!$encoded) { throw 'Credential field unavailable.' }
    return [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($encoded))
}

# These are source locations and field names, never credential values.
$catalog = @(
    @{ id='grafana'; name='Grafana'; kind='wsl-text'; username='admin'; path='/home/kuma/.config/kuma-observability/grafana-admin-password' },
    @{ id='qinglong'; name='Life / QingLong'; kind='wsl-json'; path='/home/kuma/.config/life-deals/qinglong.json' },
    @{ id='nacos'; name='Nacos'; kind='project-login'; note='读取项目的 Nacos 登录配置；不使用服务端 token 或数据库密码。' },
    @{ id='n8n'; name='n8n / owner login'; kind='manual'; note='Owner login is application-managed; a password hash cannot be recovered. Add the known login to the private file.' },
    @{ id='n8n-basic'; name='n8n / configured Basic Auth'; kind='k8s'; hiddenDefault=$true; secret='n8n-auth'; usernameKey='basic-auth-user'; passwordKey='basic-auth-password'; note='Configured Basic Auth values; these may differ from the application owner login.' },
    @{ id='qdrant'; name='Qdrant'; kind='k8s'; secret='qdrant-auth'; apiKeyKey='api-key' },
    @{ id='dsh'; name='DeepSeek Harness'; kind='launch-link'; note='Uses the current process login URL, not a username/password or model API key.' },
    @{ id='uaa'; name='UAA / Blog 登录'; kind='project-login'; note='读取项目管理员初始化配置。Blog 使用 UAA 登录；账号改过密码后，初始配置可能不再有效。' },
    @{ id='blog'; name='Blog'; kind='manual'; hiddenDefault=$true; note='Blog uses UAA login. A separately saved Blog account can still be requested explicitly.' },
    @{ id='mysql'; name='MySQL'; kind='k8s'; secret='mysql-auth'; username='root'; passwordKey='root-password' },
    @{ id='postgresql'; name='PostgreSQL'; kind='k8s'; secret='postgresql-auth'; usernameKey='username'; passwordKey='password' },
    @{ id='redis'; name='Redis'; kind='k8s'; secret='redis-auth'; username='default'; passwordKey='password' }
)
foreach ($item in @(
    @('prometheus','Prometheus'), @('alertmanager','Alertmanager'), @('skywalking','SkyWalking'),
    @('rsshub','Life / RSSHub'), @('gateway','Gateway'), @('lab','Lab'), @('loki','Loki'),
    @('zipkin','Zipkin API'), @('otlp','OTLP HTTP'), @('otlp-grpc','OTLP gRPC'),
    @('skywalking-agent','SkyWalking Agent'), @('life-redis','Life / Redis'), @('kafka','Kafka')
)) {
    $catalog += @{ id=$item[0]; name=$item[1]; kind='none'; hiddenDefault=$true; note='No independent panel login is recorded here; downstream APIs may still require application authorization.' }
}
$catalog += @{ id='qdrant-grpc'; name='Qdrant gRPC'; kind='k8s'; hiddenDefault=$true; secret='qdrant-auth'; apiKeyKey='api-key' }
if ($privateServices) {
    foreach ($property in $privateServices.PSObject.Properties) {
        if ($catalog.id -notcontains $property.Name) { $catalog += @{ id=$property.Name; name=$property.Name; kind='manual' } }
    }
}
$requested = @($Service | ForEach-Object { $_ -split ',' } | ForEach-Object { $_.Trim().ToLowerInvariant() })
foreach ($id in $requested) { if ($id -ne 'all' -and $catalog.id -notcontains $id) { throw "Unknown service id: $id" } }

$results = foreach ($entry in $catalog) {
    if ($requested -notcontains 'all' -and $requested -notcontains $entry.id) { continue }
    if ($requested -contains 'all' -and $entry.hiddenDefault -and !($privateServices -and $privateServices.PSObject.Properties[$entry.id])) { continue }
    $result = [ordered]@{ service=$entry.id; name=$entry.name; status='unavailable'; username=''; password=''; apiKey=''; loginUrl=''; source=$entry.kind; note=[string]$entry.note }
    $override = if ($privateServices) { $privateServices.PSObject.Properties[$entry.id].Value } else { $null }
    try {
        if ($override) {
            foreach ($field in @('username','password','apiKey','loginUrl')) { if ($override.$field) { $result[$field] = [string]$override.$field } }
            $result.source = $PrivateFile
        } else {
            switch ($entry.kind) {
                'wsl-text' { $result.username=$entry.username; $result.password=(Read-CredentialWsl @('cat','--',$entry.path)).TrimEnd("`r","`n") }
                'wsl-json' {
                    $value = Read-CredentialWsl @('cat','--',$entry.path) | ConvertFrom-Json
                    $result.username=[string]$value.username; $result.password=[string]$value.password
                }
                'k8s' {
                    if ($entry.usernameKey) { $result.username=Read-SecretField $entry.secret $entry.usernameKey } else { $result.username=[string]$entry.username }
                    if ($entry.passwordKey) { $result.password=Read-SecretField $entry.secret $entry.passwordKey }
                    if ($entry.apiKeyKey) { $result.apiKey=Read-SecretField $entry.secret $entry.apiKeyKey }
                }
                'environment' { $result.username=$entry.username; $result.password=[Environment]::GetEnvironmentVariable($entry.variable) }
                'project-login' {
                    $login = Read-CredentialProjectLogin $credentialWorkspace $entry.id
                    if ($login) {
                        $result.username=$login.username; $result.password=$login.password; $result.source=$login.source
                    } elseif ($entry.id -eq 'uaa' -and $env:UAA_ADMIN_PASSWORD) {
                        $result.username=if ($env:UAA_ADMIN_USERNAME) { $env:UAA_ADMIN_USERNAME } else { 'admin' }
                        $result.password=$env:UAA_ADMIN_PASSWORD; $result.source='environment'
                    } else { $result.status='manual-required' }
                }
                'launch-link' {
                    $log = Join-Path $credentialWorkspace 'build\dsh-runtime\dsh.log'
                    $text = Get-Content -LiteralPath $log -Raw -Encoding UTF8
                    $links = [regex]::Matches($text, 'http://(?:127\.0\.0\.1|localhost):[0-9]+/\?token=[A-Za-z0-9_-]+')
                    if ($links.Count) { $result.loginUrl=$links[$links.Count-1].Value }
                    $result.note += ' A stopped/restarted process can invalidate this link.'
                }
                'manual' { $result.status='manual-required' }
                'none' { $result.status='no-stored-login' }
            }
        }
        if ($result.password -or $result.apiKey -or $result.loginUrl) { $result.status='available' }
    } catch { $result.status='unavailable'; $result.note='Could not read this source. No raw source output was printed.' }
    if (!$Reveal) { foreach ($field in @('password','apiKey','loginUrl')) { if ($result[$field]) { $result[$field]='********' } } }
    [pscustomobject]$result
}
if ($AsJson) { ConvertTo-Json -InputObject @($results) -Depth 5 } else { $results }
