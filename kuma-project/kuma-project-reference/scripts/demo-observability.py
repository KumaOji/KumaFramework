"""Create demo orders and emit success, conflict, validation and missing-order signals."""
import argparse
import json
import secrets
import time
import urllib.error
import urllib.request


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--base-url", default="http://127.0.0.1:19092")
    parser.add_argument("--rounds", type=int, default=3)
    parser.add_argument("--interval", type=float, default=15)
    args = parser.parse_args()
    if not 1 <= args.rounds <= 100 or not 0 <= args.interval <= 60:
        parser.error("rounds must be 1..100; interval must be 0..60 seconds")
    opener = urllib.request.build_opener(urllib.request.ProxyHandler({}))

    def request(method, path, expected, body=None):
        trace = secrets.token_hex(16)
        headers = {"Content-Type": "application/json", "X-User-Id": "reference-demo",
                   "X-User-Name": "Reference Demo", "traceparent": f"00-{trace}-{secrets.token_hex(8)}-01"}
        req = urllib.request.Request(args.base_url.rstrip("/") + path, method=method, headers=headers,
                                     data=json.dumps(body).encode() if body is not None else None)
        try:
            response = opener.open(req, timeout=10)
        except urllib.error.HTTPError as error:
            response = error
        with response:
            result = json.loads(response.read())
            if response.status != expected:
                raise RuntimeError(f"{method} {path}: expected {expected}, got {response.status}")
            print(json.dumps({"method": method, "path": path, "status": response.status,
                              "traceId": trace, "requestId": result.get("requestId")}), flush=True)
            return result.get("data")

    # These are real sample orders; existing data is left intact.
    for index in range(args.rounds):
        for scope in ("MAIN", "MYSQL"):
            order = request("POST", f"/api/orders?scope={scope}", 201,
                            {"sku": "DEMO-" + secrets.token_hex(4).upper(), "productName": "Observability demo",
                             "unitPrice": 12.50, "quantity": index + 1})
            request("GET", f"/api/orders/{order['id']}?scope={scope}", 200)
            request("POST", f"/api/orders/{order['id']}/confirm?scope={scope}", 200)
            request("POST", f"/api/orders/{order['id']}/confirm?scope={scope}", 409)
            request("GET", f"/api/orders?scope={scope}&pageSize=10", 200)
        request("GET", "/api/orders/9223372036854775807", 404)
        request("POST", "/api/orders", 400, {"quantity": 0})
        if index + 1 < args.rounds:
            time.sleep(args.interval)
    print("Grafana: http://localhost:3000/d/kuma-reference", flush=True)


if __name__ == "__main__":
    main()
