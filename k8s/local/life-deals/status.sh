#!/usr/bin/env bash
set -euo pipefail
k3s kubectl -n life-deals get pods,pvc
systemctl --no-pager --full status life-deals-qinglong life-deals-rsshub | head -35
printf '\nQingLong: '
curl -fsS --max-time 10 http://127.0.0.1:5700/api/health
printf '\nRSSHub: '
curl -fsS --max-time 10 http://127.0.0.1:1200/healthz
printf '\n'
