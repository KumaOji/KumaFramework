#!/usr/bin/env python3
"""Send an OTLP trace through Collector and verify it in SkyWalking storage.

Run inside the K3s host with kubectl access, or pass two forwarded endpoint URLs.
"""
import argparse
import json
import secrets
import subprocess
import time
import urllib.error
import urllib.request


def service_ip(name):
    return subprocess.check_output(
        ["kubectl", "-n", "base", "get", "svc", name,
         "-o", "jsonpath={.spec.clusterIP}"], text=True
    ).strip()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--collector", help="Collector HTTP URL, e.g. http://localhost:4318")
    parser.add_argument("--query", help="OAP Zipkin query URL, e.g. http://localhost:9412")
    args = parser.parse_args()
    collector = args.collector or f"http://{service_ip('otel-collector')}:4318"
    query = args.query or f"http://{service_ip('skywalking-oap')}:9412"
    trace_id = secrets.token_hex(16)
    start = time.time_ns()
    payload = {"resourceSpans": [{
        "resource": {"attributes": [{"key": "service.name", "value": {
            "stringValue": "kuma-observability-smoke"}}]},
        "scopeSpans": [{"scope": {"name": "kuma-deployment-check"}, "spans": [{
            "traceId": trace_id, "spanId": secrets.token_hex(8),
            "name": "collector-to-skywalking", "kind": 2,
            "startTimeUnixNano": str(start), "endTimeUnixNano": str(start + 10_000_000),
            "status": {"code": 1}
        }]}]
    }]}
    request = urllib.request.Request(
        collector.rstrip("/") + "/v1/traces",
        data=json.dumps(payload).encode(), headers={"Content-Type": "application/json"}
    )
    with urllib.request.urlopen(request, timeout=15) as response:
        result = json.load(response)
    if result.get("partialSuccess", {}).get("rejectedSpans", "0") not in (0, "0"):
        raise RuntimeError(f"Collector rejected spans: {result}")
    trace_url = query.rstrip("/") + "/zipkin/api/v2/trace/" + trace_id
    deadline = time.monotonic() + 90
    last_error = None
    while time.monotonic() < deadline:
        try:
            with urllib.request.urlopen(trace_url, timeout=10) as response:
                spans = json.load(response)
            if any(span.get("traceId") == trace_id for span in spans):
                print(f"PASS: Collector -> SkyWalking -> persistent storage; traceId={trace_id}")
                return
        except (urllib.error.URLError, ValueError) as error:
            last_error = error
        time.sleep(3)
    raise RuntimeError(f"Trace {trace_id} not queryable after 90s; last error: {last_error}")


if __name__ == "__main__":
    main()
