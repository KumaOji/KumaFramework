$ErrorActionPreference = 'Stop'
$credentialScript = Join-Path $PSScriptRoot 'get-console-credentials.ps1'
$credentialTempRoot = [IO.Path]::GetFullPath([IO.Path]::GetTempPath())
$credentialFixtureDir = Join-Path $credentialTempRoot ('kuma-credential-test-' + [Guid]::NewGuid().ToString('N'))
[IO.Directory]::CreateDirectory($credentialFixtureDir) | Out-Null
$credentialFixture = Join-Path $credentialFixtureDir 'credentials.json'
try {
    . (Join-Path $PSScriptRoot 'credential-project-sources.ps1')
    $projectFixture = Join-Path $credentialFixtureDir 'project'
    $projectNacosFixture = Join-Path $projectFixture 'kuma-project/kuma-project-uaa/src/main/resources/bootstrap.yml'
    $projectUaaFixture = Join-Path $projectFixture '.nacos-tmp/local/base/kuma-cloud-uaa-dev.yaml'
    [IO.Directory]::CreateDirectory((Split-Path $projectNacosFixture -Parent)) | Out-Null
    [IO.Directory]::CreateDirectory((Split-Path $projectUaaFixture -Parent)) | Out-Null
    [IO.File]::WriteAllText($projectNacosFixture, @'
spring:
  cloud:
    nacos:
      username: ${KUMA_CREDENTIAL_FIXTURE_USER:fixture-nacos}
      password: '${KUMA_CREDENTIAL_FIXTURE_PASSWORD:fixture-secret}'
unrelated:
  password: should-not-read
'@)
    [IO.File]::WriteAllText($projectUaaFixture, @'
kuma:
  uaa:
    admin:
      username: fixture-admin
      password: "fixture # retained" # comment
'@)
    $fixtureLogin = Read-CredentialProjectLogin $projectFixture 'nacos'
    if ($fixtureLogin.username -ne 'fixture-nacos' -or $fixtureLogin.password -ne 'fixture-secret') { throw 'Project login defaults failed' }
    $fixtureLogin = Read-CredentialProjectLogin $projectFixture 'uaa'
    if ($fixtureLogin.password -ne 'fixture # retained') { throw 'Quoted password/comment parsing failed' }
    $previousFixturePassword = $env:KUMA_CREDENTIAL_FIXTURE_PASSWORD
    try {
        $env:KUMA_CREDENTIAL_FIXTURE_PASSWORD = 'fixture-environment-secret'
        if ((Read-CredentialProjectLogin $projectFixture 'nacos').password -ne 'fixture-environment-secret') { throw 'Environment override failed' }
    } finally { $env:KUMA_CREDENTIAL_FIXTURE_PASSWORD = $previousFixturePassword }
    if (Read-CredentialProjectLogin (Join-Path $projectFixture 'missing') 'nacos') { throw 'Missing project source was not handled' }
    [IO.File]::WriteAllText($credentialFixture, '{"version":1,"services":{"nacos":{"username":"fixture-user","password":"fixture-password-value"},"extra-ui":{"username":"extra-user","password":"fixture-extra-value"}}}')
    $maskedText = & powershell -NoProfile -ExecutionPolicy Bypass -File $credentialScript -PrivateFile $credentialFixture -Service nacos -AsJson
    if ($LASTEXITCODE -ne 0) { throw 'Masked retrieval failed' }
    $masked = $maskedText | ConvertFrom-Json
    if ($masked.password -ne '********' -or $masked.status -ne 'available' -or ($maskedText -join '').Contains('fixture-password-value')) { throw 'Masking failed' }
    $revealedText = & powershell -NoProfile -ExecutionPolicy Bypass -File $credentialScript -PrivateFile $credentialFixture -Service 'nacos,extra-ui' -Reveal -AsJson
    if ($LASTEXITCODE -ne 0) { throw 'Revealed retrieval failed' }
    $revealed = $revealedText | ConvertFrom-Json
    if ($revealed.Count -ne 2 -or $revealed[0].password -ne 'fixture-password-value' -or $revealed[1].password -ne 'fixture-extra-value') { throw ('Private overrides failed; count=' + $revealed.Count + '; ids=' + ($revealed.service -join ',') + '; statuses=' + ($revealed.status -join ',')) }
    $initializedFile = Join-Path $credentialFixtureDir 'initialized.json'
    $null = & powershell -NoProfile -ExecutionPolicy Bypass -File $credentialScript -PrivateFile $initializedFile -Initialize
    if ($LASTEXITCODE -ne 0) { throw 'Initialization failed' }
    if (!(Get-Acl -LiteralPath $initializedFile).AreAccessRulesProtected) { throw 'Private file inherited ACLs' }
    $oldPreference = $ErrorActionPreference
    try {
        $ErrorActionPreference = 'Continue'
        $forbiddenPath = Join-Path (Split-Path $PSScriptRoot -Parent) '.local\console-credentials.local.json'
        $null = & powershell -NoProfile -ExecutionPolicy Bypass -File $credentialScript -PrivateFile $forbiddenPath -Initialize 2>$null
        $rejectedRepositoryPath = $LASTEXITCODE -ne 0
    } finally { $ErrorActionPreference = $oldPreference }
    if (!$rejectedRepositoryPath -or (Test-Path -LiteralPath $forbiddenPath)) { throw 'Repository path was not rejected' }
    Write-Output 'PASS: project login sources, environment overrides, quoted values, masked retrieval, explicit reveal, private overrides, custom ids, ACLs, repository path rejection.'
} finally {
    $resolvedFixture = [IO.Path]::GetFullPath($credentialFixtureDir)
    $expectedRoot = $credentialTempRoot.TrimEnd('\','/') + [IO.Path]::DirectorySeparatorChar
    if (!$resolvedFixture.StartsWith($expectedRoot, [StringComparison]::OrdinalIgnoreCase) -or !(Split-Path $resolvedFixture -Leaf).StartsWith('kuma-credential-test-')) { throw 'Unsafe test cleanup path' }
    Remove-Item -LiteralPath $resolvedFixture -Recurse -Force
}
