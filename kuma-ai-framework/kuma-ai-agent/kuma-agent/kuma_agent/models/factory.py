"""
Model factory for KumaAgent.

Pattern from kuma_agent's models/factory.py:
  - Reflection-based instantiation via 'module:ClassName' strings
  - Provider-agnostic: supports OpenAI, Anthropic, etc.
  - Actionable error messages when providers are not installed
"""

import logging
from importlib import import_module

from kuma_agent.config.config import ModelConfig

logger = logging.getLogger(__name__)


def create_chat_model(model_config: ModelConfig):
    """Create a LangChain chat model from config using reflection.

    The 'use' field in ModelConfig follows the 'module:ClassName' convention
    from kuma_agent, enabling pluggable model providers without hardcoded imports.

    Args:
        model_config: ModelConfig with provider class path and parameters.

    Returns:
        A LangChain BaseChatModel instance.

    Raises:
        ImportError: If the provider package is not installed.
        ValueError: If the 'use' path is malformed.
    """
    if ":" not in model_config.use:
        raise ValueError(
            f"Invalid 'use' format: '{model_config.use}'. "
            "Expected 'module.path:ClassName', e.g. 'langchain_openai:ChatOpenAI'."
        )

    module_path, class_name = model_config.use.split(":", 1)

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

    kwargs: dict = {
        "model": model_config.model,
        "temperature": model_config.temperature,
        **model_config.extra,
    }

    # Different providers use different API key parameter names
    if model_config.api_key:
        if "openai" in module_path.lower():
            kwargs["openai_api_key"] = model_config.api_key
        elif "anthropic" in module_path.lower():
            kwargs["anthropic_api_key"] = model_config.api_key
        else:
            kwargs["api_key"] = model_config.api_key

    logger.info("Creating chat model: %s via %s", model_config.name, model_config.use)
    return cls(**kwargs)
