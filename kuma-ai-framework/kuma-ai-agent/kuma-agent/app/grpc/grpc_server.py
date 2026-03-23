#!/usr/bin/env python3
"""
KumaAgent gRPC Server — entry point.

gRPC is treated as another communication channel alongside Slack / Telegram / Feishu.
Java / Go clients connect here; the server delegates to KumaAgentClient.

Run:
    uv run python app/grpc/grpc_server.py
    uv run python app/grpc/grpc_server.py --port 50051
    uv run python app/grpc/grpc_server.py --config /path/to/config.yaml

Java 客户端接入：
    1. 将 proto/kuma_agent.proto 复制到 Java 项目
    2. 添加 grpc-java 依赖（见 docs/grpc_java.md）
    3. 用 protoc 生成 Java 存根
    4. 通过 ManagedChannel 连接本服务
"""

import argparse
import logging
import signal
import sys
import time
from concurrent import futures
from pathlib import Path

import grpc

# Project root → resolves kuma_agent package
_ROOT = Path(__file__).parent.parent.parent
sys.path.insert(0, str(_ROOT))

from kuma_agent.client import KumaAgentClient
from app.grpc.generated import kuma_agent_pb2_grpc
from app.grpc.servicer import KumaAgentServicer

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
    datefmt="%H:%M:%S",
)
logging.getLogger("httpx").setLevel(logging.WARNING)
logging.getLogger("httpcore").setLevel(logging.WARNING)

logger = logging.getLogger(__name__)


def serve(host: str, port: int, config_path: str | None, workers: int) -> None:
    """启动 gRPC 服务器，阻塞直到收到终止信号。"""
    logger.info("Initialising KumaAgentClient …")
    client = KumaAgentClient(config_path=config_path)
    cfg = client.get_config()
    logger.info(
        "Agent ready  — name=%s  model=%s  tools=%s",
        cfg["agent_name"], cfg["model"], cfg["tools"],
    )

    server = grpc.server(
        futures.ThreadPoolExecutor(max_workers=workers),
        options=[
            ("grpc.max_send_message_length",    50 * 1024 * 1024),
            ("grpc.max_receive_message_length", 50 * 1024 * 1024),
            ("grpc.keepalive_time_ms",          30_000),
            ("grpc.keepalive_timeout_ms",       10_000),
            ("grpc.keepalive_permit_without_calls", True),
        ],
    )

    servicer = KumaAgentServicer(client)
    kuma_agent_pb2_grpc.add_KumaAgentServiceServicer_to_server(servicer, server)

    listen_addr = f"{host}:{port}"
    server.add_insecure_port(listen_addr)
    server.start()

    addr_display = f"http://{host}:{port}" if host != "0.0.0.0" else f"0.0.0.0:{port}"
    print(f"\n{'=' * 55}")
    print(f"  KumaAgent gRPC Server  —  {addr_display}")
    print(f"  Workers: {workers}  |  Press Ctrl+C to stop")
    print(f"{'=' * 55}\n")

    def _shutdown(signum, frame):
        logger.info("Shutdown signal received, stopping server …")
        server.stop(grace=5).wait()
        logger.info("Server stopped.")
        sys.exit(0)

    signal.signal(signal.SIGINT,  _shutdown)
    signal.signal(signal.SIGTERM, _shutdown)

    try:
        while True:
            time.sleep(3600)
    except SystemExit:
        pass


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="KumaAgent gRPC Server")
    parser.add_argument("--host",    default="127.0.0.1",  help="Bind host (default: 127.0.0.1)")
    parser.add_argument("--port",    type=int, default=50051, help="Bind port (default: 50051)")
    parser.add_argument("--config",  "-c", default=None,   help="Path to config.yaml")
    parser.add_argument("--workers", type=int, default=10,  help="Thread-pool size (default: 10)")
    args = parser.parse_args()

    serve(
        host=args.host,
        port=args.port,
        config_path=args.config,
        workers=args.workers,
    )
