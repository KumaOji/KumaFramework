$ErrorActionPreference = 'Stop'
$credentialScript = Join-Path $PSScriptRoot 'get-console-credentials.ps1'
$credentialTempRoot = [IO.Path]::GetFullPath([IO.Path]::GetTempPath())
$credentialFixtureDir = Join-Path $credentialTempRoot ('kuma-credential-test-' + [Guid]::NewGuid().ToString('N'))
[IO.Directory]::CreateDirectory($credentialFixtureDir) | Out-Null
$credentialFixture = Join-Path $credentialFixtureDir 'credentials.json'
try {
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
    Write-Output 'PASS: masked retrieval, explicit reveal, private overrides, custom service ids, restricted ACLs, repository path rejection.'
} finally {
    $resolvedFixture = [IO.Path]::GetFullPath($credentialFixtureDir)
    $expectedRoot = $credentialTempRoot.TrimEnd('\','/') + [IO.Path]::DirectorySeparatorChar
    if (!$resolvedFixture.StartsWith($expectedRoot, [StringComparison]::OrdinalIgnoreCase) -or !(Split-Path $resolvedFixture -Leaf).StartsWith('kuma-credential-test-')) { throw 'Unsafe test cleanup path' }
    Remove-Item -LiteralPath $resolvedFixture -Recurse -Force
}
