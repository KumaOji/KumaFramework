#!/usr/bin/env bash
# 在 kumacloud 服务器上执行：通过 port-forward 向 blog-nacos 发布 prod 配置
set -euo pipefail

NACOS_USERNAME="${NACOS_USERNAME:-nacos}"
NACOS_PASSWORD="${NACOS_PASSWORD:-}"
NACOS_SERVER="127.0.0.1:8848"
CONFIG_ROOT="${CONFIG_ROOT:-/tmp/nacos-config}"

echo "=== blog-nacos status ==="
NACOS_SVC="${NACOS_SVC:-blog-nacos}"
if ! kubectl get svc "${NACOS_SVC}" -n blog >/dev/null 2>&1; then
  if kubectl get svc nacos -n blog >/dev/null 2>&1; then
    NACOS_SVC="nacos"
    echo "WARN: blog-nacos not found, fallback to svc/nacos"
  else
    echo "ERROR: No Nacos service in namespace blog."
    kubectl get deploy,svc -n blog | grep -E 'nacos|NAME' || true
    exit 1
  fi
fi

if kubectl get deploy "${NACOS_SVC}" -n blog >/dev/null 2>&1; then
  kubectl rollout status "deployment/${NACOS_SVC}" -n blog --timeout=300s
fi

pkill -f "kubectl port-forward.*${NACOS_SVC}.*8848:8848" 2>/dev/null || true
sleep 1

kubectl port-forward -n blog "svc/${NACOS_SVC}" 8848:8848 >/tmp/nacos-pf.log 2>&1 &
PF_PID=$!
trap 'kill ${PF_PID} 2>/dev/null || true' EXIT

READY=0
for _ in $(seq 1 60); do
  if curl -sf -m 3 "http://${NACOS_SERVER}/nacos/v1/console/health/liveness" >/dev/null 2>&1; then
    READY=1
    break
  fi
  sleep 2
done
if [ "${READY}" -ne 1 ]; then
  echo "ERROR: Nacos health check failed (curl exit 7 = connection refused)."
  echo "port-forward log:"
  cat /tmp/nacos-pf.log || true
  echo "blog-nacos pod logs:"
  kubectl logs -n blog -l app=blog-nacos --tail=80 || true
  exit 1
fi

login_nacos() {
  curl -s -X POST "http://${NACOS_SERVER}/nacos/v1/auth/login" \
    -d "username=${NACOS_USERNAME}&password=$1"
}

extract_token() {
  sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p'
}

# Nacos 2.4+ 首次需初始化 admin
curl -s -X POST "http://${NACOS_SERVER}/nacos/v1/auth/users/admin" \
  -H "serverIdentity: security" \
  -d "username=nacos&password=nacos" >/dev/null || true

try_passwords=()
if [ -n "${NACOS_PASSWORD}" ]; then
  try_passwords+=("${NACOS_PASSWORD}")
fi
try_passwords+=("nacos" "123456")

TOKEN=""
for pwd in "${try_passwords[@]}"; do
  RESP=$(login_nacos "${pwd}")
  TOKEN=$(echo "${RESP}" | extract_token)
  if [ -n "${TOKEN}" ]; then
    echo "Nacos login OK."
    break
  fi
done
if [ -z "${TOKEN}" ]; then
  echo "ERROR: Nacos login failed. Last response: ${RESP:-empty}"
  exit 1
fi

TENANT=$(curl -s "http://${NACOS_SERVER}/nacos/v1/console/namespaces" \
  -H "accessToken: ${TOKEN}" | sed -n 's/.*"namespace":"\(base\)".*/\1/p' | head -n1)
if [ -z "${TENANT}" ]; then
  echo "Creating Nacos namespace: base"
  curl -s -X POST "http://${NACOS_SERVER}/nacos/v1/console/namespaces" \
    -H "accessToken: ${TOKEN}" \
    -d "customNamespaceId=base&namespaceName=base&namespaceDesc=base"
  TENANT="base"
fi

mapfile -t FILES < <(find "${CONFIG_ROOT}" -name 'kuma-*-prod.yaml' -type f 2>/dev/null | sort)
if [ ${#FILES[@]} -eq 0 ]; then
  echo "ERROR: No prod yaml under ${CONFIG_ROOT}"
  find "${CONFIG_ROOT}" -type f 2>/dev/null || true
  exit 1
fi

for file in "${FILES[@]}"; do
  DATA_ID=$(basename "${file}")
  RESULT=$(curl -s -X POST "http://${NACOS_SERVER}/nacos/v1/cs/configs" \
    -H "accessToken: ${TOKEN}" \
    --data-urlencode "dataId=${DATA_ID}" \
    --data-urlencode "group=DEFAULT_GROUP" \
    --data-urlencode "tenant=${TENANT}" \
    --data-urlencode "type=yaml" \
    --data-urlencode "content@${file}")
  if [ "${RESULT}" != "true" ]; then
    echo "ERROR: publish ${DATA_ID} failed: ${RESULT}"
    exit 1
  fi
  echo "Published ${DATA_ID}"
done

echo "All prod configs published to blog-nacos (tenant=${TENANT})."
