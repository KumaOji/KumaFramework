#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"
framework="$(cd ../../.. && pwd)"
python3 "${framework}/scripts/deploy-local-wsl.py" --namespace life-deals --apply
for name in qinglong rsshub; do
  unit="life-deals-${name}.service"
  if systemctl cat "$unit" >/dev/null 2>&1; then
    systemctl disable --now "$unit"
  fi
  if [[ -f "/etc/systemd/system/$unit" ]]; then
    backup_dir=/var/backups/life-deals-port-forward
    install -d -m 0700 "$backup_dir"
    mv "/etc/systemd/system/$unit" "$backup_dir/$unit"
  fi
done
systemctl daemon-reload
for name in qinglong rsshub; do
  selector="svccontroller.k3s.cattle.io/svcnamespace=life-deals,svccontroller.k3s.cattle.io/svcname=$name"
  daemonset=''
  for ((attempt=0; attempt<60; attempt++)); do
    daemonset=$(k3s kubectl -n kube-system get daemonset -l "$selector" -o name)
    [[ -n "$daemonset" ]] && break
    sleep 2
  done
  if [[ -z "$daemonset" ]]; then
    echo "ServiceLB DaemonSet missing for $name" >&2
    exit 1
  fi
  k3s kubectl -n kube-system rollout status "$daemonset" --timeout=180s
done
bash "${framework}/scripts/expose-service-panels-wsl.sh" qinglong rsshub
bash ./status.sh
