#!/usr/bin/env python3
"""Apply a manifest while preserving omitted, provisioner-set PVC fields."""
import argparse
import json
import subprocess


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("manifest")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()
    document = json.loads(subprocess.check_output([
        "kubectl", "create", "--dry-run=client", "-f", args.manifest, "-o", "json"
    ], text=True))
    resources = document.get("items", [document])
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
