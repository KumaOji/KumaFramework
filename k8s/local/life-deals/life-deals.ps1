param(
    [ValidateSet('start', 'status', 'stop', 'credentials')]
    [string]$Action = 'status'
)

$distro = 'Ubuntu-24.04'
switch ($Action) {
    'start' {
        wsl -d $distro -u root -- systemctl start k3s
        if ($LASTEXITCODE -ne 0) { throw 'Failed to start k3s' }
        wsl -d $distro -u root -- k3s kubectl -n life-deals scale deployment/qinglong deployment/rsshub deployment/redis --replicas=1
        wsl -d $distro -u root -- systemctl start life-deals-qinglong life-deals-rsshub
    }
    'status' {
        wsl -d $distro -u root -- k3s kubectl -n life-deals get deployments,pods,pvc
        wsl -d $distro -- systemctl is-active life-deals-qinglong life-deals-rsshub
    }
    'stop' {
        wsl -d $distro -u root -- systemctl stop life-deals-qinglong life-deals-rsshub
        wsl -d $distro -u root -- k3s kubectl -n life-deals scale deployment/qinglong deployment/rsshub deployment/redis --replicas=0
    }
    'credentials' {
        # Explicit local terminal action; never call this while collecting shared logs.
        wsl -d $distro -- cat /home/kuma/.config/life-deals/qinglong.json
    }
}
if ($LASTEXITCODE -ne 0) { throw "Life deals action failed: $Action" }
