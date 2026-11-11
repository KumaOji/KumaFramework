#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"
pod=$(k3s kubectl -n life-deals get pod -l app=life-deals-qinglong -o jsonpath='{.items[0].metadata.name}')
k3s kubectl -n life-deals cp rss_watch.py "${pod}:/ql/data/scripts/life-deals/rss_watch.py"
k3s kubectl -n life-deals exec "$pod" -- bash -c '
set -euo pipefail
if [ -f /ql/cli/dist/ql.js ]; then
  node /ql/cli/dist/ql.js --help
  node /ql/cli/dist/ql.js task exec --root /ql --json life-deals/rss_watch.py now
else
  /usr/local/bin/task life-deals/rss_watch.py now
fi
'
python3 - <<'PY'
import urllib.request
import xml.etree.ElementTree as ET
import json
import pathlib
import time
import subprocess

def service_url(name, port):
    address = subprocess.check_output([
        'k3s', 'kubectl', '-n', 'life-deals', 'get', 'service', name,
        '-o', 'jsonpath={.spec.clusterIP}',
    ], text=True).strip()
    return f'http://{address}:{port}'

rsshub_url = service_url('rsshub', 1200)
qinglong_url = service_url('qinglong', 5700) + '/api'
for path in ['/healthz', '/smzdm/ranking/pinlei/11/3?limit=2']:
    with urllib.request.urlopen(rsshub_url + path, timeout=50) as response:
        data = response.read()
    if 'smzdm' in path:
        root = ET.fromstring(data)
        items = root.findall('./channel/item')
        assert items, 'No items in live feed'
        print(f'Live SMZDM feed verified: {len(items)} items')
    else:
        print('RSSHub health verified')

credentials = json.loads(pathlib.Path('/home/kuma/.config/life-deals/qinglong.json').read_text())
token = None

def api(path, method='GET', payload=None):
    headers = {'Content-Type': 'application/json', 'User-Agent': 'LifeDeals-Setup/1.0'}
    if token:
        headers['Authorization'] = 'Bearer ' + token
    body = json.dumps(payload).encode() if payload is not None else None
    req = urllib.request.Request(qinglong_url + path, data=body, headers=headers, method=method)
    with urllib.request.urlopen(req, timeout=15) as response:
        result = json.load(response)
    assert result['code'] == 200, f'API failure: {method} {path}'
    return result.get('data')

token = api('/user/login', 'POST', credentials)['token']
tasks = api('/crons')
if isinstance(tasks, dict):
    tasks = tasks.get('data', tasks.get('records', []))
task = next(item for item in tasks if item['command'] == 'task life-deals/rss_watch.py')
for item in tasks:
    if item['command'].startswith('task faker2/'):
        assert item['isDisabled'] == 1, 'An account task was unexpectedly enabled'
print('Account tasks verified disabled')
before = {instance['id'] for instance in api(f'/crons/{task["id"]}/instances')}
api('/crons/run', 'PUT', [task['id']])
for attempt in range(30):
    instances = api(f'/crons/{task["id"]}/instances')
    completed = next((instance for instance in instances if instance['id'] not in before and instance['status'] != 0), None)
    if completed:
        assert completed['status'] == 1 and completed['exit_code'] == 0, 'Scheduled RSS execution failed'
        print('QingLong scheduler verified: RSS task exit code 0')
        break
    time.sleep(1)
else:
    raise RuntimeError('RSS task did not finish within the verification window')
PY
