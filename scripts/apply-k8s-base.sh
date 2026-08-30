#!/usr/bin/env bash
# 在服务器 /data/k8s/base 目录执行：应用除 secret 示例外全部清单
set -euo pipefail

K8S_DIR="${K8S_DIR:-/data/k8s/base}"
NAMESPACE="${K8S_NAMESPACE:-base}"

if [ ! -d "${K8S_DIR}" ]; then
  echo "ERROR: ${K8S_DIR} not found"
  exit 1
fi

shopt -s nullglob
files=("${K8S_DIR}"/*.yaml)
if [ ${#files[@]} -eq 0 ]; then
  echo "ERROR: no yaml under ${K8S_DIR}"
  exit 1
fi

for file in "${files[@]}"; do
  base=$(basename "${file}")
  case "${base}" in
    *secret.example.yaml|*secret.yaml)
      echo "SKIP ${base}"
      continue
      ;;
    blog-background.yaml|blog-fronted.yaml|gateway.yaml|uaa.yaml)
      echo "SKIP ${base} (image managed by deploy workflow)"
      continue
      ;;
  esac
  echo "APPLY ${base}"
  kubectl apply -f "${file}"
done

kubectl rollout status deployment/nacos -n "${NAMESPACE}" --timeout=300s || true
