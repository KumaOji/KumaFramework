from kuma_agent.tools.tools import get_tools, get_current_time, calculate, search_web


def get_available_tools(
    model_name: str | None = None,
    groups: list[str] | None = None,
    subagent_enabled: bool = False,
) -> list:
    """Assemble tools available to the lead agent at runtime.

    Args:
        model_name: Resolved model name (reserved for future per-model filtering).
        groups: Optional tool-group whitelist from the agent's config.tool_groups.
                None means all groups are enabled.
        subagent_enabled: Whether to include the task_tool for spawning subagents.

    Returns:
        List of LangChain tool objects.
    """
    from kuma_agent.config.app_config import get_app_config
    from kuma_agent.tools.builtins import task_tool

    app_config = get_app_config()
    tools = get_tools(app_config.tools)

    if subagent_enabled:
        tools = [*tools, task_tool]

    return tools


__all__ = ["get_tools", "get_available_tools", "get_current_time", "calculate", "search_web"]
