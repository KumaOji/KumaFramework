"""System RPCs: HealthCheck."""

import logging

from app.grpc.generated import kuma_agent_pb2

logger = logging.getLogger(__name__)


class SystemRouter:
    """Mixin: HealthCheck."""

    def HealthCheck(self, request, context):
        return kuma_agent_pb2.HealthCheckResponse(status="ok")
