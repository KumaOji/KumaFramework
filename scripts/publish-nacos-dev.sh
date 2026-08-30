#!/usr/bin/env bash
# 本地 WSL：向 nacos 发布 dev 配置
set -euo pipefail

NACOS_SERVER="${NACOS_SERVER:-172.23.89.45:8848}"
CONFIG_ROOT="${CONFIG_ROOT:-.nacos-tmp/base}"
TENANT="${TENANT:-base}"

login_nacos() {
  curl -s -X POST "http://${NACOS_SERVER}/nacos/v1/auth/login" \
    -d "username=nacos&password=$1"
}

extract_token() {
  sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p'
}

TOKEN=""
for pwd in 123456 nacos; do
  RESP=$(login_nacos "${pwd}")
  TOKEN=$(echo "${RESP}" | extract_token)
  [ -n "${TOKEN}" ] && break
done
[ -n "${TOKEN}" ] || { echo "Nacos login failed: ${RESP:-empty}"; exit 1; }

for file in "${CONFIG_ROOT}"/kuma-*-dev.yaml; do
  [ -f "${file}" ] || continue
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

echo "All dev configs published (tenant=${TENANT})."
