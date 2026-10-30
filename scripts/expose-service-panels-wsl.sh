#!/usr/bin/env bash
# Expose existing WSL K3s web consoles on loopback; reconnect after pod restarts.
set -euo pipefail
if [[ ${EUID} -ne 0 ]]; then
  echo 'Run as root inside WSL.' >&2
  exit 1
fi
kubectl_bin="$(command -v kubectl)"
for component in n8n nacos qdrant; do
  kubectl -n base get "svc/${component}" >/dev/null
  case "${component}" in
    n8n) ports='5678:5678' ;;
    nacos) ports='8848:8848' ;;
    qdrant) ports='6333:6333' ;;
  esac
  cat > "/etc/systemd/system/kuma-panel-${component}.service" <<EOF
[Unit]
Description=Kuma local ${component} web panel port forward
After=k3s.service
Wants=k3s.service
StartLimitIntervalSec=0

[Service]
Environment=KUBECONFIG=/etc/rancher/k3s/k3s.yaml
ExecStart=${kubectl_bin} -n base port-forward svc/${component} ${ports} --address=127.0.0.1
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF
done
systemctl daemon-reload
for component in n8n nacos qdrant; do
  systemctl enable --now "kuma-panel-${component}.service"
done
echo 'n8n: http://localhost:5678; Nacos: http://localhost:8848/nacos/; Qdrant: http://localhost:6333/dashboard'
