"""System RPCs: HealthCheck, GetConfig."""

import logging

from app.grpc.generated import kuma_agent_pb2

logger = logging.getLogger(__name__)


class SystemRouter:
    """Mixin: HealthCheck, GetConfig."""

    def HealthCheck(self, request, context):
        return kuma_agent_pb2.HealthCheckResponse(status="ok")

    def GetConfig(self, request, context):
        cfg = self._client.get_config()
        return kuma_agent_pb2.GetConfigResponse(
            agent_name=cfg["agent_name"],
            description=cfg["description"],
            model=cfg["model"] or "",
            tools=cfg["tools"],
        )
