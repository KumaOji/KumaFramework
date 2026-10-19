param(
    [ValidateSet('all', 'crud', 'search', 'aggregation')]
    [string]$Lesson = 'all',
    [string]$Distribution = 'Ubuntu-24.04',
    [ValidateRange(1024, 65535)]
    [int]$Port = 19200,
    [switch]$Test
)
$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$previous = [Environment]::GetEnvironmentVariable('KUMA_LAB_ES_URL', 'Process')
$forward = $null
$pidFile = '/tmp/kuma-es-lab-' + [guid]::NewGuid().ToString('N') + '.pid'
$logDir = Join-Path $repoRoot 'kuma-project/kuma-project-lab/build/es-lab'
New-Item -ItemType Directory -Force $logDir | Out-Null
$logId = [guid]::NewGuid().ToString('N')
try {
    # Fail on an occupied port rather than accidentally talking to a different server.
    $listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, $Port)
    try { $listener.Start() } finally { $listener.Stop() }
    # WSL cold starts can briefly expose stale Pod readiness; retry forwarding during startup.
    # A separate Linux process group lets finally stop both the loop and its kubectl child.
    $drive = $repoRoot.Substring(0, 1).ToLowerInvariant()
    $wslScript = '/mnt/' + $drive + $repoRoot.Substring(2).Replace('\', '/') + '/kuma-project/kuma-project-lab/scripts/forward-elasticsearch-lab.sh'
    $forward = Start-Process -FilePath 'wsl.exe' -ArgumentList @('-d', $Distribution, '-u', 'root', '--', 'setsid', '--wait', 'sh', ('"' + $wslScript + '"'), $pidFile, $Port) `
        -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $logDir "$logId.out.log") `
        -RedirectStandardError (Join-Path $logDir "$logId.err.log")
    $ready = $false
    $deadline = [DateTime]::UtcNow.AddSeconds(55)
    while ([DateTime]::UtcNow -lt $deadline) {
        if ($forward.HasExited) { throw "ES port-forward exited. See $logDir/$logId.err.log" }
        try {
            $info = Invoke-RestMethod "http://127.0.0.1:$Port" -TimeoutSec 1
            if ($info.version.number -and $info.tagline -eq 'You Know, for Search') { $ready = $true; break }
        } catch { Start-Sleep -Milliseconds 500 }
    }
    if (!$ready) { throw 'WSL Elasticsearch did not become reachable within the startup window.' }
    [Environment]::SetEnvironmentVariable('KUMA_LAB_ES_URL', "http://127.0.0.1:$Port", 'Process')
    if ($Test) {
        & (Join-Path $repoRoot 'gradlew.bat') -p $repoRoot :kuma-project:kuma-project-lab:test --tests '*Elasticsearch*Test' --console=plain
    } else {
        & (Join-Path $repoRoot 'gradlew.bat') -p $repoRoot :kuma-project:kuma-project-lab:elasticsearchLab "-PesLesson=$Lesson" --console=plain
    }
    if ($LASTEXITCODE -ne 0) { throw 'Elasticsearch experiments failed; inspect the experiment output.' }
} finally {
    [Environment]::SetEnvironmentVariable('KUMA_LAB_ES_URL', $previous, 'Process')
    if ($forward) {
        # Windows PowerShell treats even WSL's proxy warning on stderr as an error.
        $ErrorActionPreference = 'Continue'
        # The Linux group identifies only this script's loop and forwarding process.
        $linuxPid = (& wsl -d $Distribution -u root -- cat $pidFile 2>$null | Out-String).Trim()
        if ($linuxPid -match '^\d+$') { & wsl -d $Distribution -u root -- kill -- "-$linuxPid" 2>$null }
        if (!$forward.HasExited) { Stop-Process -Id $forward.Id -ErrorAction SilentlyContinue }
        & wsl -d $Distribution -u root -- unlink $pidFile 2>$null
    }
}
