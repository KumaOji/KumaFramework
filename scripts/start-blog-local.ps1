param(
    [string]$Distribution = 'Ubuntu-24.04',
    [int]$DebugPort = 5005,
    [switch]$Build,
    [switch]$Restart,
    [switch]$WithoutObservability
)
& (Join-Path $PSScriptRoot 'start-services-local.ps1') -Services blog -Distribution $Distribution `
    -BlogDebugPort $DebugPort -Build:$Build -Restart:$Restart -WithoutObservability:$WithoutObservability
