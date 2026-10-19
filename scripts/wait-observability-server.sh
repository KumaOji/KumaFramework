#!/usr/bin/env bash
# Newly enabled Logback/OTLP endpoints must be ready before switching production pods.
set -euo pipefail
deadline=$((SECONDS + 1800))
for component in loki prometheus grafana alertmanager; do
  until kubectl -n base get "statefulset/${component}" >/dev/null 2>&1; do
    (( SECONDS < deadline )) || { echo "Timed out waiting for ${component} deployment" >&2; exit 1; }
    sleep 5
  done
  kubectl -n base rollout status "statefulset/${component}" --timeout=1800s
done
kubectl -n base rollout status deployment/otel-collector --timeout=300s
kubectl -n base rollout status deployment/nacos --timeout=300s
echo 'Monitoring infrastructure is ready; application rollout may proceed.'
