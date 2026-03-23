"""Models RPCs: ListModels, GetModel."""

import logging

import grpc

from app.grpc.generated import kuma_agent_pb2
from kuma_agent.config import get_app_config

logger = logging.getLogger(__name__)


def _model_to_pb(m) -> kuma_agent_pb2.ModelInfo:
    return kuma_agent_pb2.ModelInfo(
        name=m.name,
        model=m.model,
        display_name=m.display_name or "",
        description=m.description or "",
        supports_thinking=m.supports_thinking,
        supports_reasoning_effort=m.supports_reasoning_effort,
    )


class ModelsRouter:
    """Mixin: ListModels, GetModel."""

    def ListModels(self, request, context):
        try:
            config = get_app_config()
            return kuma_agent_pb2.ListModelsResponse(
                models=[_model_to_pb(m) for m in config.models]
            )
        except Exception as exc:
            logger.exception("ListModels failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def GetModel(self, request, context):
        try:
            config = get_app_config()
            m = config.get_model_config(request.name)
            if m is None:
                context.abort(grpc.StatusCode.NOT_FOUND, f"Model '{request.name}' not found")
                return
            return _model_to_pb(m)
        except Exception as exc:
            logger.exception("GetModel failed for '%s'", request.name)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))
