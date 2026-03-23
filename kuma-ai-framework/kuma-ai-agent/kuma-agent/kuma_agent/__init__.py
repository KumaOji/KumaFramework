"""
KumaAgent — A simple LangGraph-based AI agent demo.

Inspired by kuma_agent/backend/packages/harness patterns:
  - Immutable state management with custom reducers
  - Ordered middleware chain for extensible behavior
  - Reflection-based model loading
  - Embedded client matching Gateway API style
"""

from kuma_agent.client import KumaAgentClient

__all__ = ["KumaAgentClient"]
