#!/usr/bin/env bash
# Deploy only the local four-component monitoring stack.
set -euo pipefail
repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
kubectl get namespace base >/dev/null 2>&1 || kubectl create namespace base
local_user="${MONITORING_LOCAL_USER:-kuma}"
local_home="$(getent passwd "${local_user}" | cut -d: -f6)"
if [[ -z "${local_home}" ]]; then
  echo "Unknown local user: ${local_user}" >&2
  exit 1
fi
credential_dir="${local_home}/.config/kuma-observability"
mkdir -p "${credential_dir}"
chmod 700 "${credential_dir}"
password_file="${credential_dir}/grafana-admin-password"
umask 077
if ! kubectl -n base get secret grafana-admin >/dev/null 2>&1; then
  python3 -c 'import secrets, sys; sys.stdout.write(secrets.token_urlsafe(24))' > "${password_file}"
  kubectl -n base create secret generic grafana-admin \
    --from-literal=admin-user=admin --from-file="admin-password=${password_file}"
fi
kubectl -n base get secret grafana-admin -o 'jsonpath={.data.admin-password}' \
  | base64 --decode > "${password_file}"
chmod 600 "${password_file}"
if [[ ${EUID} -eq 0 ]]; then
  chown "${local_user}:$(id -gn "${local_user}")" "${credential_dir}" "${password_file}"
fi
kubectl apply -k "${repo_root}/k8s/local/monitoring"
for component in loki grafana prometheus alertmanager; do
  kubectl -n base rollout status "statefulset/${component}" --timeout=600s
done
echo "Grafana username: admin; password saved to ${password_file}"
