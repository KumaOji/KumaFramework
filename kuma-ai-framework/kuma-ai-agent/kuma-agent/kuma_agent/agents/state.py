"""
Agent state schema for KumaAgent.

Pattern from kuma_agent's agents/thread_state.py:
  - Extends MessagesState (provides 'messages' with add_messages reducer)
  - Custom reducers for immutable field merging
  - Each field is independently updateable via state update dicts
"""

from typing import Annotated

from langgraph.graph import MessagesState
from langgraph.managed import RemainingSteps


def _merge_unique(existing: list[str] | None, new: list[str] | None) -> list[str]:
    """Merge two string lists, deduplicating while preserving insertion order.

    Follows kuma_agent's merge_artifacts reducer pattern:
      - Never mutates the existing list
      - Deduplicates using dict.fromkeys (preserves order, O(n))
      - Handles None inputs gracefully
    """
    if existing is None:
        return new or []
    if new is None:
        return existing
    return list(dict.fromkeys(existing + new))


class AgentState(MessagesState):
    """State schema for KumaAgent.

    Inherits 'messages: list[BaseMessage]' with add_messages reducer from MessagesState.
    Custom fields use Annotated reducers for immutable updates.

    Fields:
        messages:         Conversation history (inherited, append-only via add_messages)
        remaining_steps:  Required by create_react_agent to prevent infinite loops
        thread_id:        Identifies the conversation thread (for multi-turn memory)
        title:            Auto-generated title for this conversation thread
        memory_facts:     Accumulated key facts from conversation (deduplicated list)
    """
    remaining_steps: RemainingSteps
    thread_id: str | None
    title: str | None
    memory_facts: Annotated[list[str], _merge_unique]
