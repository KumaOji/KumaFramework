#!/usr/bin/env bash
# Server loopback-only forwards. Access them through an authenticated SSH tunnel.
set -euo pipefail
[[ ${EUID} -eq 0 ]] || { echo 'Run as root on the server.' >&2; exit 1; }
kubectl_bin="$(command -v kubectl)"
for component in grafana prometheus alertmanager loki skywalking-ui skywalking-oap lab; do
  case "${component}" in
    grafana) service=grafana; ports='3000:3000' ;;
    prometheus) service=prometheus; ports='9090:9090' ;;
    alertmanager) service=alertmanager; ports='9093:9093' ;;
    loki) service=loki; ports='3100:3100' ;;
    skywalking-ui) service=skywalking-ui; ports='12880:8080' ;;
    skywalking-oap) service=skywalking-oap; ports='9412:9412' ;;
    lab) service=lab; ports='19090:19090' ;;
  esac
  cat > "/etc/systemd/system/kuma-server-${component}.service" <<EOF
[Unit]
Description=Kuma server ${component} loopback port forward
After=k3s.service
Wants=k3s.service
StartLimitIntervalSec=0
[Service]
Environment=KUBECONFIG=/etc/rancher/k3s/k3s.yaml
ExecStart=${kubectl_bin} -n base port-forward svc/${service} ${ports} --address=127.0.0.1
Restart=always
RestartSec=5
[Install]
WantedBy=multi-user.target
EOF
done
systemctl daemon-reload
for component in grafana prometheus alertmanager loki skywalking-ui skywalking-oap lab; do
  systemctl enable --now "kuma-server-${component}.service"
done
echo 'Server UI forwards listen on 127.0.0.1 only; use SSH -L to access them.'
