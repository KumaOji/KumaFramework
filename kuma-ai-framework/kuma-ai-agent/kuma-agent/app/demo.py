#!/usr/bin/env python3
"""
KumaAgent Demo

Demonstrates the KumaAgent capabilities, referencing deer-flow patterns:
  - State management (AgentState with custom reducers)
  - Middleware chain (LogMiddleware, TitleMiddleware)
  - Tool calling (get_current_time, calculate)
  - Multi-turn conversation (thread_id-based memory via LangGraph checkpointer)
  - Embedded client (KumaAgentClient without HTTP server)

Prerequisites:
    cd kuma-ai-framework/kuma-ai-agent/kuma-agent

    # Install with uv (recommended, same tool as deer-flow)
    uv sync --extra openai       # for OpenAI
    uv sync --extra anthropic    # for Anthropic
    uv sync --extra all          # both

    # Copy and edit config
    cp config.yaml.example config.yaml
    # Then set your API key in config.yaml or via environment variable:
    export OPENAI_API_KEY=sk-...

Run:
    uv run python app/demo.py              # Run all demo scenarios
    uv run python app/demo.py --interactive  # Interactive chat session
    uv run python app/demo.py --config /path/to/config.yaml
"""

import logging
import sys
from pathlib import Path

# Add the packages directory to the Python path when running directly
sys.path.insert(0, str(Path(__file__).parent.parent / "packages"))

from kuma_agent.client import KumaAgentClient

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
    datefmt="%H:%M:%S",
)
# Quieten noisy third-party loggers
logging.getLogger("httpx").setLevel(logging.WARNING)
logging.getLogger("httpcore").setLevel(logging.WARNING)

logger = logging.getLogger(__name__)


# ---------------------------------------------------------------------------
# Demo scenarios
# ---------------------------------------------------------------------------

def demo_basic(client: KumaAgentClient) -> None:
    """Demo 1: Basic conversational response."""
    print("\n" + "─" * 50)
    print("Demo 1 — Basic Conversation")
    print("─" * 50)
    question = "Hello! What can you help me with today?"
    print(f"User : {question}")
    answer = client.chat(question, thread_id="demo-basic")
    print(f"Agent: {answer}")


def demo_tool_time(client: KumaAgentClient) -> None:
    """Demo 2: Tool use — get current time."""
    print("\n" + "─" * 50)
    print("Demo 2 — Tool Use: Current Time")
    print("─" * 50)
    question = "What is the current date and time?"
    print(f"User : {question}")
    answer = client.chat(question, thread_id="demo-time")
    print(f"Agent: {answer}")


def demo_tool_calc(client: KumaAgentClient) -> None:
    """Demo 3: Tool use — calculator."""
    print("\n" + "─" * 50)
    print("Demo 3 — Tool Use: Calculator")
    print("─" * 50)
    question = "What is (42 * 7) + (100 / 4)?"
    print(f"User : {question}")
    answer = client.chat(question, thread_id="demo-calc")
    print(f"Agent: {answer}")


def demo_multi_turn(client: KumaAgentClient) -> None:
    """Demo 4: Multi-turn conversation memory via thread_id."""
    print("\n" + "─" * 50)
    print("Demo 4 — Multi-Turn Conversation (Memory)")
    print("─" * 50)
    thread = "demo-memory"

    pairs = [
        ("My name is Alice and I work as a data scientist.", None),
        ("What do I do for work?", None),
        ("Can you calculate the square of 13 for me?", None),
    ]

    for question, _ in pairs:
        print(f"User : {question}")
        answer = client.chat(question, thread_id=thread)
        print(f"Agent: {answer}\n")

    title = client.get_title(thread)
    if title:
        print(f"[Auto-generated title: '{title}']")


def run_demo(client: KumaAgentClient) -> None:
    """Run all demo scenarios."""
    print("=" * 50)
    print("KumaAgent — Demo")
    print("=" * 50)

    cfg = client.get_config()
    print(f"\nAgent : {cfg['agent_name']}")
    print(f"Model : {cfg['model'] or '(not configured)'}")
    print(f"Tools : {', '.join(cfg['tools']) or '(none)'}")

    demo_basic(client)
    demo_tool_time(client)
    demo_tool_calc(client)
    demo_multi_turn(client)

    print("\n" + "=" * 50)
    print("Demo complete.")
    print("=" * 50)


# ---------------------------------------------------------------------------
# Interactive mode
# ---------------------------------------------------------------------------

def run_interactive(client: KumaAgentClient) -> None:
    """Run an interactive chat session with the agent."""
    print("=" * 50)
    print("KumaAgent — Interactive Chat")
    print("Commands: 'quit' | 'exit' → stop;  'new' → new thread")
    print("=" * 50)

    cfg = client.get_config()
    thread_count = 1
    thread_id = f"chat-{thread_count}"
    print(f"\nAgent : {cfg['agent_name']}  |  Model: {cfg['model'] or '(none)'}  |  Thread: {thread_id}\n")

    while True:
        try:
            user_input = input("You  : ").strip()
        except (EOFError, KeyboardInterrupt):
            print("\nGoodbye!")
            break

        if not user_input:
            continue

        if user_input.lower() in ("quit", "exit"):
            print("Goodbye!")
            break

        if user_input.lower() == "new":
            thread_count += 1
            thread_id = f"chat-{thread_count}"
            print(f"[Started new thread: {thread_id}]\n")
            continue

        # Stream the response, printing incrementally
        print("Agent: ", end="", flush=True)
        last_content = ""
        for event in client.stream(user_input, thread_id=thread_id):
            messages = event.get("messages", [])
            if messages:
                last = messages[-1]
                content = getattr(last, "content", "")
                if isinstance(content, str) and not getattr(last, "tool_calls", None):
                    # Print only the newly arrived characters
                    if content != last_content:
                        print(content[len(last_content):], end="", flush=True)
                        last_content = content
        print()  # Newline after full response


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="KumaAgent Demo")
    parser.add_argument(
        "--interactive", "-i",
        action="store_true",
        help="Run in interactive chat mode instead of scripted demos",
    )
    parser.add_argument(
        "--config", "-c",
        type=str,
        default=None,
        help="Path to config.yaml (default: auto-detect)",
    )
    args = parser.parse_args()

    client = KumaAgentClient(config_path=args.config)

    if args.interactive:
        run_interactive(client)
    else:
        run_demo(client)
