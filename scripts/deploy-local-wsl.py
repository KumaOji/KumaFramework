#!/usr/bin/env python3
"""Render/validate WSL manifests by default; use --apply to deploy them in order."""
import argparse
import ipaddress
import json
import pathlib
import subprocess
import sys

import yaml

ROOT = pathlib.Path(__file__).resolve().parents[1]
LOCAL = ROOT / "k8s/local"


def kubectl(*args, data=None):
    return subprocess.check_output(
        ["kubectl", *args], input=data, text=True
    )


def required_secrets(resources):
    required = {}

    def scan(value, namespace):
        if isinstance(value, dict):
            for field in ("secretKeyRef", "secretRef"):
                ref = value.get(field)
                if ref and not ref.get("optional", False):
                    required.setdefault((namespace, ref["name"]), set()).update(
                        [ref["key"]] if "key" in ref else []
                    )
            if "secretName" in value and not value.get("optional", False):
                required.setdefault((namespace, value["secretName"]), set())
            for child in value.values():
                scan(child, namespace)
        elif isinstance(value, list):
            for child in value:
                scan(child, namespace)

    for resource in resources:
        scan(resource, resource["metadata"].get("namespace", "default"))
    return required


def check_local_runtime(resources):
    """Validate host mounts, runtime classes and images that cannot be pulled."""
    runtimes = set()
    images = set()
    for resource in resources:
        pod = resource.get("spec", {}).get("template", {}).get("spec", {})
        if pod.get("runtimeClassName"):
            runtimes.add(pod["runtimeClassName"])
        for volume in pod.get("volumes", []):
            host = volume.get("hostPath", {})
            if host.get("type") in ("Directory", "File"):
                path = pathlib.Path(host["path"])
                exists = path.is_dir() if host["type"] == "Directory" else path.is_file()
                if not exists:
                    raise SystemExit(f"Required hostPath is missing or has the wrong type: {path}")
        for container in pod.get("containers", []) + pod.get("initContainers", []):
            if container.get("imagePullPolicy") == "Never":
                images.add(container["image"])
    for runtime in sorted(runtimes):
        kubectl("get", "runtimeclass", runtime, "-o", "name")
    for image in sorted(images):
        subprocess.run(["k3s", "crictl", "inspecti", image],
                       stdout=subprocess.DEVNULL, check=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apply", action="store_true", help="Apply after prerequisite checks")
    parser.add_argument("--render", action="store_true", help="Print resolved YAML only")
    parser.add_argument("--namespace", action="append", choices=sorted(
        path.name for path in LOCAL.iterdir() if path.is_dir()
    ), help="Limit validation/deployment to a namespace; repeat to select several")
    parser.add_argument("--node-ip", help="Kafka advertised IPv4 address (default: node InternalIP)")
    parser.add_argument("--windows-host", help="Windows IPv4 address (default: WSL default gateway)")
    args = parser.parse_args()
    if args.apply and args.render:
        parser.error("--apply and --render are mutually exclusive")

    nodes = json.loads(kubectl("get", "nodes", "-o", "json"))["items"]
    if len(nodes) != 1:
        parser.error("This configuration requires a single-node WSL cluster")
    node_ip = args.node_ip or next(
        item["address"] for item in nodes[0]["status"]["addresses"]
        if item["type"] == "InternalIP"
    )
    if args.windows_host:
        windows_host = args.windows_host
    else:
        routes = json.loads(subprocess.check_output(
            ["ip", "-j", "-4", "route", "show", "default"], text=True
        ))
        windows_host = routes[0]["gateway"]
    node_ip = str(ipaddress.IPv4Address(node_ip))
    windows_host = str(ipaddress.IPv4Address(windows_host))

    resources = list(yaml.safe_load_all(kubectl("kustomize", str(LOCAL))))
    if args.namespace:
        selected = set(args.namespace)
        resources = [resource for resource in resources if (
            resource["metadata"].get("namespace") in selected
            or resource["kind"] == "Namespace" and resource["metadata"]["name"] in selected
        )]
    for resource in resources:
        if resource["kind"] == "Kafka":
            for listener in resource["spec"]["kafka"]["listeners"]:
                for broker in listener.get("configuration", {}).get("brokers", []):
                    broker["advertisedHost"] = node_ip
        if resource["kind"] == "EndpointSlice" and resource["metadata"]["name"].endswith("-local"):
            for endpoint in resource["endpoints"]:
                endpoint["addresses"] = [windows_host]
    if args.render:
        sys.stdout.write(yaml.safe_dump_all(resources, sort_keys=False, allow_unicode=True))
        return

    for (namespace, name), keys in sorted(required_secrets(resources).items()):
        secret = json.loads(kubectl("get", "secret", name, "-n", namespace, "-o", "json"))
        missing = keys - secret.get("data", {}).keys()
        if missing:
            raise SystemExit(f"Missing keys in Secret {namespace}/{name}: {sorted(missing)}")
    check_local_runtime(resources)
    if any(resource["kind"] in ("StatefulSet", "PersistentVolumeClaim", "KafkaNodePool")
           for resource in resources):
        kubectl("get", "storageclass", "local-path", "-o", "name")

    has_kafka = any(resource["kind"] == "Kafka" for resource in resources)
    if args.apply and has_kafka:
        # Helm installs the Strimzi CRDs and operator before Kafka custom resources.
        print(kubectl("apply", "-k", str(LOCAL / "kube-system")), end="", flush=True)
        for crd in ("kafkas.kafka.strimzi.io", "kafkanodepools.kafka.strimzi.io"):
            kubectl("wait", "--for=create", f"crd/{crd}", "--timeout=600s")
            kubectl("wait", "--for=condition=Established", f"crd/{crd}", "--timeout=120s")
        kubectl("wait", "--for=create", "deployment/strimzi-cluster-operator", "-n", "base", "--timeout=600s")
        kubectl("rollout", "status", "deployment/strimzi-cluster-operator", "-n", "base", "--timeout=600s")

    payload = json.dumps({"apiVersion": "v1", "kind": "List", "items": resources})
    print(kubectl("apply", "--dry-run=server", "-f", "-", data=payload), end="", flush=True)
    if args.apply:
        print(kubectl("apply", "-f", "-", data=payload), end="", flush=True)
        for resource in resources:
            if resource["kind"] not in ("StatefulSet", "Deployment"):
                continue
            if resource["spec"].get("replicas", 1) == 0:
                continue
            kubectl("rollout", "status", f'{resource["kind"].lower()}/{resource["metadata"]["name"]}',
                    "-n", resource["metadata"]["namespace"], "--timeout=600s")
        if has_kafka:
            kubectl("wait", "kafka/kafka", "-n", "base", "--for=condition=Ready", "--timeout=600s")
    else:
        print("Validation passed; no resources changed. Use --apply to deploy.")


if __name__ == "__main__":
    main()
