#!/usr/bin/env bash
set -euo pipefail
if ! command -v nvidia-container-runtime >/dev/null; then
    curl -fsSL --retry 3 https://nvidia.github.io/libnvidia-container/gpgkey |
        gpg --dearmor --yes -o /usr/share/keyrings/nvidia-container-toolkit-keyring.gpg
    curl -fsSL --retry 3 https://nvidia.github.io/libnvidia-container/stable/deb/nvidia-container-toolkit.list |
        sed 's#deb https://#deb [signed-by=/usr/share/keyrings/nvidia-container-toolkit-keyring.gpg] https://#g' \
        > /etc/apt/sources.list.d/nvidia-container-toolkit.list
    apt-get update
    DEBIAN_FRONTEND=noninteractive apt-get install -y nvidia-container-toolkit
fi
# K3s detects this runtime on startup; the default runtime stays unchanged.
if ! grep -q 'nvidia-container-runtime' /var/lib/rancher/k3s/agent/etc/containerd/config.toml; then
    systemctl restart k3s
fi
