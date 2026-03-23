"""
Model factory for KumaAgent.

  - Reflection-based instantiation via 'module:ClassName' strings
  - Provider-agnostic: supports OpenAI, Anthropic, etc.
  - Supports both legacy (ModelConfig dataclass) and runtime (name + flags) call styles
  - Handles both old dataclass ModelConfig and new Pydantic ModelConfig
"""

import logging
from importlib import import_module

logger = logging.getLogger(__name__)


def _get_extra(model_config) -> dict:
    """Extract extra kwargs from either dataclass (.extra) or Pydantic (.model_extra)."""
    extra = getattr(model_config, "model_extra", None)
    if extra is None:
        extra = getattr(model_config, "extra", None)
    return dict(extra) if extra else {}


def _get_field(model_config, field: str, default=None):
    """Get a field that may be a declared attribute or in extra."""
    val = getattr(model_config, field, None)
    if val is not None:
        return val
    return _get_extra(model_config).get(field, default)


def create_chat_model(
    model_config=None,
    *,
    name: str | None = None,
    thinking_enabled: bool = False,
    reasoning_effort: str | None = None,
):
    """Create a LangChain chat model from config using reflection.

    Supports two call styles:

    Legacy (used by agents/agent.py):
        create_chat_model(model_config)

    Runtime (used by lead_agent/agent.py):
        create_chat_model(name="qwen-turbo", thinking_enabled=True)
        create_chat_model(name="claude-3-opus", reasoning_effort="high")
        create_chat_model(thinking_enabled=False)   # uses default model

    Args:
        model_config: ModelConfig instance (legacy style, dataclass or Pydantic).
        name: Model name to look up from get_app_config() (runtime style).
        thinking_enabled: Enable extended thinking where supported.
        reasoning_effort: Reasoning effort for models that support it ("low"/"medium"/"high").

    Returns:
        A LangChain BaseChatModel instance.
    """
    # ── Resolve ModelConfig ────────────────────────────────────────────────
    if model_config is None:
        from kuma_agent.config.app_config import get_app_config
        app_config = get_app_config()
        if name:
            model_config = app_config.get_model_config(name)
            if model_config is None:
                raise ValueError(f"Model '{name}' not found in config.yaml")
        else:
            if not app_config.models:
                raise ValueError("No models configured in config.yaml")
            model_config = app_config.models[0]

    # ── Validate 'use' field ───────────────────────────────────────────────
    use = model_config.use
    if ":" not in use:
        raise ValueError(
            f"Invalid 'use' format: '{use}'. "
            "Expected 'module.path:ClassName', e.g. 'langchain_openai:ChatOpenAI'."
        )

    module_path, class_name = use.split(":", 1)

    try:
        module = import_module(module_path)
    except ImportError as e:
        pkg_name = module_path.replace("_", "-")
        raise ImportError(
            f"Could not import '{module_path}'. "
            f"Install it with: pip install {pkg_name}"
        ) from e

    cls = getattr(module, class_name, None)
    if cls is None:
        raise ValueError(f"Class '{class_name}' not found in module '{module_path}'.")

    # ── Build kwargs ───────────────────────────────────────────────────────
    # Start with explicit known fields, then overlay extras
    extra = _get_extra(model_config)
    temperature = _get_field(model_config, "temperature", 0.7)

    # Remove fields we handle explicitly to avoid duplicates
    extra.pop("api_key", None)
    extra.pop("base_url", None)
    extra.pop("temperature", None)

    kwargs: dict = {
        "model": model_config.model,
        "temperature": temperature,
        **extra,
    }

    # ── API key ────────────────────────────────────────────────────────────
    api_key = _get_field(model_config, "api_key")
    if api_key:
        if "openai" in module_path.lower():
            kwargs["openai_api_key"] = api_key
        elif "anthropic" in module_path.lower():
            kwargs["anthropic_api_key"] = api_key
        else:
            kwargs["api_key"] = api_key

    # ── base_url ───────────────────────────────────────────────────────────
    base_url = _get_field(model_config, "base_url")
    if base_url:
        kwargs["base_url"] = base_url

    # ── Thinking / reasoning flags ─────────────────────────────────────────
    supports_thinking = getattr(model_config, "supports_thinking", False)
    supports_reasoning = getattr(model_config, "supports_reasoning_effort", False)

    if thinking_enabled and supports_thinking:
        # Use model-specific thinking config if declared, else sensible default
        when_thinking = getattr(model_config, "when_thinking_enabled", None) or {}
        thinking_cfg = getattr(model_config, "thinking", None) or {}
        merged = {**when_thinking, **thinking_cfg}
        if merged:
            kwargs.update(merged)
        elif "anthropic" in module_path.lower():
            kwargs["thinking"] = {"type": "enabled", "budget_tokens": 8000}

    if reasoning_effort and supports_reasoning:
        kwargs["reasoning_effort"] = reasoning_effort

    logger.info("Creating chat model: %s via %s", model_config.name, use)
    return cls(**kwargs)
