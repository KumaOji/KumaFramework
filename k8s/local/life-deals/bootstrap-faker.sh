#!/usr/bin/env bash
set -euo pipefail
deploy="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
project="${LIFE_PROJECT_DIR:-/mnt/d/Vscode_project/life}"
cd "$project"
pod=$(k3s kubectl -n life-deals get pod -l app=life-deals-qinglong -o jsonpath='{.items[0].metadata.name}')
k3s kubectl -n life-deals exec "$pod" -- mkdir -p /ql/data/scripts/faker2 /ql/data/scripts/life-deals
tar --exclude=.git --exclude=node_modules --exclude=.venv -C faker2 -cf - . |
  k3s kubectl -n life-deals exec -i "$pod" -- tar -xf - -C /ql/data/scripts/faker2
tar -C "$deploy" -cf - rss_watch.py credentials.example.env |
  k3s kubectl -n life-deals exec -i "$pod" -- tar -xf - -C /ql/data/scripts/life-deals
if ! k3s kubectl -n life-deals exec "$pod" -- test -f /ql/data/scripts/life-deals/feeds.json; then
  k3s kubectl -n life-deals cp "$deploy/feeds.json" "${pod}:/ql/data/scripts/life-deals/feeds.json"
fi
k3s kubectl -n life-deals exec "$pod" -- bash -c '
set -euo pipefail
cd /ql/data/scripts/faker2
npm ci --no-fund --no-audit
python3 -m pip install --index-url https://pypi.org/simple --target /ql/data/dep_cache/life-deals-python -r requirements.txt
if ! grep -q "LIFE_DEALS_CONFIG" /ql/data/config/config.sh; then
  cat >> /ql/data/config/config.sh <<"CONFIG"

# LIFE_DEALS_CONFIG: local setup; no account tasks are enabled automatically.
AutoAddCron="false"
AutoDelCron="false"
RandomDelay="60"
CommandTimeoutTime="10m"
export RSSHUB_URL="http://rsshub:1200"
export PYTHONPATH="/ql/data/dep_cache/life-deals-python${PYTHONPATH:+:$PYTHONPATH}"
CONFIG
fi
node -e "require(\"got\"); require(\"http-cookie-agent/http\"); require(\"sharp\")({create:{width:1,height:1,channels:3,background:\"#fff\"}}).png().toBuffer().then(()=>console.log(\"Faker dependency checks passed\"))"
PYTHONPATH=/ql/data/dep_cache/life-deals-python python3 -c "import requests,jieba; print(\"Python dependency checks passed\")"
'
