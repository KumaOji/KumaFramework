"""MCP RPCs: GetMcpConfig, UpdateMcpConfig."""

import json
import logging
from pathlib import Path

import grpc

from app.grpc.generated import kuma_agent_pb2
from kuma_agent.config.extensions_config import ExtensionsConfig, get_extensions_config, reload_extensions_config

logger = logging.getLogger(__name__)


def _ext_config_to_pb(config) -> kuma_agent_pb2.McpConfigResponse:
    servers = {}
    for name, s in config.mcp_servers.items():
        servers[name] = kuma_agent_pb2.McpServerConfig(
            enabled=s.enabled,
            type=s.type or "",
            command=s.command or "",
            args=list(s.args or []),
            env=dict(s.env or {}),
            url=s.url or "",
            headers=dict(s.headers or {}),
            description=s.description or "",
        )
    return kuma_agent_pb2.McpConfigResponse(mcp_servers=servers)


class McpRouter:
    """Mixin: GetMcpConfig, UpdateMcpConfig."""

    def GetMcpConfig(self, request, context):
        try:
            return _ext_config_to_pb(get_extensions_config())
        except Exception as exc:
            logger.exception("GetMcpConfig failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def UpdateMcpConfig(self, request, context):
        try:
            config_path = ExtensionsConfig.resolve_config_path()
            if config_path is None:
                config_path = Path.cwd().parent / "extensions_config.json"

            current = get_extensions_config()
            mcp_servers_data = {}
            for name, s in request.mcp_servers.items():
                mcp_servers_data[name] = {
                    "enabled": s.enabled,
                    "type": s.type,
                    "command": s.command,
                    "args": list(s.args),
                    "env": dict(s.env),
                    "url": s.url,
                    "headers": dict(s.headers),
                    "description": s.description,
                }
            config_data = {
                "mcpServers": mcp_servers_data,
                "skills": {n: {"enabled": sk.enabled} for n, sk in current.skills.items()},
            }
            with open(config_path, "w", encoding="utf-8") as f:
                json.dump(config_data, f, indent=2)

            return _ext_config_to_pb(reload_extensions_config())
        except Exception as exc:
            logger.exception("UpdateMcpConfig failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))
