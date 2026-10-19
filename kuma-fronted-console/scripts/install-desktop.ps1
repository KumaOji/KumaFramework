param([switch]$DisableAutoStart)

$ErrorActionPreference = 'Stop'
$frontendRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$electronPath = Join-Path $frontendRoot 'node_modules\electron\dist\electron.exe'
if (-not (Test-Path -LiteralPath $electronPath)) {
    throw 'Electron is missing. Run pnpm install --frozen-lockfile first.'
}
Push-Location $frontendRoot
try {
    $electronPath = & node -p "require('electron')"
    if ($LASTEXITCODE -ne 0) { throw 'Cannot resolve Electron executable.' }
} finally { Pop-Location }

$desktopPath = [Environment]::GetFolderPath('DesktopDirectory')
$shortcutPath = Join-Path $desktopPath 'Kuma Console.lnk'
$wshShell = New-Object -ComObject WScript.Shell
$shortcut = $wshShell.CreateShortcut($shortcutPath)
$shortcut.TargetPath = $electronPath
$shortcut.Arguments = '"' + $frontendRoot + '"'
$shortcut.WorkingDirectory = $frontendRoot
$shortcut.Description = 'Kuma Console'
$shortcut.IconLocation = (Join-Path $frontendRoot 'renderer\assets\kuma-console.ico') + ',0'
$shortcut.Save()

$runPath = 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Run'
$startupName = 'io.github.kumaoji.console'
if ($DisableAutoStart) {
    Remove-ItemProperty -LiteralPath $runPath -Name $startupName -ErrorAction SilentlyContinue
} else {
    if (-not (Test-Path -LiteralPath $runPath)) { New-Item -Path $runPath | Out-Null }
    New-ItemProperty -LiteralPath $runPath -Name $startupName -PropertyType String -Value ('"' + $electronPath + '" "' + $frontendRoot + '"') -Force | Out-Null
    # A prior Task Manager disable must not override the requested enable.
    $approvedPath = 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Explorer\StartupApproved\Run'
    if (Test-Path -LiteralPath $approvedPath) {
        Remove-ItemProperty -LiteralPath $approvedPath -Name $startupName -ErrorAction SilentlyContinue
    }
}
Write-Output "Desktop shortcut: $shortcutPath"
Write-Output "Open at login: $(-not $DisableAutoStart)"
