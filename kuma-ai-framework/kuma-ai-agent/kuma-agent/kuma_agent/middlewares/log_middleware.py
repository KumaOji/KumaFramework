"""
Logging middleware for KumaAgent.

Logs the conversation state before and after each agent invocation.
Inspired by kuma_agent's middleware pattern for cross-cutting concerns.
"""

import logging

from kuma_agent.middlewares.base import AgentMiddleware

logger = logging.getLogger(__name__)


class LogMiddleware(AgentMiddleware):
    """Logs agent state transitions for observability.

    Logs at INFO level: thread_id and message count.
    Logs at DEBUG level: content preview of the last message.
    """

    def before_agent(self, state: dict) -> dict | None:
        messages = state.get("messages", [])
        thread_id = state.get("thread_id", "unknown")
        logger.info(
            "[LogMiddleware] BEFORE | thread=%s | messages=%d",
            thread_id,
            len(messages),
        )
        if messages and logger.isEnabledFor(logging.DEBUG):
            last = messages[-1]
            content = getattr(last, "content", "")
            if isinstance(content, str):
                logger.debug("[LogMiddleware] Last message preview: %.100s", content)
        return None

    def after_agent(self, state: dict) -> dict | None:
        messages = state.get("messages", [])
        thread_id = state.get("thread_id", "unknown")
        title = state.get("title")
        logger.info(
            "[LogMiddleware] AFTER  | thread=%s | messages=%d | title=%s",
            thread_id,
            len(messages),
            repr(title) if title else "(none)",
        )
        return None
