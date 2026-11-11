#!/usr/bin/env python3
"""Validate canonical KumaTTS manifests; do not generate a second deployment source."""
import argparse
import pathlib
import subprocess

import yaml


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--image", action="store_true", help="Print the runtime image used by both Pods")
    args = parser.parse_args()
    deploy = pathlib.Path(__file__).resolve().parent
    resources = list(yaml.safe_load_all(subprocess.check_output(
        ["kubectl", "kustomize", str(deploy)], text=True
    )))
    images = {
        container["image"]
        for resource in resources if resource["kind"] == "Deployment"
        for container in resource["spec"]["template"]["spec"]["containers"]
    }
    if len(images) != 1:
        raise SystemExit("Expected one shared runtime image for voice and AstrBot")
    if args.image:
        print(next(iter(images)))
        return
    subprocess.run([
        "python3", str(deploy.parents[2] / "scripts/deploy-local-wsl.py"),
        "--namespace", "kumatts",
    ], check=True)


if __name__ == "__main__":
    main()
