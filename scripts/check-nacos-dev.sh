#!/usr/bin/env bash
set -euo pipefail
NACOS_SERVER="${NACOS_SERVER:-172.23.89.45:8848}"
TOKEN=""
for pwd in 123456 nacos; do
  RESP=$(curl -s -X POST "http://${NACOS_SERVER}/nacos/v1/auth/login" -d "username=nacos&password=${pwd}")
  TOKEN=$(echo "${RESP}" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')
  [ -n "${TOKEN}" ] && break
done
[ -n "${TOKEN}" ] || { echo "login failed: ${RESP:-}"; exit 1; }
echo "=== kuma-cloud-blog-dev.yaml (oauth related) ==="
curl -s "http://${NACOS_SERVER}/nacos/v1/cs/configs?dataId=kuma-cloud-blog-dev.yaml&group=DEFAULT_GROUP&tenant=base&accessToken=${TOKEN}" \
  | grep -E 'authorization-uri|issuer-uri|redirect-uri|172\.23|33336|4321' || echo "(no matches or empty)"
echo "=== kuma-cloud-uaa-dev.yaml (redirect-uris) ==="
curl -s "http://${NACOS_SERVER}/nacos/v1/cs/configs?dataId=kuma-cloud-uaa-dev.yaml&group=DEFAULT_GROUP&tenant=base&accessToken=${TOKEN}" \
  | grep -E 'redirect-uris|172\.17|localhost:9000|require-proof-key' -A8 || echo "(no matches or empty)"
