"""
Title middleware for KumaAgent.

Simplified version of kuma_agent's TitleMiddleware:
  - Runs after the first complete exchange (human + AI)
  - Extracts a title from the first human message (no extra LLM call)
  - Sets state['title'] exactly once; never overwrites an existing title

In kuma_agent, TitleMiddleware calls the LLM to generate a semantic title.
This simplified version avoids the extra API call while demonstrating
the same middleware lifecycle pattern.
"""

import logging

from langchain_core.messages import AIMessage, HumanMessage

from kuma_agent.middlewares.base import AgentMiddleware

logger = logging.getLogger(__name__)

_MAX_TITLE_LEN = 60


class TitleMiddleware(AgentMiddleware):
    """Auto-generates a thread title after the first exchange.

    Called in after_agent() so we can inspect the completed AI response.
    Returns {"title": <generated>} on the first successful exchange;
    returns None on all subsequent calls (title is already set).
    """

    def after_agent(self, state: dict) -> dict | None:
        # Never overwrite an existing title
        if state.get("title"):
            return None

        messages = state.get("messages", [])
        human_msgs = [m for m in messages if isinstance(m, HumanMessage)]
        ai_msgs = [m for m in messages if isinstance(m, AIMessage)]

        # Need at least one of each to consider the exchange complete
        if not human_msgs or not ai_msgs:
            return None

        first_human = human_msgs[0]
        raw = first_human.content if isinstance(first_human.content, str) else str(first_human.content)

        # Take the first line, truncate gracefully at a word boundary
        title = raw.split("\n")[0].strip()
        if len(title) > _MAX_TITLE_LEN:
            truncated = title[:_MAX_TITLE_LEN].rsplit(" ", 1)[0]
            title = truncated + "..."

        logger.info("[TitleMiddleware] Generated title: '%s'", title)
        return {"title": title}
