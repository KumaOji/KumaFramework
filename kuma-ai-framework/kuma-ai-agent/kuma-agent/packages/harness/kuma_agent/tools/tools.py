"""
Built-in tools for KumaAgent.

Pattern from deer-flow's tools/builtins/:
  - Each tool is a @tool decorated function
  - get_tools() assembles the list based on ToolsConfig
  - Tools are stateless and have clear docstrings (used as tool descriptions by LLM)
"""

import ast
import datetime
import logging
import operator

from langchain_core.tools import tool

from kuma_agent.config.config import ToolsConfig

logger = logging.getLogger(__name__)

# ---------------------------------------------------------------------------
# Tool definitions
# ---------------------------------------------------------------------------

@tool
def get_current_time() -> str:
    """Get the current date and time."""
    now = datetime.datetime.now()
    return now.strftime("Current time: %Y-%m-%d %H:%M:%S (%A)")


@tool
def calculate(expression: str) -> str:
    """Safely evaluate a mathematical expression.

    Supports +, -, *, /, ** (power), and unary +/-.
    Does NOT support function calls or arbitrary Python code.

    Args:
        expression: A math expression string, e.g. '(42 * 7) + (100 / 4)'
    """
    _ALLOWED_OPS: dict = {
        ast.Add: operator.add,
        ast.Sub: operator.sub,
        ast.Mult: operator.mul,
        ast.Div: operator.truediv,
        ast.Pow: operator.pow,
        ast.USub: operator.neg,
        ast.UAdd: operator.pos,
    }

    def _eval(node: ast.AST) -> float:
        if isinstance(node, ast.Constant):
            return node.value
        if isinstance(node, ast.BinOp):
            op_type = type(node.op)
            if op_type not in _ALLOWED_OPS:
                raise ValueError(f"Unsupported operator: {op_type.__name__}")
            return _ALLOWED_OPS[op_type](_eval(node.left), _eval(node.right))
        if isinstance(node, ast.UnaryOp):
            op_type = type(node.op)
            if op_type not in _ALLOWED_OPS:
                raise ValueError(f"Unsupported operator: {op_type.__name__}")
            return _ALLOWED_OPS[op_type](_eval(node.operand))
        raise ValueError(f"Unsupported expression node: {type(node).__name__}")

    try:
        tree = ast.parse(expression.strip(), mode="eval")
        result = _eval(tree.body)
        # Format cleanly: int if no remainder, else float
        formatted = int(result) if isinstance(result, float) and result.is_integer() else result
        return f"{expression} = {formatted}"
    except Exception as e:
        return f"Error evaluating '{expression}': {e}"


@tool
def search_web(query: str) -> str:
    """Search the web for up-to-date information.

    Args:
        query: The search query string.
    """
    # Mock implementation.
    # Replace with langchain_community.tools.TavilySearchResults or similar.
    logger.info("Mock web search: %s", query)
    return (
        f"[Mock search results for: '{query}']\n"
        "This is a placeholder. To enable real web search:\n"
        "  1. pip install langchain-community tavily-python\n"
        "  2. Set TAVILY_API_KEY environment variable\n"
        "  3. Replace this tool with TavilySearchResults in tools.py"
    )


# ---------------------------------------------------------------------------
# Tool assembly
# ---------------------------------------------------------------------------

def get_tools(config: ToolsConfig) -> list:
    """Assemble tool list based on configuration.

    Pattern from deer-flow's get_available_tools(): builds the tool list
    dynamically based on config flags instead of hardcoding.
    """
    tools = []
    if config.current_time:
        tools.append(get_current_time)
    if config.calculator:
        tools.append(calculate)
    if config.web_search:
        tools.append(search_web)

    logger.debug("Assembled %d tool(s): %s", len(tools), [t.name for t in tools])
    return tools
