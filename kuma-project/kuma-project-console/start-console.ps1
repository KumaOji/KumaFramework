$ErrorActionPreference = 'Stop'
$consoleRoot = $PSScriptRoot
$workspaceRoot = (Resolve-Path (Join-Path $consoleRoot '..\..')).Path
$consolePort = if ($env:KUMA_CONSOLE_PORT) { [int]$env:KUMA_CONSOLE_PORT } else { 18090 }
$consoleUrl = "http://127.0.0.1:$consolePort"

function Open-ConsoleWindow {
    foreach ($browserRoot in @($env:ProgramFiles, ${env:ProgramFiles(x86)}, $env:LOCALAPPDATA)) {
        if (-not $browserRoot) { continue }
        foreach ($browserRelativePath in @('Microsoft\Edge\Application\msedge.exe', 'Google\Chrome\Application\chrome.exe')) {
            $browserPath = Join-Path $browserRoot $browserRelativePath
            if (Test-Path -LiteralPath $browserPath) {
                Start-Process -FilePath $browserPath -ArgumentList @("--app=$consoleUrl", '--new-window')
                return
            }
        }
    }
    Start-Process $consoleUrl
}

try {
    $identity = Invoke-RestMethod "$consoleUrl/api/identity" -TimeoutSec 2
    if ($identity.application -eq 'kuma-local-console') {
        if ($env:KUMA_CONSOLE_OPEN_BROWSER -ne 'false') { Open-ConsoleWindow }
        Write-Host "Kuma Console already running: $consoleUrl"
        exit 0
    }
} catch { }

Push-Location $workspaceRoot
try {
    & .\gradlew.bat :kuma-project:kuma-project-console:bootJar --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Console build failed.' }
    $javaPath = (Get-Command java -ErrorAction Stop).Source
    $jarPath = Join-Path $consoleRoot 'build\libs\kuma-console.jar'
    $stdoutPath = Join-Path $consoleRoot 'build\console-stdout.log'
    $stderrPath = Join-Path $consoleRoot 'build\console-stderr.log'
    # Browser launch is handled once below, after the HTTP endpoint is ready.
    $process = Start-Process -FilePath $javaPath -ArgumentList @('--enable-preview', '--enable-native-access=ALL-UNNAMED',
        '-jar', ('"' + $jarPath + '"'), '--console.open-browser=false') -WorkingDirectory $workspaceRoot -WindowStyle Hidden `
        -RedirectStandardOutput $stdoutPath -RedirectStandardError $stderrPath -PassThru
    $process.Id | Set-Content -LiteralPath (Join-Path $consoleRoot 'build\console.pid')
    for ($attempt = 0; $attempt -lt 60; $attempt++) {
        if ($process.HasExited) { throw "Console exited. Check $stdoutPath and $stderrPath" }
        try {
            $identity = Invoke-RestMethod "$consoleUrl/api/identity" -TimeoutSec 1
            if ($identity.application -eq 'kuma-local-console') {
                if ($env:KUMA_CONSOLE_OPEN_BROWSER -ne 'false') { Open-ConsoleWindow }
                Write-Host "Kuma Console: $consoleUrl"
                exit 0
            }
        } catch { }
        Start-Sleep -Milliseconds 250
    }
    throw "Console did not become ready. Check $stdoutPath"
} finally { Pop-Location }
