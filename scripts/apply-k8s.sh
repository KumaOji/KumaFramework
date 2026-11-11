#!/usr/bin/env bash
# 在现有服务器执行：按命名空间组织的 Kustomize 入口只包含配置与基础设施。
# Secret 和应用镜像部署由各自的发布流程管理。
set -euo pipefail

K8S_ROOT="${K8S_ROOT:-/data/k8s}"
script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

if [[ ! -f "${K8S_ROOT}/server/kustomization.yaml" ]]; then
  echo "ERROR: missing ${K8S_ROOT}/server/kustomization.yaml"
  exit 1
fi
case "${1:-}" in
  '') bash "${script_dir}/prepare-monitoring-server.sh" ;;
  --skip-prepare) ;; # Apply workflow already made the private backup and credentials.
  *) echo "Usage: $0 [--skip-prepare]" >&2; exit 2 ;;
esac

manifest="$(mktemp)"
trap 'rm -f "$manifest"' EXIT
kubectl kustomize "${K8S_ROOT}/server" > "$manifest"
# Preserve provisioner-owned PVC fields and remove obsolete probe handlers.
python3 "${script_dir}/apply-k8s-resource.py" "$manifest"

# Frontend images are published separately. Apply its configuration with the
# running image so changes to this server manifest are not silently skipped.
frontend_manifest="${K8S_ROOT}/server/blog/blog-frontend.yaml"
if [[ -f "$frontend_manifest" ]]; then
  frontend_image=$(kubectl -n blog get deployment blog-fronted -o 'jsonpath={.spec.template.spec.containers[?(@.name=="blog-fronted")].image}')
  if [[ -z "$frontend_image" || "$frontend_image" == *:placeholder ]]; then
    echo 'ERROR: blog-fronted has no published image to preserve.' >&2
    exit 1
  fi
  python3 "${script_dir}/apply-k8s-resource.py" "$frontend_manifest" --image "$frontend_image"
  kubectl rollout status deployment/blog-fronted -n blog --timeout=300s
fi

kubectl rollout status deployment/nacos -n base --timeout=300s || true
