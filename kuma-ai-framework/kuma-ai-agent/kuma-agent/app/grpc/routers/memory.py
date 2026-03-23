"""Memory RPCs: GetMemory, ReloadMemory."""

import logging

import grpc

from app.grpc.generated import kuma_agent_pb2
from kuma_agent.agents.memory.updater import get_memory_data, reload_memory_data

logger = logging.getLogger(__name__)


def _section(d: dict, key: str) -> kuma_agent_pb2.ContextSection:
    s = d.get(key, {})
    return kuma_agent_pb2.ContextSection(
        summary=s.get("summary", ""),
        updated_at=s.get("updatedAt", ""),
    )


def _memory_dict_to_pb(data: dict) -> kuma_agent_pb2.MemoryResponse:
    user = data.get("user", {})
    history = data.get("history", {})
    facts = [
        kuma_agent_pb2.MemoryFact(
            id=f.get("id", ""),
            content=f.get("content", ""),
            category=f.get("category", "context"),
            confidence=f.get("confidence", 0.5),
            created_at=f.get("createdAt", ""),
            source=f.get("source", "unknown"),
        )
        for f in data.get("facts", [])
    ]
    return kuma_agent_pb2.MemoryResponse(
        version=data.get("version", "1.0"),
        last_updated=data.get("lastUpdated", ""),
        user=kuma_agent_pb2.UserMemoryContext(
            work_context=_section(user, "workContext"),
            personal_context=_section(user, "personalContext"),
            top_of_mind=_section(user, "topOfMind"),
        ),
        history=kuma_agent_pb2.HistoryMemoryContext(
            recent_months=_section(history, "recentMonths"),
            earlier_context=_section(history, "earlierContext"),
            long_term_background=_section(history, "longTermBackground"),
        ),
        facts=facts,
    )


class MemoryRouter:
    """Mixin: GetMemory, ReloadMemory."""

    def GetMemory(self, request, context):
        try:
            return _memory_dict_to_pb(get_memory_data())
        except Exception as exc:
            logger.exception("GetMemory failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def ReloadMemory(self, request, context):
        try:
            return _memory_dict_to_pb(reload_memory_data())
        except Exception as exc:
            logger.exception("ReloadMemory failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))
