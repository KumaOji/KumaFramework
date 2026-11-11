#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"
framework="$(cd ../../.. && pwd)"
python3 "${framework}/scripts/deploy-local-wsl.py" --namespace life-deals --apply
for name in qinglong rsshub; do
  install -m 0644 "life-deals-${name}.service" "/etc/systemd/system/life-deals-${name}.service"
done
systemctl daemon-reload
systemctl enable --now life-deals-qinglong.service life-deals-rsshub.service
k3s kubectl -n life-deals get deployments,pvc
