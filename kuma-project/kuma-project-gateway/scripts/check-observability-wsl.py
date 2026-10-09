#!/usr/bin/env python3
"""Run a real Gateway against a temporary upstream and verify logs, traces and metrics."""
import argparse
import http.server
import json
import pathlib
import secrets
import shutil
import subprocess
import tempfile
import threading
import time
import urllib.error
import urllib.parse
import urllib.request


def get(url, headers=None):
    req = urllib.request.Request(url, headers=headers or {})
    try:
        response = urllib.request.urlopen(req, timeout=5)
    except urllib.error.HTTPError as error:
        response = error
    with response:
        body = response.read().decode()
        return response.status, dict(response.headers), body


def poll(check, timeout=90):
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        try:
            if check():
                return
        except (urllib.error.URLError, ValueError, ConnectionError):
            pass
        time.sleep(2)
    raise RuntimeError("Timed out waiting for Gateway telemetry")


class Upstream(http.server.BaseHTTPRequestHandler):
    def do_GET(self):
        body = json.dumps({key.lower(): value for key, value in self.headers.items()}).encode()
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def log_message(self, *_):
        pass


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("jar", help="Built Gateway bootJar")
    args = parser.parse_args()
    backend = http.server.ThreadingHTTPServer(("127.0.0.1", 0), Upstream)
    threading.Thread(target=backend.serve_forever, daemon=True).start()
    log_path = pathlib.Path(tempfile.gettempdir()) / ("gateway-observability-" + secrets.token_hex(4) + ".log")
    artifact_dir = tempfile.TemporaryDirectory(prefix="gateway-observability-artifact-")
    artifact = pathlib.Path(artifact_dir.name) / "gateway.jar"
    shutil.copyfile(args.jar, artifact)
    command = ["java", "--enable-preview", "--enable-native-access=ALL-UNNAMED",
               "-Dlogging.config=classpath:logback-gateway.xml", "-jar", str(artifact),
               "--spring.profiles.active=observability", "--spring.config.import=",
               "--spring.cloud.nacos.config.enabled=false", "--spring.cloud.nacos.discovery.enabled=false",
               "--kuma.boot.monitor.enabled=false", "--kuma.boot.ip2region.enabled=false",
               "--kuma.boot.logger.logging.files.enabled=false", "--management.health.redis.enabled=false",
               "--server.port=18180", "--server.address=127.0.0.1", "--management.server.port=18181",
               "--management.server.address=127.0.0.1",
               "--management.tracing.sampling.probability=1",
               "--spring.cloud.gateway.server.webflux.routes[0].id=observability-check",
               f"--spring.cloud.gateway.server.webflux.routes[0].uri=http://127.0.0.1:{backend.server_port}",
               "--spring.cloud.gateway.server.webflux.routes[0].predicates[0]=Path=/__gateway_observability/**"]
    with log_path.open("w") as log_file:
        process = subprocess.Popen(command, stdout=log_file, stderr=subprocess.STDOUT)
        try:
            def ready():
                if process.poll() is not None:
                    raise RuntimeError(f"Gateway exited with code {process.returncode}")
                return get("http://127.0.0.1:18181/actuator/health/readiness")[0] == 200
            poll(ready)
            trace_id = secrets.token_hex(16)
            status, headers, body = get("http://127.0.0.1:18180/__gateway_observability/probe?token=should-not-log", {
                "traceparent": "00-" + trace_id + "-" + secrets.token_hex(8) + "-01",
                "Authorization": "Bearer should-not-log",
            })
            downstream = json.loads(body)
            assert status == 200, (status, body)
            assert headers["kmc-trace-id"] == trace_id, headers
            assert "X-Response-Time" in headers, headers
            assert downstream["traceparent"].split("-")[1] == trace_id, downstream
            assert downstream["kmc-trace-id"] == trace_id, downstream
            status, headers, body = get("http://127.0.0.1:18180/__gateway_missing")
            assert status == 404
            assert json.loads(body)["requestId"] == headers["kmc-trace-id"]
            assert get("http://127.0.0.1:18180/actuator/prometheus")[0] in (401, 403, 404)
            metrics = get("http://127.0.0.1:18181/actuator/prometheus")[2]
            assert "spring_cloud_gateway_requests_seconds_count" in metrics
            assert "http_server_requests_seconds_bucket" in metrics
            assert "jvm_memory_used_bytes" in metrics
            print("PASS: real routing, W3C propagation, correlated errors, timing header and protected metrics", flush=True)
            query = urllib.parse.urlencode({
                "query": '{service_name="kuma-cloud-gateway"} |= "' + trace_id + '"',
                "start": str(time.time_ns() - 120_000_000_000), "limit": 100
            })
            def logs_arrived():
                _, _, body = get("http://127.0.0.1:3100/loki/api/v1/query_range?" + query)
                lines = [value[1] for stream in json.loads(body)["data"]["result"] for value in stream["values"]]
                access = [json.loads(line) for line in lines if "Gateway request completed" in line]
                if not access:
                    return False
                assert all("should-not-log" not in json.dumps(line) for line in access)
                assert any(line.get("path") == "/__gateway_observability/probe" and line.get("traceId") == trace_id for line in access)
                return True
            poll(logs_arrived)
            print("PASS: correlated access log in Loki, without Authorization or query-string values", flush=True)
            def trace_arrived():
                status, _, body = get("http://127.0.0.1:9412/zipkin/api/v2/trace/" + trace_id)
                if status != 200:
                    return False
                spans = json.loads(body)
                return len(spans) >= 2 and all(span["traceId"] == trace_id for span in spans)
            poll(trace_arrived)
            print("PASS: Gateway server/client spans stored and queryable in SkyWalking; traceId=" + trace_id, flush=True)
        except Exception:
            print("Gateway runtime log: " + str(log_path), flush=True)
            raise
        finally:
            process.terminate()
            try:
                process.wait(timeout=45)
            except subprocess.TimeoutExpired:
                process.kill()
                process.wait()
            backend.shutdown()
            backend.server_close()
            artifact_dir.cleanup()


if __name__ == "__main__":
    main()
