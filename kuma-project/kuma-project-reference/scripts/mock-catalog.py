"""Local development only: deterministic HTTP product catalog, no third-party packages."""
import argparse
import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import urlsplit

PRODUCTS = {
    "BOOK-001": {"sku": "BOOK-001", "name": "DDD Reference Book", "unitPrice": "99.00"},
    "PEN-001": {"sku": "PEN-001", "name": "Reference Pen", "unitPrice": "9.90"},
}


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        path = urlsplit(self.path).path
        product = PRODUCTS.get(path.removeprefix("/products/")) if path.startswith("/products/") else None
        body = json.dumps(product or {"message": "Product not found"}).encode("utf-8")
        self.send_response(200 if product else 404)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--port", type=int, default=19093)
    args = parser.parse_args()
    with ThreadingHTTPServer(("127.0.0.1", args.port), Handler) as server:
        print(f"Mock catalog: http://127.0.0.1:{args.port}/products/BOOK-001", flush=True)
        try:
            server.serve_forever()
        except KeyboardInterrupt:
            pass
