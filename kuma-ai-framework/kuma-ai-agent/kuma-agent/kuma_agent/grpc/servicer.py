"""
KumaAgentServicer — gRPC service implementation.

Wraps KumaAgentClient with the protobuf-generated interface so that
any gRPC client (Java, Go, Python, etc.) can call the agent over the
network with full multi-turn conversation support.
"""

import logging
import uuid

import grpc

from kuma_agent.client import KumaAgentClient
from kuma_agent.config import get_app_config
from kuma_agent.grpc.generated import kuma_agent_pb2, kuma_agent_pb2_grpc

logger = logging.getLogger(__name__)


class KumaAgentServicer(kuma_agent_pb2_grpc.KumaAgentServiceServicer):
    """Implements every RPC defined in kuma_agent.proto."""

    def __init__(self, client: KumaAgentClient):
        self._client = client
        # Thread registry: thread_id -> {title, message_count}
        self._threads: dict[str, dict] = {}

    # ------------------------------------------------------------------
    # Internal helpers
    # ------------------------------------------------------------------

    def _resolve_thread(self, thread_id: str) -> str:
        """Return existing thread_id or create a new UUID."""
        if not thread_id:
            thread_id = str(uuid.uuid4())
        if thread_id not in self._threads:
            self._threads[thread_id] = {"title": None, "message_count": 0}
        return thread_id

    def _refresh_meta(self, thread_id: str) -> None:
        """Sync title and message count from LangGraph checkpointer."""
        title = self._client.get_title(thread_id)
        if title:
            self._threads[thread_id]["title"] = title
        try:
            agent = self._client._ensure_agent()
            state = agent.get_state({"configurable": {"thread_id": thread_id}})
            if state and state.values:
                self._threads[thread_id]["message_count"] = len(
                    state.values.get("messages", [])
                )
        except Exception:
            pass

    # ------------------------------------------------------------------
    # RPC implementations
    # ------------------------------------------------------------------

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

    def Chat(self, request, context):
        """Blocking unary RPC — waits for the full agent response."""
        if not request.message:
            context.abort(grpc.StatusCode.INVALID_ARGUMENT, "message must not be empty")

        thread_id = self._resolve_thread(request.thread_id)
        logger.info("Chat  thread=%s  msg=%.60s", thread_id, request.message)

        try:
            response = self._client.chat(request.message, thread_id=thread_id)
        except Exception as exc:
            logger.exception("Chat failed for thread %s", thread_id)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

        self._refresh_meta(thread_id)
        return kuma_agent_pb2.ChatResponse(
            thread_id=thread_id,
            response=response,
            title=self._threads[thread_id].get("title") or "",
        )

    def ChatStream(self, request, context):
        """Server-streaming RPC — yields StreamToken messages incrementally."""
        if not request.message:
            context.abort(grpc.StatusCode.INVALID_ARGUMENT, "message must not be empty")

        thread_id = self._resolve_thread(request.thread_id)
        logger.info("ChatStream  thread=%s  msg=%.60s", thread_id, request.message)

        last_content = ""
        full_response = ""
        try:
            for event in self._client.stream(request.message, thread_id=thread_id):
                messages = event.get("messages", [])
                if not messages:
                    continue
                last = messages[-1]
                content = getattr(last, "content", "")
                if isinstance(content, str) and not getattr(last, "tool_calls", None):
                    delta = content[len(last_content):]
                    if delta:
                        full_response = content
                        yield kuma_agent_pb2.StreamToken(
                            thread_id=thread_id,
                            delta=delta,
                            done=False,
                        )
                        last_content = content
        except Exception as exc:
            logger.exception("ChatStream failed for thread %s", thread_id)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))
            return

        # Final "done" token with complete response
        self._refresh_meta(thread_id)
        yield kuma_agent_pb2.StreamToken(
            thread_id=thread_id,
            delta="",
            done=True,
            response=full_response,
            title=self._threads[thread_id].get("title") or "",
        )

    def ListThreads(self, request, context):
        threads = [
            kuma_agent_pb2.ThreadInfo(
                thread_id=tid,
                title=meta.get("title") or "",
                message_count=meta.get("message_count", 0),
            )
            for tid, meta in self._threads.items()
        ]
        return kuma_agent_pb2.ListThreadsResponse(threads=threads)

    def DeleteThread(self, request, context):
        tid = request.thread_id
        if tid not in self._threads:
            context.abort(grpc.StatusCode.NOT_FOUND, f"Thread '{tid}' not found")
        del self._threads[tid]
        logger.info("Deleted thread %s", tid)
        return kuma_agent_pb2.DeleteThreadResponse(deleted=tid)

    def ListModels(self, request, context):
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
