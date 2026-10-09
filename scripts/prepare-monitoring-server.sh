#!/usr/bin/env bash
# Use on the existing production K3s node before applying the release.
set -euo pipefail
umask 077
release_dir="/data/kuma-releases/$(date -u +%Y%m%d-%H%M%S)"
mkdir -p "${release_dir}"
for namespace in base blog; do
  kubectl -n "${namespace}" get deployment,statefulset,service,configmap,secret,networkpolicy -o yaml \
    > "${release_dir}/${namespace}-snapshot.yaml"
done
if [[ -d /data/k8s ]]; then
  tar -C /data -czf "${release_dir}/k8s-before.tar.gz" k8s
fi
python3 - "${release_dir}" <<'PY'
import base64, json, pathlib, subprocess, sys, urllib.parse, urllib.request, urllib.error
destination = pathlib.Path(sys.argv[1]) / 'nacos'
destination.mkdir()
service = json.loads(subprocess.check_output(['kubectl','-n','base','get','svc','nacos','-o','json']))
secret = json.loads(subprocess.check_output(['kubectl','-n','base','get','secret','kuma-secret','-o','json']))
password = base64.b64decode(secret['data']['nacos-password']).decode()
base = 'http://' + service['spec']['clusterIP'] + ':8848/nacos'
opener = urllib.request.build_opener(urllib.request.ProxyHandler({}))
login = opener.open(urllib.request.Request(base+'/v1/auth/login',
    data=urllib.parse.urlencode({'username':'nacos','password':password}).encode()), timeout=10)
token = json.loads(login.read())['accessToken']
for name in ('kuma-shared','kuma-cloud-blog','kuma-cloud-uaa','kuma-cloud-gateway','kuma-cloud-lab'):
    identifier = name+'-prod.yaml'
    query = urllib.parse.urlencode({'dataId':identifier,'group':'DEFAULT_GROUP','tenant':'base','accessToken':token})
    try:
        response = opener.open(base+'/v1/cs/configs?'+query, timeout=10)
        (destination/identifier).write_bytes(response.read())
    except urllib.error.HTTPError as error:
        if error.code != 404:
            raise RuntimeError('Cannot back up '+identifier+': HTTP '+str(error.code)) from None
print('Production Nacos configurations backed up privately.')
PY
kubectl -n base exec mysql-0 -- sh -c \
  'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysqldump -uroot --single-transaction --no-tablespaces --databases base kuma_uaa' \
  > "${release_dir}/applications-before.sql"
kubectl -n base exec mysql-0 -- sh -c \
  'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql -uroot -e "CREATE DATABASE IF NOT EXISTS kuma_lab CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"'
credential_dir=/data/kuma-observability
mkdir -p "${credential_dir}"
if ! kubectl -n base get secret grafana-admin >/dev/null 2>&1; then
  python3 -c 'import secrets,sys; sys.stdout.write(secrets.token_urlsafe(24))' > "${credential_dir}/grafana-admin-password"
  kubectl -n base create secret generic grafana-admin --from-literal=admin-user=admin \
    --from-file="admin-password=${credential_dir}/grafana-admin-password"
fi
kubectl -n base get secret grafana-admin -o 'jsonpath={.data.admin-password}' \
  | base64 --decode > "${credential_dir}/grafana-admin-password"
chmod 600 "${credential_dir}/grafana-admin-password"
echo "Private deployment backup: ${release_dir}; Grafana credentials: ${credential_dir}/grafana-admin-password"
