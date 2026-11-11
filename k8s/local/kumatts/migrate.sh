#!/usr/bin/env bash
set -euo pipefail
project=/mnt/d/KumaProject/KumaTTS
deploy="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
framework="$(cd "$deploy/../../.." && pwd)"
image="$(python3 "$deploy/prepare.py" --image)"
buildah push "$image" oci-archive:/tmp/kumatts-runtime.oci.tar
k3s ctr images import --local --index-name "$image" /tmp/kumatts-runtime.oci.tar
k3s crictl inspecti "$image" >/dev/null
python3 "$deploy/prepare.py"
kubectl create namespace kumatts --dry-run=client -o yaml | kubectl apply -f -

# Only stop the exact legacy processes recorded by this project's launcher.
python3 - <<'PY'
import os
import signal
import time
from pathlib import Path

root = Path('/mnt/d/KumaProject/KumaTTS')
for filename, expected in [('voice-service.pid', b'feiyu_voice_service'),
                           ('astrbot.pid', b'feiyu-voice/astrbot/bin/python')]:
    path = root / filename
    if not path.exists():
        continue
    pid = int(path.read_text().strip())
    proc = Path(f'/proc/{pid}/cmdline')
    if not proc.exists():
        continue
    if expected not in proc.read_bytes():
        raise RuntimeError(f'{filename} refers to a different process; refusing to stop it')
    os.kill(pid, signal.SIGTERM)
    for _ in range(60):
        if not proc.exists():
            break
        time.sleep(.5)
    else:
        raise RuntimeError(f'Legacy process {pid} did not exit')
    path.unlink()
    print(f'Stopped legacy {filename}: {pid}')
PY
python3 "$framework/scripts/deploy-local-wsl.py" --namespace kumatts --apply
kubectl -n kumatts get pods -o wide
