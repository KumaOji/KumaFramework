#!/usr/bin/env python3
"""
重新生成 Python gRPC stubs。

每次修改 proto/kuma_agent.proto 后执行：
    uv run python scripts/gen_proto.py
"""

import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).parent.parent
PROTO_DIR = ROOT / "proto"
OUT_DIR = ROOT / "app" / "grpc" / "generated"

OUT_DIR.mkdir(parents=True, exist_ok=True)

result = subprocess.run(
    [
        sys.executable, "-m", "grpc_tools.protoc",
        f"-I{PROTO_DIR}",
        f"--python_out={OUT_DIR}",
        f"--grpc_python_out={OUT_DIR}",
        str(PROTO_DIR / "kuma_agent.proto"),
    ],
    capture_output=True,
    text=True,
)

if result.returncode != 0:
    print("ERROR:", result.stderr)
    sys.exit(1)

# 修正 grpc 存根中的相对导入（protoc 生成的是绝对 import）
grpc_stub = OUT_DIR / "kuma_agent_pb2_grpc.py"
content = grpc_stub.read_text(encoding="utf-8")
content = content.replace(
    "import kuma_agent_pb2 as kuma__agent__pb2",
    "from . import kuma_agent_pb2 as kuma__agent__pb2",
)
grpc_stub.write_text(content, encoding="utf-8")

print(f"Stubs generated in {OUT_DIR}")
print("  kuma_agent_pb2.py")
print("  kuma_agent_pb2_grpc.py")
