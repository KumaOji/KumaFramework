"""Models RPCs: ListModels."""

import logging

import grpc

from app.grpc.generated import kuma_agent_pb2
from kuma_agent.config import get_app_config

logger = logging.getLogger(__name__)


class ModelsRouter:
    """Mixin: ListModels."""

    def ListModels(self, request, context):
        try:
            config = get_app_config()
            models = [
                kuma_agent_pb2.ModelInfo(
                    name=m.name,
                    model=m.model,
                    display_name=m.display_name or "",
                    description=m.description or "",
                    supports_thinking=m.supports_thinking,
                    supports_reasoning_effort=m.supports_reasoning_effort,
                )
                for m in config.models
            ]
            return kuma_agent_pb2.ListModelsResponse(models=models)
        except Exception as exc:
            logger.exception("ListModels failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))
