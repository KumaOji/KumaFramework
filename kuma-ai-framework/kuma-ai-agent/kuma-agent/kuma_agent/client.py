"""
KumaAgentClient — Embedded Python client for KumaAgent.

Inspired by kuma_agent.client:
  - Direct in-process agent access, no HTTP server required
  - Lazy agent initialization (created on first use)
  - Middleware chain wrapping each invocation
  - Thread-isolated multi-turn memory via LangGraph checkpointer
  - All dict return types align with a potential REST API's response schema

Usage:
    client = KumaAgentClient()
    response = client.chat("What time is it?")

    # Multi-turn conversation (same thread_id preserves history)
    client.chat("My name is Alice.", thread_id="t1")
    client.chat("What is my name?", thread_id="t1")  # → "Your name is Alice."
"""

import logging
from pathlib import Path
from typing import Any, Generator

from langchain_core.messages import HumanMessage

from kuma_agent.agents.agent import make_agent
from kuma_agent.config.config import AppConfig
from kuma_agent.middlewares.base import AgentMiddleware
from kuma_agent.middlewares.log_middleware import LogMiddleware
from kuma_agent.middlewares.title_middleware import TitleMiddleware

logger = logging.getLogger(__name__)


class KumaAgentClient:
    """Embedded client for interacting with KumaAgent.

    Mirrors the high-level interface of kuma_agent's KumaAgentClient:
      - chat()         — blocking single response
      - stream()       — streaming state events
      - get_title()    — retrieve auto-generated thread title
      - get_config()   — current agent configuration summary
      - list_tools()   — available tool names

    The agent is created lazily on first use and cached until reset.
    """

    def __init__(self, config_path: str | Path | None = None):
        self._config = AppConfig.from_file(config_path)
        self._agent = None
        self._middlewares: list[AgentMiddleware] = self._build_middlewares()

    # ------------------------------------------------------------------
    # Internal helpers
    # ------------------------------------------------------------------

    def _build_middlewares(self) -> list[AgentMiddleware]:
        """Build the ordered middleware chain from config.

        Pattern from kuma_agent's _build_middlewares(): each middleware is
        instantiated based on config flags and added in a defined order.
        Ordering matters — later middlewares run after earlier ones.
        """
        middlewares: list[AgentMiddleware] = []
        if self._config.middlewares.log:
            middlewares.append(LogMiddleware())
        if self._config.middlewares.title:
            middlewares.append(TitleMiddleware())
        return middlewares

    def _ensure_agent(self):
        """Lazy agent initialization — create once and cache."""
        if self._agent is None:
            self._agent = make_agent(self._config)
        return self._agent

    def _run_config(self, thread_id: str) -> dict:
        """Build LangGraph run config with thread_id for checkpointer."""
        return {"configurable": {"thread_id": thread_id}}

    # ------------------------------------------------------------------
    # Public API
    # ------------------------------------------------------------------

    def stream(
        self,
        message: str,
        thread_id: str = "default",
        **kwargs: Any,
    ) -> Generator[dict, None, None]:
        """Stream agent execution, yielding state snapshots.

        Each yielded dict is a full AgentState snapshot containing:
          - "messages": list of BaseMessage (grows with each step)
          - "thread_id": the conversation thread identifier
          - "title": auto-generated title (set after first exchange)
          - "memory_facts": accumulated facts (if populated)

        Args:
            message:   User input text.
            thread_id: Conversation thread (same ID = continued context).
            **kwargs:  Extra initial state fields (e.g. memory_facts=[...]).

        Yields:
            State snapshot dicts as the agent processes the message.
        """
        agent = self._ensure_agent()

        initial_state: dict = {
            "messages": [HumanMessage(content=message)],
            "thread_id": thread_id,
            **kwargs,
        }

        # --- Before-agent middleware pass ---
        for mw in self._middlewares:
            updates = mw.before_agent(initial_state)
            if updates:
                initial_state.update(updates)

        # --- Agent execution ---
        final_state: dict | None = None
        for event in agent.stream(initial_state, self._run_config(thread_id), stream_mode="values"):
            final_state = event
            yield event

        # --- After-agent middleware pass ---
        if final_state is not None:
            for mw in self._middlewares:
                updates = mw.after_agent(final_state)
                if updates:
                    final_state = {**final_state, **updates}
                    yield final_state  # Yield the updated state snapshot

    def chat(self, message: str, thread_id: str = "default") -> str:
        """Send a message and return the final AI response text.

        Convenience wrapper around stream() that extracts the last
        non-tool-call AI message content.

        Args:
            message:   User input text.
            thread_id: Conversation thread ID.

        Returns:
            The final AI response as a plain string.
        """
        result = ""
        for event in self.stream(message, thread_id):
            messages = event.get("messages", [])
            if messages:
                last = messages[-1]
                content = getattr(last, "content", "")
                # Capture only AI text responses, not tool call messages
                if isinstance(content, str) and not getattr(last, "tool_calls", None):
                    result = content
        return result

    def get_title(self, thread_id: str = "default") -> str | None:
        """Get the auto-generated title for a conversation thread.

        Args:
            thread_id: The thread to query.

        Returns:
            The title string, or None if not yet generated.
        """
        agent = self._ensure_agent()
        try:
            state = agent.get_state(self._run_config(thread_id))
            return state.values.get("title") if state else None
        except Exception as exc:
            logger.debug("Could not retrieve title for thread '%s': %s", thread_id, exc)
            return None

    def list_tools(self) -> list[str]:
        """List the names of all configured tools."""
        from kuma_agent.tools.tools import get_tools
        return [t.name for t in get_tools(self._config.tools)]

    def get_config(self) -> dict:
        """Return a summary of the current agent configuration.

        Returns a dict that mirrors what a REST /api/config endpoint would return.
        """
        model = self._config.get_default_model()
        return {
            "agent_name": self._config.agent.name,
            "description": self._config.agent.description,
            "model": model.name if model else None,
            "tools": self.list_tools(),
            "middlewares": {
                "log": self._config.middlewares.log,
                "title": self._config.middlewares.title,
            },
        }

    def reset_agent(self) -> None:
        """Force agent recreation on next invocation.

        Useful after updating config or when you want a fresh model instance.
        Note: existing conversation history persisted in MemorySaver is preserved.
        """
        self._agent = None
        logger.info("Agent reset — will be recreated on next invocation.")
