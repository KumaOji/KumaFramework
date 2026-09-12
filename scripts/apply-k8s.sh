#!/usr/bin/env bash
# 在服务器执行：应用 /data/k8s 下除 secret 与应用部署外的全部清单
# 目录布局与仓库 k8s/ 对齐：/data/k8s/base（base 命名空间）、/data/k8s/blog（blog 命名空间）
set -euo pipefail

K8S_ROOT="${K8S_ROOT:-/data/k8s}"

shopt -s nullglob
files=("${K8S_ROOT}"/base/*.yaml "${K8S_ROOT}"/blog/*.yaml)
if [ ${#files[@]} -eq 0 ]; then
  echo "ERROR: no yaml under ${K8S_ROOT}/{base,blog}"
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

kubectl rollout status deployment/nacos -n base --timeout=300s || true
