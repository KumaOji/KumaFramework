#!/usr/bin/env bash
set -euo pipefail
k3s kubectl -n life-deals get deployments,pods,pvc,services
node_ip=$(k3s kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}')
printf '\nDirect access: QingLong http://%s:5700; RSSHub http://%s:1200\n' "$node_ip" "$node_ip"
printf '\nQingLong: '
curl --noproxy '*' -fsS --max-time 10 "http://${node_ip}:5700/api/health"
printf '\nRSSHub: '
curl --noproxy '*' -fsS --max-time 10 "http://${node_ip}:1200/healthz"
printf '\n'
