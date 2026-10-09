#!/usr/bin/env bash
# Install automatic localhost port forwards on the WSL K3s host.
set -euo pipefail
if [[ ${EUID} -ne 0 ]]; then
  echo 'Run as root inside WSL.' >&2
  exit 1
fi
kubectl_bin="$(command -v kubectl)"
for component in loki grafana prometheus alertmanager; do
  case "${component}" in
    loki) ports='3100:3100' ;;
    grafana) ports='3000:3000' ;;
    prometheus) ports='9090:9090' ;;
    alertmanager) ports='9093:9093' ;;
  esac
  cat > "/etc/systemd/system/kuma-monitoring-${component}.service" <<EOF
[Unit]
Description=Kuma local monitoring ${component} port forward
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
for component in loki grafana prometheus alertmanager; do
  systemctl enable "kuma-monitoring-${component}.service"
  systemctl restart "kuma-monitoring-${component}.service"
done
echo 'Grafana: http://localhost:3000; Prometheus: http://localhost:9090; Alertmanager: http://localhost:9093; Loki: http://localhost:3100'
