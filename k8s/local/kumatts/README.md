# KumaTTS on the local WSL k3s node

Namespace: `kumatts`. Deployments: `astrbot` and `voice`, one replica each.

Canonical deployment directory: `D:\KumaProject\KumaFramework\k8s\local\kumatts`.
The original `D:\KumaProject\KumaTTS\deploy\k3s` paths now forward to this directory.
`namespace.yaml`, `astrbot.yaml` and `voice.yaml` are the only deployment sources;
`Containerfile` and lifecycle tools are maintained alongside them. Application source
remains in `D:\KumaProject\KumaTTS`.
The image uses Ubuntu 24.04 system libraries and mounts this machine's existing
Python environments and model files at their original paths. It is a local-node
deployment, not a standalone image for another machine.

Both Pods use host networking to retain Windows-to-WSL localhost access and the
existing plugin endpoint. AstrBot remains at http://localhost:6185 and the voice
API at http://localhost:9880. No new public ingress is configured.

Code, Python environments and models are mounted read-only. AstrBot `data`, voice
`.runtime` and model caches are writable host directories. The original credentials
and history remain there; the manifest contains no API keys or passwords.

The voice Pod uses `runtimeClassName: nvidia` and `NVIDIA_VISIBLE_DEVICES=all`.
GPU access is shared with other host processes; no device plugin or exclusive GPU
reservation is configured. Models still load per job and release VRAM on exit.
K3s restarts failed containers, but restarting an active inference job interrupts it.
WSL must be running for the services to be available.

## Verified on this machine

On 2026-11-11 the voice Pod recognized the RTX 5080 using PyTorch 2.11.0+cu128.
Actual authenticated HTTP jobs passed for cute TTS, gentle TTS and RVC conversion,
including downloaded WAV hash, finite-sample and non-silence checks. Audio and
receipts are in `D:\KumaProject\KumaTTS\acceptance-k3s`; listening quality remains for the user to
judge. Windows localhost checks returned HTTP 200 for the UI and voice health API.
The optional WeText frontend still falls back after ModelScope download errors,
as it did before migration; this can add delay to per-job model initialization.

## Manage from Windows PowerShell

```powershell
& D:\KumaProject\KumaTTS\Start-KumaTTS.ps1
wsl -d Ubuntu-24.04 -u root -- kubectl -n kumatts get pods -o wide
wsl -d Ubuntu-24.04 -u root -- kubectl -n kumatts logs deployment/voice --tail=100
wsl -d Ubuntu-24.04 -u root -- kubectl -n kumatts logs deployment/astrbot --tail=100
wsl -d Ubuntu-24.04 -u root -- kubectl -n kumatts scale deployment/voice deployment/astrbot --replicas=0
```

Use the scale-to-zero command to stop both applications while preserving data.
Do not run the saved legacy launcher at the same time as these Pods.

## Rebuild on this WSL host

Run as root inside Ubuntu-24.04. Buildah and NVIDIA Container Toolkit are installed.
The toolkit was installed using NVIDIA's official apt repository; K3s was restarted
once to discover the NVIDIA runtime, without changing its default runtime.
Runtime setup follows the [K3s NVIDIA configuration](https://docs.k3s.io/advanced#nvidia-container-runtime-support)
and [NVIDIA Container Toolkit installation guide](https://docs.nvidia.com/datacenter/cloud-native/container-toolkit/latest/install-guide.html).

```bash
deploy=/mnt/d/KumaProject/KumaFramework/k8s/local/kumatts
buildah bud --layers --isolation chroot --network host \
  -t localhost/kumatts-runtime:20261111-v2 \
  -f "$deploy/Containerfile" "$deploy"
bash "$deploy/migrate.sh"
```

`prepare.py` validates the canonical YAML instead of generating `kumatts.json`.
`migrate.sh` reads the image tag from both Deployment manifests, imports the image
into k3s, validates prerequisites, stops only PID-verified legacy project processes,
and deploys only `kumatts`. Update both Deployment image tags together for future image changes.

Read-only Kubernetes configuration validation:

```bash
python3 /mnt/d/KumaProject/KumaFramework/scripts/deploy-local-wsl.py --namespace kumatts
```

The shared deployment script checks host directories, the NVIDIA RuntimeClass and
the imported image before applying. It does not build/import images or stop legacy
processes. Use `migrate.sh` explicitly for those operations; normal configuration
validation does not run inference or touch running jobs.

## Return to legacy processes

First scale both deployments to zero and wait for their Pods to terminate. Then
run `bash /mnt/d/KumaProject/KumaFramework/k8s/local/kumatts/start-services-legacy.sh` as user
`kuma`. This uses the same original data. The normal project launcher now starts
the k3s deployments, so do not use it while testing legacy mode.
