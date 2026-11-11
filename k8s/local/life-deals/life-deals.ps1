param(
    [ValidateSet('start', 'status', 'stop', 'credentials')]
    [string]$Action = 'status'
)

$distro = 'Ubuntu-24.04'
switch ($Action) {
    'start' {
        wsl -d $distro -u root -- systemctl start k3s
        if ($LASTEXITCODE -ne 0) { throw 'Failed to start k3s' }
        $deployPath = (wsl -d $distro --exec wslpath -u "$($PSScriptRoot.Replace('\', '/'))/deploy.sh").Trim()
        if ($LASTEXITCODE -ne 0) { throw 'Failed to resolve deploy.sh path' }
        wsl -d $distro -u root -- bash $deployPath
    }
    'status' {
        $statusPath = (wsl -d $distro --exec wslpath -u "$($PSScriptRoot.Replace('\', '/'))/status.sh").Trim()
        if ($LASTEXITCODE -ne 0) { throw 'Failed to resolve status.sh path' }
        wsl -d $distro -u root -- bash $statusPath
    }
    'stop' {
        wsl -d $distro -u root -- k3s kubectl -n life-deals scale deployment/qinglong deployment/rsshub deployment/redis --replicas=0
    }
    'credentials' {
        # Explicit local terminal action; never call this while collecting shared logs.
        wsl -d $distro -- cat /home/kuma/.config/life-deals/qinglong.json
    }
}
if ($LASTEXITCODE -ne 0) { throw "Life deals action failed: $Action" }
