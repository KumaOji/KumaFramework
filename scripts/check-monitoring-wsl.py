#!/usr/bin/env python3
"""Verify the local monitoring stack through its localhost port forwards."""
import argparse
import base64
import datetime
import json
import pathlib
import secrets
import time
import urllib.parse
import urllib.error
import urllib.request


def request(url, data=None, auth=None):
    headers = {"Content-Type": "application/json"}
    if auth:
        headers["Authorization"] = "Basic " + base64.b64encode(auth.encode()).decode()
    req = urllib.request.Request(
        url, data=json.dumps(data).encode() if data is not None else None, headers=headers
    )
    try:
        with urllib.request.urlopen(req, timeout=10) as response:
            body = response.read()
            return json.loads(body) if body else None
    except urllib.error.HTTPError as error:
        raise RuntimeError(f"{url}: HTTP {error.code}: {error.read().decode()}") from error


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--password-file", default="/home/kuma/.config/kuma-observability/grafana-admin-password")
    args = parser.parse_args()
    password = pathlib.Path(args.password_file).read_text()
    auth = "admin:" + password
    grafana = "http://127.0.0.1:3000"
    prometheus = "http://127.0.0.1:9090"
    alertmanager = "http://127.0.0.1:9093"
    loki = "http://127.0.0.1:3100"
    assert request(grafana + "/api/health")["database"] == "ok"
    for uid in ("prometheus", "loki"):
        result = request(grafana + f"/api/datasources/uid/{uid}/health", auth=auth)
        if result.get("status") != "OK":
            raise RuntimeError(f"Grafana datasource {uid}: {result}")
    # Alertmanager uses the datasource proxy; it has no backend health-check handler.
    status = request(grafana + "/api/datasources/proxy/uid/alertmanager/api/v2/status", auth=auth)
    if not status.get("versionInfo", {}).get("version"):
        raise RuntimeError(f"Grafana Alertmanager proxy connection: {status}")
    print("PASS: Grafana login and all three provisioned datasource connections")
    deadline = time.monotonic() + 60
    while True:
        targets = request(prometheus + "/api/v1/targets")["data"]["activeTargets"]
        if len(targets) >= 5 and all(target["health"] == "up" for target in targets):
            break
        if time.monotonic() >= deadline:
            raise RuntimeError(f"Prometheus targets not ready: {targets}")
        time.sleep(3)
    managers = request(prometheus + "/api/v1/alertmanagers")["data"]["activeAlertmanagers"]
    if not managers:
        raise RuntimeError("Prometheus has no active Alertmanager")
    print(f"PASS: {len(targets)} Prometheus scrape targets and Alertmanager discovery")
    test_id = secrets.token_hex(8)
    line = "Kuma local monitoring smoke " + test_id
    timestamp = time.time_ns()
    request(loki + "/loki/api/v1/push", {"streams": [{
        "stream": {"service_name": "kuma-monitoring-smoke"},
        "values": [[str(timestamp), line]]
    }]})
    query = urllib.parse.urlencode({
        "query": '{service_name="kuma-monitoring-smoke"} |= "' + test_id + '"',
        "start": str(timestamp - 60_000_000_000), "end": str(timestamp + 60_000_000_000)
    })
    deadline = time.monotonic() + 30
    while True:
        result = request(loki + "/loki/api/v1/query_range?" + query)
        if any(value[1] == line for stream in result["data"]["result"] for value in stream["values"]):
            break
        if time.monotonic() >= deadline:
            raise RuntimeError("Loki did not return the test log")
        time.sleep(2)
    print("PASS: Loki log ingestion and query")
    now = datetime.datetime.now(datetime.timezone.utc)
    request(alertmanager + "/api/v2/alerts", [{
        "labels": {"alertname": "KumaMonitoringSmoke", "severity": "info", "test_id": test_id},
        "annotations": {"summary": "Local deployment validation; expires in one minute"},
        "startsAt": now.isoformat(), "endsAt": (now + datetime.timedelta(minutes=1)).isoformat()
    }])
    alerts = request(alertmanager + "/api/v2/alerts")
    if not any(alert["labels"].get("test_id") == test_id for alert in alerts):
        raise RuntimeError("Alertmanager did not return the test alert")
    print("PASS: Alertmanager accepts and exposes alerts (local receiver, no notifications)")


if __name__ == "__main__":
    main()
