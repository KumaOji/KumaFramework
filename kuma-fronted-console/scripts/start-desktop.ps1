$ErrorActionPreference = 'Stop'
$frontendRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Push-Location $frontendRoot
try {
    if (-not (Test-Path -LiteralPath 'node_modules\electron\dist\electron.exe')) {
        if (-not (Get-Command pnpm.cmd -ErrorAction SilentlyContinue)) {
            throw 'pnpm is required. Install pnpm, then run start-console.cmd again.'
        }
        & pnpm.cmd install --frozen-lockfile
        if ($LASTEXITCODE -ne 0) { throw 'Desktop dependencies installation failed.' }
    }
    $env:ELECTRON_RUN_AS_NODE = $null
    Start-Process -FilePath (Join-Path $frontendRoot 'node_modules\electron\dist\electron.exe') -ArgumentList ('"' + $frontendRoot + '"') -WorkingDirectory $frontendRoot
} finally { Pop-Location }
