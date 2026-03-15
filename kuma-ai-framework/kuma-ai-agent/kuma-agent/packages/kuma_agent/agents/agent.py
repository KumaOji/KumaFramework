"""
Agent factory for KumaAgent.

Pattern from deer-flow's agents/lead_agent/agent.py — make_lead_agent():
  1. Resolve model config
  2. Create chat model via reflection (models/factory.py)
  3. Assemble tool list (tools/tools.py)
  4. Build system prompt (agents/prompt.py)
  5. Return compiled LangGraph agent with in-memory checkpointer
"""

import logging

from langgraph.checkpoint.memory import MemorySaver
from langgraph.prebuilt import create_react_agent

from kuma_agent.agents.prompt import build_system_prompt
from kuma_agent.agents.state import AgentState
from kuma_agent.config.config import AppConfig
from kuma_agent.models.factory import create_chat_model
from kuma_agent.tools.tools import get_tools

logger = logging.getLogger(__name__)


def make_agent(config: AppConfig):
    """Create a compiled LangGraph ReAct agent from AppConfig.

    Returns a CompiledGraph that supports:
      - .stream(state, config, stream_mode="values")  — streaming execution
      - .invoke(state, config)                        — blocking execution
      - .get_state(config)                            — retrieve persisted state

    The MemorySaver checkpointer enables multi-turn conversations: pass
    the same thread_id in config["configurable"]["thread_id"] across calls
    to maintain conversation history.

    Args:
        config: Loaded AppConfig (from config.yaml via AppConfig.from_file()).

    Returns:
        A compiled LangGraph CompiledGraph instance.

    Raises:
        ValueError: If no models are configured.
        ImportError: If the configured model provider package is not installed.
    """
    model_config = config.get_default_model()
    if model_config is None:
        raise ValueError(
            "No models configured. "
            "Copy config.yaml.example to config.yaml and set your API key."
        )

    logger.info(
        "Creating agent '%s' with model '%s' (%s)",
        config.agent.name,
        model_config.name,
        model_config.use,
    )

    model = create_chat_model(model_config)
    tools = get_tools(config.tools)
    system_prompt = build_system_prompt(config.agent.name, config.agent.description)

    # MemorySaver: in-memory checkpointer enabling multi-turn conversations.
    # For production, swap with SqliteSaver or PostgresSaver from langgraph.
    checkpointer = MemorySaver()

    agent = create_react_agent(
        model=model,
        tools=tools,
        state_schema=AgentState,
        prompt=system_prompt,
        checkpointer=checkpointer,
    )

    logger.info(
        "Agent '%s' ready — %d tool(s): %s",
        config.agent.name,
        len(tools),
        [t.name for t in tools],
    )
    return agent
