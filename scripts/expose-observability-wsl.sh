#!/usr/bin/env bash
# Install restartable localhost port forwards on the local WSL K3s host.
set -euo pipefail
if [[ ${EUID} -ne 0 ]]; then
  echo 'Run as root inside WSL.' >&2
  exit 1
fi
kubectl_bin="$(command -v kubectl)"
for component in ui collector oap; do
  case "${component}" in
    ui) service=skywalking-ui; ports='12880:8080' ;;
    collector) service=otel-collector; ports='4317:4317 4318:4318' ;;
    oap) service=skywalking-oap; ports='11800:11800 9412:9412' ;;
  esac
  cat > "/etc/systemd/system/kuma-observability-${component}.service" <<EOF
[Unit]
Description=Kuma observability ${component} localhost port forward
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
for component in ui collector oap; do
  systemctl enable "kuma-observability-${component}.service"
  systemctl restart "kuma-observability-${component}.service"
done
echo 'UI: http://localhost:12880; OTLP: localhost:4317 / localhost:4318; SkyWalking Agent: localhost:11800'
