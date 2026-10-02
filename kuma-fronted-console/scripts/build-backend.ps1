$ErrorActionPreference = 'Stop'
Push-Location (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
try {
    & .\gradlew.bat :kuma-project:kuma-project-console:bootJar --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Backend build failed.' }
} finally { Pop-Location }
