#!/usr/bin/env python3
"""Apply manifests, replacing probe handlers and preserving provisioner-set PVC fields."""
import argparse
import json
import subprocess


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("manifest")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--image", help="Set application placeholder images atomically with configuration")
    args = parser.parse_args()
    output = subprocess.check_output([
        "kubectl", "create", "--dry-run=client", "-f", args.manifest, "-o", "json"
    ], text=True)
    # kubectl versions may emit a List or consecutive JSON objects for multi-doc YAML.
    decoder = json.JSONDecoder()
    resources = []
    remaining = output.strip()
    while remaining:
        resource, end = decoder.raw_decode(remaining)
        resources.extend(resource["items"] if resource.get("kind") == "List" else [resource])
        remaining = remaining[end:].lstrip()
    document = {"apiVersion": "v1", "kind": "List", "items": resources}
    # Strategic merge retains old handlers unless they are explicitly removed.
    # A TCP -> HTTP migration otherwise creates an invalid probe with two handlers.
    handlers = ("exec", "httpGet", "tcpSocket", "grpc")
    for resource in resources:
        pod_spec = resource.get("spec", {}).get("template", {}).get("spec", {})
        for container in pod_spec.get("containers", []) + pod_spec.get("initContainers", []):
            for name in ("startupProbe", "readinessProbe", "livenessProbe"):
                probe = container.get(name)
                if probe and sum(probe.get(handler) is not None for handler in handlers) == 1:
                    for handler in handlers:
                        probe.setdefault(handler, None)
    if args.image:
        for resource in resources:
            for container in resource.get("spec", {}).get("template", {}).get("spec", {}).get("containers", []):
                if container.get("image", "").endswith(":placeholder"):
                    container["image"] = args.image
    for resource in resources:
        if resource.get("kind") != "PersistentVolumeClaim":
            continue
        metadata = resource["metadata"]
        existing = subprocess.check_output([
            "kubectl", "get", "pvc", metadata["name"],
            "-n", metadata.get("namespace", "default"),
            "--ignore-not-found", "-o", "json"
        ], text=True).strip()
        if not existing:
            continue
        live_spec = json.loads(existing)["spec"]
        for key in ("volumeName", "storageClassName", "volumeMode"):
            if key not in resource["spec"] and key in live_spec:
                resource["spec"][key] = live_spec[key]
    command = ["kubectl", "apply", "-f", "-"]
    if args.dry_run:
        command.append("--dry-run=server")
    subprocess.run(command, input=json.dumps(document), text=True, check=True)


if __name__ == "__main__":
    main()
