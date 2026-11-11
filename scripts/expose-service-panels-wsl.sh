#!/usr/bin/env bash
# Expose existing WSL K3s web consoles on loopback; reconnect after pod restarts.
set -euo pipefail
if [[ ${EUID} -ne 0 ]]; then
  echo 'Run as root inside WSL.' >&2
  exit 1
fi
kubectl_bin="$(command -v kubectl)"
components=("$@")
if [[ ${#components[@]} -eq 0 ]]; then
  components=(n8n nacos qdrant)
fi
for component in "${components[@]}"; do
  namespace=base
  case "${component}" in
    n8n) ports='5678:5678' ;;
    nacos) ports='8848:8848' ;;
    qdrant) ports='6333:6333' ;;
    qinglong) namespace=life-deals; ports='5700:5700' ;;
    rsshub) namespace=life-deals; ports='1200:1200' ;;
    *) echo "Unknown panel: ${component}" >&2; exit 1 ;;
  esac
  kubectl -n "${namespace}" get "svc/${component}" >/dev/null
  cat > "/etc/systemd/system/kuma-panel-${component}.service" <<EOF
[Unit]
Description=Kuma local ${component} web panel port forward
After=k3s.service
Wants=k3s.service
StartLimitIntervalSec=0

[Service]
Environment=KUBECONFIG=/etc/rancher/k3s/k3s.yaml
ExecStart=${kubectl_bin} -n ${namespace} port-forward svc/${component} ${ports} --address=127.0.0.1
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF
done
systemctl daemon-reload
for component in "${components[@]}"; do
  systemctl enable "kuma-panel-${component}.service"
  systemctl restart "kuma-panel-${component}.service"
done
echo "Local panel forwarding enabled: ${components[*]}"
