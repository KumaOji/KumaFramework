#!/usr/bin/env python3
"""Verify the live Gateway -> Blog -> UAA chain, telemetry and management isolation."""
import base64
import json
import pathlib
import secrets
import subprocess
import time
import urllib.error
import urllib.parse
import urllib.request

opener = urllib.request.build_opener(urllib.request.ProxyHandler({}))
host = json.loads(subprocess.check_output(["ip", "-j", "-4", "route", "show", "default"]))[0]["gateway"]


def get(url, headers=None):
    try:
        response = opener.open(urllib.request.Request(url, headers=headers or {}), timeout=8)
    except urllib.error.HTTPError as error:
        response = error
    with response:
        return response.status, dict(response.headers), response.read().decode()


def poll(check, description, timeout=90):
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        try:
            value = check()
            if value:
                print("PASS: " + description, flush=True)
                return value
        except (urllib.error.URLError, TimeoutError, ConnectionError):
            pass
        time.sleep(2)
    raise RuntimeError("Timed out: " + description)


services = {"blog": (9000, 19001, "/api"), "uaa": (33336, 19002, ""), "gateway": (18080, 18081, "")}
for name, (port, management, context) in services.items():
    address = "127.0.0.1" if name == "gateway" else host
    base = f"http://{address}:{management}"
    poll(lambda: get(base + "/actuator/health/readiness")[0] == 200, name + " ready")
    status, _, metrics = get(base + "/actuator/prometheus")
    assert status == 200 and "jvm_memory_used_bytes" in metrics and "# HELP" in metrics, (name, status)
    business = f"http://{address}:{port}{context}"
    assert get(business + "/actuator/prometheus")[0] in (401, 403, 404), name
    assert get(base + "/actuator/env")[0] in (401, 403, 404), name
print("PASS: JVM metrics and isolated management endpoints on all three services", flush=True)

# A sampled parent makes the check deterministic even with the default 10% sampling.
trace_id = secrets.token_hex(16)
marker = "observability-secret-" + secrets.token_hex(8)
headers = {"traceparent": f"00-{trace_id}-{secrets.token_hex(8)}-01"}
status, response_headers, body = get("http://127.0.0.1:18080/blog/auth/totp/feature?token=" + marker, headers)
assert status == 200, (status, body)
assert {key.lower(): value for key, value in response_headers.items()}["kmc-trace-id"] == trace_id
print("PASS: Gateway -> Blog feature endpoint; traceId=" + trace_id, flush=True)
assert get("http://127.0.0.1:18080/blog/article/list")[0] == 200
assert get("http://127.0.0.1:18080/uaa/oauth2/jwks")[0] == 200
assert get(f"http://{host}:9000/api/auth/current")[0] == 401
assert get(f"http://{host}:33336/api/admin/users", {"Accept": "application/json"})[0] in (401, 403)
print("PASS: Blog data, UAA JWK routing and unauthenticated access rejection", flush=True)


def traces():
    status, _, body = get("http://127.0.0.1:9412/zipkin/api/v2/trace/" + trace_id)
    if status != 200:
        return None
    spans = json.loads(body)
    names = {span.get("localEndpoint", {}).get("serviceName") for span in spans}
    if not {"kuma-cloud-gateway", "kuma-cloud-blog", "kuma-cloud-uaa"}.issubset(names):
        return None
    assert all(span["traceId"] == trace_id for span in spans)
    blog_clients = [span for span in spans if span.get("kind") == "CLIENT"
                    and span.get("localEndpoint", {}).get("serviceName") == "kuma-cloud-blog"]
    uaa_servers = [span for span in spans if span.get("kind") == "SERVER"
                   and span.get("localEndpoint", {}).get("serviceName") == "kuma-cloud-uaa"]
    assert any(server.get("parentId") == client["id"] for server in uaa_servers for client in blog_clients)
    return spans


spans = poll(traces, "one connected trace stored in SkyWalking across all three services")
query = urllib.parse.urlencode({"query": '{service_name=~"kuma-cloud-(blog|uaa|gateway)"} |= "' + trace_id + '"',
                               "start": str(time.time_ns() - 300_000_000_000), "limit": 200})


def logs():
    status, _, body = get("http://127.0.0.1:3100/loki/api/v1/query_range?" + query)
    if status != 200:
        return False
    streams = json.loads(body)["data"]["result"]
    names = {stream["stream"].get("service_name") for stream in streams}
    lines = [value[1] for stream in streams for value in stream["values"]]
    assert all(marker not in line for line in lines), "Query value leaked into logs"
    return {"kuma-cloud-gateway", "kuma-cloud-blog", "kuma-cloud-uaa"}.issubset(names)


poll(logs, "correlated logs in Loki from all three services, without query secrets")


def targets():
    status, _, body = get("http://127.0.0.1:9090/api/v1/targets")
    if status != 200:
        return False
    selected = {target["labels"]["job"]: target for target in json.loads(body)["data"]["activeTargets"]
                if target["labels"]["job"] in services}
    return len(selected) == 3 and all(target["health"] == "up" for target in selected.values())


poll(targets, "Prometheus scrapes Blog, UAA and Gateway")
rules = json.loads(get("http://127.0.0.1:9090/api/v1/rules")[2])
rule_names = {rule["name"] for group in rules["data"]["groups"] for rule in group["rules"]}
assert {"MonitoringTargetDown", "ServiceHighErrorRate", "ServiceHighLatency"}.issubset(rule_names)
assert get("http://127.0.0.1:9093/-/ready")[0] == 200
print("PASS: service availability, error-rate and latency rules loaded; Alertmanager ready", flush=True)

password = pathlib.Path("/home/kuma/.config/kuma-observability/grafana-admin-password").read_text().strip()
auth = {"Authorization": "Basic " + base64.b64encode(("admin:" + password).encode()).decode()}
poll(lambda: get("http://127.0.0.1:3000/api/health")[0] == 200, "Grafana ready")
status, _, body = get("http://127.0.0.1:3000/api/dashboards/uid/kuma-services", auth)
assert status == 200 and len(json.loads(body)["dashboard"]["panels"]) == 8
for uid in ("prometheus", "loki", "skywalking-zipkin"):
    assert get("http://127.0.0.1:3000/api/datasources/uid/" + uid + "/health", auth)[0] == 200, uid
status, _, body = get("http://127.0.0.1:3000/api/datasources/proxy/uid/skywalking-zipkin/api/v2/trace/" + trace_id, auth)
assert status == 200 and len(json.loads(body)) >= 5
print("PASS: Grafana three-service dashboard, healthy data sources and trace query", flush=True)
print(json.dumps({"traceId": trace_id, "spans": len(spans), "dashboard": "http://localhost:3000/d/kuma-services"}), flush=True)
