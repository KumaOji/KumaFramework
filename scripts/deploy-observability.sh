#!/usr/bin/env bash
# Run with cluster-admin kubectl access; only applies observability resources.
set -euo pipefail
repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
kubectl get namespace base >/dev/null 2>&1 || kubectl create namespace base
kubectl apply -f "${repo_root}/k8s/base/observability.yaml"
kubectl -n base rollout status statefulset/skywalking-storage --timeout=600s
for deployment in skywalking-oap skywalking-ui otel-collector; do
  kubectl -n base rollout status "deployment/${deployment}" --timeout=600s
done
