# Reads the simple nested scalar mappings used by this project's login settings.
# Returns data only; never executes configuration text or prints source contents.
function Read-CredentialProjectScalar([string]$Path, [string]$Key) {
    if (!(Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
    $parents = @()
    foreach ($line in [IO.File]::ReadAllLines($Path)) {
        if ($line -notmatch '^(\s*)([A-Za-z0-9_-]+):\s*(.*)$') { continue }
        $depth = $Matches[1].Length
        $name = $Matches[2]
        $value = $Matches[3].Trim()
        $parents = @($parents | Where-Object { $_.depth -lt $depth })
        $parents += [pscustomobject]@{ depth=$depth; name=$name }
        if (($parents.name -join '.') -ne $Key) { continue }
        if ($value -match '^"(.*)"(?:\s+#.*)?$') {
            $value = ('"' + $Matches[1] + '"') | ConvertFrom-Json
        } elseif ($value -match "^'(.*)'(?:\s+#.*)?$") {
            $value = $Matches[1].Replace("''", "'")
        } else { $value = ($value -replace '\s+#.*$', '').Trim() }
        if ($value -match '^\$\{([A-Za-z_][A-Za-z0-9_]*)(?::(.*))?\}$') {
            $variable = $Matches[1]
            $fallback = $Matches[2]
            $configured = [Environment]::GetEnvironmentVariable($variable)
            $value = if ($configured) { $configured } else { $fallback }
        }
        if (!$value -or $value -in @('null','~','|','>') -or $value.Contains('${')) { return $null }
        return $value
    }
    return $null
}

function Read-CredentialProjectLogin([string]$Workspace, [string]$Service) {
    $sources = switch ($Service) {
        'nacos' { @(@{path='kuma-project/kuma-project-uaa/src/main/resources/bootstrap.yml'; prefix='spring.cloud.nacos'}) }
        'uaa' { @(
            @{path='.nacos-tmp/base/kuma-cloud-uaa-dev.yaml'; prefix='kuma.uaa.admin'},
            @{path='kuma-project/kuma-project-uaa/src/main/resources/application-dev.yml'; prefix='kuma.uaa.admin'}
        ) }
        default { @() }
    }
    foreach ($source in $sources) {
        $path = Join-Path $Workspace $source.path
        $username = Read-CredentialProjectScalar $path ($source.prefix + '.username')
        $password = Read-CredentialProjectScalar $path ($source.prefix + '.password')
        if ($username -and $password) { return [pscustomobject]@{ username=$username; password=$password; source=$source.path } }
    }
    return $null
}
