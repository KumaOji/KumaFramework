"""
System prompt template for KumaAgent.

Pattern from kuma_agent's agents/lead_agent/prompt.py:
  - Template-based system prompt
  - Injected with agent name, description, and current date
  - Extensible: add memory facts, skills, tool guidance, etc.
"""

import datetime

_SYSTEM_PROMPT_TEMPLATE = """\
You are {agent_name}, a helpful AI assistant.

{description}

## Capabilities
- Answer questions and provide clear, accurate information
- Use tools when they can provide precise or real-time data
- Remember context within a conversation thread

## Guidelines
- Be concise and to the point
- Use tools proactively when they can improve accuracy (e.g., use `calculate` for math)
- Explain your reasoning when it adds clarity
- If you are unsure, say so honestly

Current date: {current_date}
"""


def build_system_prompt(agent_name: str, description: str) -> str:
    """Build the system prompt string for the agent.

    Pattern from kuma_agent's apply_prompt_template(): assembles a system
    prompt from structured sections, injecting runtime context.
    """
    return _SYSTEM_PROMPT_TEMPLATE.format(
        agent_name=agent_name,
        description=description,
        current_date=datetime.datetime.now().strftime("%Y-%m-%d"),
    )
