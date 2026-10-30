function Invoke-KumaServiceBuild {
    param(
        [Parameter(Mandatory)][string]$RepoRoot,
        [Parameter(Mandatory)][scriptblock]$Build,
        [int]$TimeoutSeconds = 600
    )
    # Independent service launchers share Gradle task outputs in this checkout.
    $cacheDirectory = Join-Path $RepoRoot 'build/startup-cache'
    New-Item -ItemType Directory -Path $cacheDirectory -Force | Out-Null
    $lockPath = Join-Path $cacheDirectory 'service-build.lock'
    $buildHandle = $null
    $elapsed = [Diagnostics.Stopwatch]::StartNew()
    $reportedWaiting = $false
    while ($null -eq $buildHandle) {
        try {
            $buildHandle = [IO.File]::Open($lockPath, [IO.FileMode]::OpenOrCreate, [IO.FileAccess]::ReadWrite, [IO.FileShare]::None)
        } catch [IO.IOException] {
            if ($elapsed.Elapsed.TotalSeconds -ge $TimeoutSeconds) { throw 'Timed out waiting for another service build to finish.' }
            if (!$reportedWaiting) { Write-Host 'Waiting for another service build to finish...'; $reportedWaiting = $true }
            Start-Sleep -Milliseconds 250
        }
    }
    try { & $Build } finally { $buildHandle.Dispose() }
}
