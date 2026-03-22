"""
Base middleware class for KumaAgent.

Pattern from deer-flow's middleware system:
  - before_agent() runs before each agent invocation, can inject state updates
  - after_agent() runs after each agent invocation, can inject state updates
  - Middlewares are ordered and applied sequentially by KumaAgentClient

Unlike deer-flow (which integrates middlewares into the LangGraph graph itself
via langchain.agents.create_agent), this simplified implementation wraps
the agent invocation in the client, keeping it framework-independent.
"""

from abc import ABC


class AgentMiddleware(ABC):
    """Base class for agent middlewares.

    Subclasses override before_agent() and/or after_agent() to:
      - Inspect or log the conversation state
      - Inject state field updates (title, memory_facts, etc.)
      - Implement cross-cutting concerns (rate limiting, caching, etc.)

    Return a dict of state updates to apply, or None to skip.
    """

    def before_agent(self, state: dict) -> dict | None:
        """Called before the agent processes the current message.

        Args:
            state: Current agent state dict (contains 'messages', 'thread_id', etc.)

        Returns:
            A dict of state updates to merge, or None.
        """
        return None

    def after_agent(self, state: dict) -> dict | None:
        """Called after the agent finishes processing.

        Args:
            state: Final agent state dict after execution.

        Returns:
            A dict of state updates to apply, or None.
        """
        return None
