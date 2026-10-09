#!/usr/bin/env python3
"""Register Windows-hosted servlet metrics in the local single-node WSL cluster."""
import ipaddress
import json
import subprocess
import sys

address = str(ipaddress.IPv4Address(sys.argv[1]))
for service, port in (("blog", 19001), ("uaa", 19002)):
    name = service + "-local"
    resources = [
        {"apiVersion": "v1", "kind": "Service", "metadata": {"name": name, "namespace": "base"},
         "spec": {"ports": [{"name": "metrics", "port": port, "targetPort": port}]}},
        {"apiVersion": "discovery.k8s.io/v1", "kind": "EndpointSlice",
         "metadata": {"name": name, "namespace": "base", "labels": {
             "kubernetes.io/service-name": name, "endpointslice.kubernetes.io/managed-by": "kuma-wsl"}},
         "addressType": "IPv4", "ports": [{"name": "metrics", "protocol": "TCP", "port": port}],
         "endpoints": [{"addresses": [address], "conditions": {"ready": True}}]}
    ]
    subprocess.run(["kubectl", "apply", "-f", "-"],
                   input=json.dumps({"apiVersion": "v1", "kind": "List", "items": resources}),
                   text=True, check=True)
