import importlib.util
import json
from pathlib import Path
import unittest
from unittest.mock import patch

spec = importlib.util.spec_from_file_location("apply_k8s_resource", Path(__file__).with_name("apply-k8s-resource.py"))
apply_resource = importlib.util.module_from_spec(spec)
spec.loader.exec_module(apply_resource)


class ApplyResourceTest(unittest.TestCase):
    def test_probe_migration_image_and_bound_pvc_survive_dry_run(self):
        deployment = {"kind": "Deployment", "spec": {"template": {"spec": {"containers": [{
            "name": "app", "image": "app:placeholder",
            "readinessProbe": {"httpGet": {"path": "/ready", "port": 8081}},
            "startupProbe": {"tcpSocket": {"port": 8080}},
        }]}}}}
        pvc = {"kind": "PersistentVolumeClaim", "metadata": {"name": "data", "namespace": "base"},
               "spec": {"accessModes": ["ReadWriteOnce"]}}
        live = {"spec": {"volumeName": "bound-volume", "storageClassName": "local-path", "volumeMode": "Filesystem"}}
        with patch("sys.argv", ["apply", "manifest.yaml", "--image", "app:release", "--dry-run"]), \
                patch.object(apply_resource.subprocess, "check_output", side_effect=[
                    json.dumps({"kind": "List", "items": [deployment, pvc]}), json.dumps(live)]), \
                patch.object(apply_resource.subprocess, "run") as run:
            apply_resource.main()
        self.assertIn("--dry-run=server", run.call_args.args[0])
        applied = json.loads(run.call_args.kwargs["input"])["items"]
        app = applied[0]["spec"]["template"]["spec"]["containers"][0]
        self.assertEqual(app["image"], "app:release")
        self.assertEqual(app["readinessProbe"]["httpGet"], {"path": "/ready", "port": 8081})
        for handler in ("tcpSocket", "exec", "grpc"):
            self.assertIn(handler, app["readinessProbe"])
            self.assertIsNone(app["readinessProbe"][handler])
        self.assertIsNone(app["startupProbe"]["httpGet"])
        self.assertEqual(app["startupProbe"]["tcpSocket"], {"port": 8080})
        self.assertEqual(applied[1]["spec"]["volumeName"], "bound-volume")
        self.assertEqual(applied[1]["spec"]["storageClassName"], "local-path")


if __name__ == "__main__":
    unittest.main()
