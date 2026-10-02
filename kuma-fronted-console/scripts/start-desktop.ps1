$ErrorActionPreference = 'Stop'
$frontendRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Push-Location $frontendRoot
try {
    if (-not (Test-Path -LiteralPath 'node_modules\electron\dist\electron.exe')) {
        & npm.cmd ci --no-audit --no-fund
        if ($LASTEXITCODE -ne 0) { throw 'Desktop dependencies installation failed.' }
        & node node_modules/electron/install.js
        if ($LASTEXITCODE -ne 0) { throw 'Electron runtime installation failed.' }
    }
    $env:ELECTRON_RUN_AS_NODE = $null
    Start-Process -FilePath (Join-Path $frontendRoot 'node_modules\electron\dist\electron.exe') -ArgumentList ('"' + $frontendRoot + '"') -WorkingDirectory $frontendRoot
} finally { Pop-Location }
