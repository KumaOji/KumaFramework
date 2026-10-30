$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$helper = Join-Path $PSScriptRoot 'service-build.ps1'
$testRoot = Join-Path $repoRoot ('build/service-build-test/' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $testRoot -Force | Out-Null
$jobs = @(1..2 | ForEach-Object {
    Start-Job -ArgumentList $helper, $testRoot, $_ -ScriptBlock {
        param($Helper, $Root, $Number)
        $ErrorActionPreference = 'Stop'
        . $Helper
        Invoke-KumaServiceBuild -RepoRoot $Root -Build {
            Add-Content -LiteralPath (Join-Path $Root 'events.log') -Value "enter:$Number"
            Start-Sleep -Milliseconds 600
            Add-Content -LiteralPath (Join-Path $Root 'events.log') -Value "exit:$Number"
        }
    }
})
try {
    $jobs | Wait-Job | Out-Null
    foreach ($job in $jobs) {
        if ($job.State -ne 'Completed') { throw 'Concurrent build-lock fixture failed.' }
        Receive-Job $job -ErrorAction Stop
    }
} finally { $jobs | Remove-Job -Force }
$events = @(Get-Content -LiteralPath (Join-Path $testRoot 'events.log'))
if ($events.Count -ne 4 -or $events[0] -notmatch '^enter:(\d+)$') { throw 'Missing build-lock events.' }
$first = $Matches[1]
if ($events[1] -ne "exit:$first" -or $events[2] -notmatch '^enter:(\d+)$') { throw 'Service builds overlapped.' }
$second = $Matches[1]
if ($first -eq $second -or $events[3] -ne "exit:$second") { throw 'Service builds overlapped.' }
. $helper
$failed = $false
try { Invoke-KumaServiceBuild -RepoRoot $testRoot -Build { throw 'expected build failure' } } catch { $failed = $_.Exception.Message -eq 'expected build failure' }
if (!$failed) { throw 'Build failure was not propagated.' }
Invoke-KumaServiceBuild -RepoRoot $testRoot -TimeoutSeconds 0 -Build { Write-Output 'PASS: concurrent builds serialized; failure releases the lock.' }
