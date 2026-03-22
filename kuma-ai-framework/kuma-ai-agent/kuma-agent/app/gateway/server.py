#!/usr/bin/env python3
"""
KumaAgent API Gateway — HTTP entry point.

Starts the FastAPI gateway defined in app.gateway.app.
Routers: models, mcp, memory, skills, artifacts, uploads, agents, suggestions, channels.

Run:
    uv run python app/gateway/server.py
    uv run python app/gateway/server.py --port 8001 --host 0.0.0.0
    uv run python app/gateway/server.py --reload
"""

import sys
from pathlib import Path

# Project root → resolves kuma_agent package
_ROOT = Path(__file__).parent.parent.parent
sys.path.insert(0, str(_ROOT))

import uvicorn

from app.gateway.app import app
from app.gateway.config import get_gateway_config

if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="KumaAgent API Gateway")
    parser.add_argument("--host", default=None, help="Bind host (overrides GATEWAY_HOST env, default: 0.0.0.0)")
    parser.add_argument("--port", type=int, default=None, help="Bind port (overrides GATEWAY_PORT env, default: 8001)")
    parser.add_argument("--reload", action="store_true", help="Enable auto-reload (dev mode)")
    parser.add_argument(
        "--log-level",
        default="info",
        choices=["debug", "info", "warning", "error"],
        help="Uvicorn log level",
    )
    args = parser.parse_args()

    config = get_gateway_config()
    host = args.host or config.host
    port = args.port or config.port

    print(f"\n{'=' * 55}")
    print(f"  KumaAgent Gateway  —  http://{host}:{port}")
    print(f"  API Docs           —  http://{host}:{port}/docs")
    print(f"  Press Ctrl+C to stop")
    print(f"{'=' * 55}\n")

    uvicorn.run(
        app,
        host=host,
        port=port,
        log_level=args.log_level,
        reload=args.reload,
    )
