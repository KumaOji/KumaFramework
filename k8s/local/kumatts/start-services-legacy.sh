#!/usr/bin/env bash
set -euo pipefail
project=/mnt/d/KumaProject/KumaTTS
runtime=/home/kuma/.local/share/feiyu-voice
export HF_HOME=/mnt/g/models/cache/feiyu-huggingface
export HF_HUB_DISABLE_XET=1
export MODELSCOPE_CACHE=/mnt/g/models/cache/modelscope
export TORCH_HOME=/mnt/g/models/cache/torch

if ! curl --silent --fail http://127.0.0.1:9880/health >/dev/null; then
    cd "$project/astrbot_plugin_feiyu_voice/server"
    nohup "$runtime/api/bin/python" -m feiyu_voice_service >> "$project/voice-service.log" 2>&1 < /dev/null &
    echo $! > "$project/voice-service.pid"
fi
if ! curl --silent --fail http://127.0.0.1:6185/ >/dev/null; then
    cd "$project/AstrBot"
    nohup "$runtime/astrbot/bin/python" main.py >> "$project/astrbot-runtime.log" 2>&1 < /dev/null &
    echo $! > "$project/astrbot.pid"
fi
echo 'AstrBot: http://localhost:6185'
echo 'Voice service: http://localhost:9880'
