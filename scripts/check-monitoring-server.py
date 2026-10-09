#!/usr/bin/env python3
"""Verify server monitoring infrastructure and optionally the live three-service chain and Lab."""
import argparse
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


def get(url, headers=None, body=None):
    data = json.dumps(body).encode() if body is not None else None
    headers = dict(headers or {})
    if data is not None:
        headers["Content-Type"] = "application/json"
    try:
        response = opener.open(urllib.request.Request(url, data=data, headers=headers), timeout=120 if body is not None else 8)
    except urllib.error.HTTPError as error:
        response = error
    with response:
        return response.status, dict(response.headers), response.read().decode()


def poll(check, label, timeout=120):
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        try:
            result = check()
            if result:
                print("PASS: " + label, flush=True)
                return result
        except (urllib.error.URLError, TimeoutError, ConnectionError):
            pass
        time.sleep(2)
    raise RuntimeError("Timed out: " + label)


def service(name, namespace, port):
    resource = json.loads(subprocess.check_output(["kubectl", "-n", namespace, "get", "svc", name, "-o", "json"]))
    return "http://" + resource["spec"]["clusterIP"] + ":" + str(port)


def unwrap(body):
    document = json.loads(body)
    if "payload" in document:
        document = document["payload"]
    return document.get("data", document)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--infrastructure-only", action="store_true")
    args = parser.parse_args()
    grafana = service("grafana", "base", 3000)
    prometheus = service("prometheus", "base", 9090)
    loki = service("loki", "base", 3100)
    alertmanager = service("alertmanager", "base", 9093)
    skywalking = service("skywalking-oap", "base", 9412)
    password = pathlib.Path("/data/kuma-observability/grafana-admin-password").read_text().strip()
    auth = {"Authorization": "Basic " + base64.b64encode(("admin:" + password).encode()).decode()}
    poll(lambda: get(grafana + "/api/health")[0] == 200, "Grafana ready")
    for uid in ("prometheus", "loki", "skywalking-zipkin"):
        poll(lambda uid=uid: get(grafana + "/api/datasources/uid/" + uid + "/health", auth)[0] == 200,
             "Grafana data source " + uid)
    assert get(grafana + "/api/dashboards/uid/kuma-services", auth)[0] == 200
    assert get(alertmanager + "/-/ready")[0] == 200
    rules = json.loads(get(prometheus + "/api/v1/rules")[2])
    names = {rule["name"] for group in rules["data"]["groups"] for rule in group["rules"]}
    assert {"MonitoringTargetDown", "ServiceHighErrorRate", "ServiceHighLatency"}.issubset(names)
    print("PASS: provisioned three-service dashboard, Alertmanager and service alert rules", flush=True)
    marker = "server-monitoring-" + secrets.token_hex(8)
    status, _, _ = get(loki + "/loki/api/v1/push", body={"streams": [{
        "stream": {"service_name": "kuma-server-monitoring-smoke", "environment": "server"},
        "values": [[str(time.time_ns()), marker]]}]})
    assert status == 204
    query = urllib.parse.urlencode({"query": '{service_name="kuma-server-monitoring-smoke"} |= "' + marker + '"', "limit": 20})
    poll(lambda: marker in get(loki + "/loki/api/v1/query_range?" + query)[2], "Loki write/query round trip")
    if args.infrastructure_only:
        return
    gateway = service("gateway", "base", 18080)
    blog = service("blog-background", "blog", 9000)
    uaa = service("uaa", "base", 33336)
    for name, namespace, port in (("gateway", "base", 18081), ("blog-background", "blog", 19001), ("uaa", "base", 19002)):
        management = service(name, namespace, port)
        poll(lambda management=management: get(management + "/actuator/health/readiness")[0] == 200, name + " ready")
    for base, context in ((gateway, ""), (blog, "/api"), (uaa, "")):
        assert get(base + context + "/actuator/prometheus")[0] in (401, 403, 404)
    assert get(gateway + "/blog/article/list")[0] == 200
    assert get(gateway + "/uaa/oauth2/jwks")[0] == 200
    assert get(uaa + "/api/admin/users", {"Accept": "application/json"})[0] == 401
    print("PASS: real Blog data, UAA JWK routing, 401 authentication and management isolation", flush=True)
    trace_id = secrets.token_hex(16)
    headers = {"traceparent": "00-" + trace_id + "-" + secrets.token_hex(8) + "-01"}
    status, response_headers, body = get(gateway + "/blog/auth/totp/feature", headers)
    assert status == 200
    assert {key.lower(): value for key, value in response_headers.items()}["kmc-trace-id"] == trace_id
    started = time.monotonic()
    retried = False

    def trace_arrived():
        nonlocal retried
        status, _, body = get(skywalking + "/zipkin/api/v2/trace/" + trace_id)
        spans = json.loads(body) if status == 200 else []
        services = {span.get("localEndpoint", {}).get("serviceName") for span in spans}
        if {"kuma-cloud-gateway", "kuma-cloud-blog", "kuma-cloud-uaa"}.issubset(services):
            return spans
        # Blog caches UAA auth settings for 30 seconds; retry after expiry if the first request hit the cache.
        if time.monotonic() - started >= 32 and not retried:
            retried = True
            assert get(gateway + "/blog/auth/totp/feature", headers)[0] == 200
        return None

    spans = poll(trace_arrived, "connected Gateway -> Blog -> UAA trace stored in SkyWalking")
    query = urllib.parse.urlencode({"query": '{service_name=~"kuma-cloud-(blog|uaa|gateway)"} |= "' + trace_id + '"', "limit": 200})

    def logs_arrived():
        streams = json.loads(get(loki + "/loki/api/v1/query_range?" + query)[2])["data"]["result"]
        return {"kuma-cloud-gateway", "kuma-cloud-blog", "kuma-cloud-uaa"}.issubset(
            {stream["stream"].get("service_name") for stream in streams})

    poll(logs_arrived, "same traceId in Loki logs from all three applications")

    def targets_ready():
        targets = json.loads(get(prometheus + "/api/v1/targets")[2])["data"]["activeTargets"]
        return len(targets) >= 8 and all(target["health"] == "up" for target in targets)

    poll(targets_ready, "all eight Prometheus targets up")
    lab = "http://127.0.0.1:19090"
    poll(lambda: get(lab + "/api/lab/monitoring/guide")[0] == 200, "internal Lab available through loopback forward")
    for component in ("loki", "prometheus", "alertmanager", "otel", "skywalking", "grafana"):
        status, _, body = get(lab + "/api/lab/monitoring/" + component, body={})
        report = unwrap(body)
        assert status == 200 and report.get("component") == component and all(report["checks"].values()), component
        print("PASS: server Lab " + component, flush=True)
    print(json.dumps({"traceId": trace_id, "spans": len(spans), "targets": 8, "labExperiments": 6}), flush=True)


if __name__ == "__main__":
    main()
