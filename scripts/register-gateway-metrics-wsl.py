#!/usr/bin/env python3
"""Point the local Gateway metrics Service at the current single-node WSL host."""
import json
import subprocess

nodes = json.loads(subprocess.check_output(["kubectl", "get", "nodes", "-o", "json"]))["items"]
if len(nodes) != 1:
    raise RuntimeError("This launcher expects the existing single-node WSL K3s cluster")
address = next(entry["address"] for entry in nodes[0]["status"]["addresses"] if entry["type"] == "InternalIP")
resource = {
    "apiVersion": "discovery.k8s.io/v1", "kind": "EndpointSlice",
    "metadata": {"name": "gateway-local", "namespace": "base", "labels": {
        "kubernetes.io/service-name": "gateway-local", "endpointslice.kubernetes.io/managed-by": "kuma-wsl"}},
    "addressType": "IPv4",
    "ports": [{"name": "metrics", "protocol": "TCP", "port": 18081}],
    "endpoints": [{"addresses": [address], "conditions": {"ready": True}}]
}
subprocess.run(["kubectl", "apply", "-f", "-"], input=json.dumps(resource), text=True, check=True)
