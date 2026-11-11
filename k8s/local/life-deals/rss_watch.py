#!/usr/bin/env python3
"""Poll configured RSS feeds, persist deduplication state, optionally notify."""
import datetime
import html
import json
import os
import pathlib
import re
import subprocess
import urllib.request
import xml.etree.ElementTree as ET

folder = pathlib.Path(__file__).resolve().parent
config = json.loads((folder / 'feeds.json').read_text(encoding='utf-8'))
state_path = folder / 'rss-state.json'
state = json.loads(state_path.read_text(encoding='utf-8')) if state_path.exists() else {}
base_url = os.environ.get('RSSHUB_URL', 'http://rsshub:1200').rstrip('/')
messages = []
snapshot = []
failures = 0

for feed in config['feeds']:
    try:
        req = urllib.request.Request(base_url + feed['path'], headers={'User-Agent': 'LifeDeals/1.0'})
        with urllib.request.urlopen(req, timeout=50) as response:
            root = ET.fromstring(response.read())
        items = root.findall('./channel/item')
        if not items:
            raise ValueError('Feed has no RSS items; state was preserved')
        key = feed['path']
        is_first_poll = key not in state
        previous = set(state.get(key, []))
        current = []
        deals = []
        for item in items:
            title = item.findtext('title', default='').strip()
            link = item.findtext('link', default='').strip()
            identity = item.findtext('guid') or link
            if not identity or not link or not title:
                continue
            current.append(identity)
            if any(word in title for word in config.get('exclude_keywords', [])):
                continue
            is_new = not is_first_poll and identity not in previous
            description = html.unescape(re.sub(r'<[^>]*>', ' ', item.findtext('description', default='')))
            description = re.sub(r'\s+', ' ', description).strip()
            deals.append({
                'title': title,
                'link': link,
                'description': description,
                'published_at': item.findtext('pubDate', default=''),
                'status': '首次收录' if is_first_poll else ('新增' if is_new else '已见'),
            })
            if is_new:
                messages.append(f'{title}\n{link}')
        state[key] = list(dict.fromkeys(current + state.get(key, [])))[:1000]
        print(f"\n=== {feed['name']} ===")
        print(f"获取 {len(current)} 条，筛选后 {len(deals)} 条，新增 {sum(deal['status'] == '新增' for deal in deals)} 条。")
        print('以下为本次榜单；已见内容仍展示，新增计数仅用于去重。')
        log_limit = max(1, int(config.get('max_log_items_per_feed', 20)))
        for index, deal in enumerate(deals[:log_limit], 1):
            print(f"\n{index}. [{deal['status']}] {deal['title']}")
            if deal['description'] and deal['description'] not in deal['title']:
                print(f"   优惠说明：{deal['description'][:800]}")
            if deal['published_at']:
                print(f"   发布时间：{deal['published_at']}")
            print(f"   详情：{deal['link']}")
        if len(deals) > log_limit:
            print(f'其余 {len(deals) - log_limit} 条见 latest-deals.json。')
        snapshot.append({'name': feed['name'], 'path': key, 'deals': deals})
    except Exception as error:
        failures += 1
        print(f"ERROR {feed['name']}: {error}")

if snapshot:
    snapshot_path = folder / 'latest-deals.json'
    snapshot_temporary = snapshot_path.with_suffix('.tmp')
    snapshot_temporary.write_text(json.dumps({
        'fetched_at': datetime.datetime.now().astimezone().isoformat(timespec='seconds'),
        'failed_feeds': failures,
        'feeds': snapshot,
    }, ensure_ascii=False, indent=2), encoding='utf-8')
    snapshot_temporary.replace(snapshot_path)

if messages and os.environ.get('LIFE_DEALS_NOTIFY') == 'true':
    body = '\n\n'.join(messages[:config.get('max_items_per_run', 10)])
    # Use QingLong's built-in notification adapter; secrets remain in its environment.
    relay = "let b='';process.stdin.setEncoding('utf8');process.stdin.on('data',c=>b+=c);process.stdin.on('end',async()=>{try{await require('/ql/sample/notify.js').sendNotify('好价提醒',b)}catch(e){console.error(e.message);process.exitCode=1}})"
    subprocess.run(['node', '-e', relay], input=body, text=True, check=True)

temporary = state_path.with_suffix('.tmp')
temporary.write_text(json.dumps(state, ensure_ascii=False, indent=2), encoding='utf-8')
temporary.replace(state_path)
print(f'{datetime.datetime.now().isoformat(timespec="seconds")}: {len(messages)} new deals; notifications ' + ('enabled' if os.environ.get('LIFE_DEALS_NOTIFY') == 'true' else 'disabled'))
raise SystemExit(1 if failures else 0)
