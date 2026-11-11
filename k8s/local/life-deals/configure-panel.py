#!/usr/bin/env python3
"""Initialize a new local panel and register the prepared tasks without printing secrets."""
import json
import os
import pathlib
import secrets
import urllib.request

base_url = 'http://127.0.0.1:5700/api'
secret_dir = pathlib.Path('/home/kuma/.config/life-deals')
secret_dir.mkdir(mode=0o700, parents=True, exist_ok=True)
os.chmod(secret_dir, 0o700)
os.chown(secret_dir, 1000, 1000)
credentials_file = secret_dir / 'qinglong.json'


def api(path, method='GET', payload=None, token=None):
    headers = {'Content-Type': 'application/json', 'User-Agent': 'LifeDeals-Setup/1.0'}
    if token:
        headers['Authorization'] = 'Bearer ' + token
    data = json.dumps(payload).encode() if payload is not None else None
    request = urllib.request.Request(base_url + path, data=data, headers=headers, method=method)
    with urllib.request.urlopen(request, timeout=30) as response:
        result = json.load(response)
    if result.get('code') != 200:
        raise RuntimeError(f'{method} {path} failed: code={result.get("code")}')
    return result


initialized = api('/system')['data']['isInitialized']
if not initialized:
    if credentials_file.exists():
        credentials = json.loads(credentials_file.read_text())
    else:
        credentials = {'username': 'kuma', 'password': secrets.token_urlsafe(24)}
        descriptor = os.open(credentials_file, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
        with os.fdopen(descriptor, 'w') as handle:
            json.dump(credentials, handle, indent=2)
        os.chown(credentials_file, 1000, 1000)
    api('/user/init', 'PUT', credentials)
elif credentials_file.exists():
    credentials = json.loads(credentials_file.read_text())
else:
    raise SystemExit('Panel already initialized; existing account was preserved. Use its UI to add prepared tasks.')

token = api('/user/login', 'POST', credentials)['data']['token']
existing = api('/crons', token=token)['data']
if isinstance(existing, dict):
    existing = existing.get('data', existing.get('records', []))
commands = {item['command']: item for item in existing}
tasks = [
    ('好价聚合 RSS（每15分钟）', 'task life-deals/rss_watch.py', '*/15 * * * *', False),
    ('京东 Cookie 状态检查（待填账号）', 'task faker2/jd_CheckCK.js', '15 7 * * *', True),
    ('京东日常任务（待填账号并验证）', 'task faker2/jd_bean_home.js', '30 7 * * *', True),
    ('京东店铺签到（待填账号并验证）', 'task faker2/jd_dpqd_sign.js', '45 7 * * *', True),
]
for name, command, schedule, disabled in tasks:
    if command in commands:
        print(f'Preserved existing task: {name}')
        continue
    created = api('/crons', 'POST', {'name': name, 'command': command, 'schedule': schedule}, token)['data']
    if disabled:
        api('/crons/disable', 'PUT', [created['id']], token)
    print(f'Configured task: {name} ({"disabled" if disabled else "enabled"})')
print('Panel initialization complete; credentials saved privately at ' + str(credentials_file))
