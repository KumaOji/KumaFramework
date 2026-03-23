"""
Configuration system for KumaAgent.

Pattern from kuma_agent's config/app_config.py:
  - YAML-based configuration
  - Environment variable resolution (values starting with $)
  - Fallback search across multiple config file locations
"""

import os
import logging
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any

import yaml

logger = logging.getLogger(__name__)


@dataclass
class ModelConfig:
    """Configuration for a single LLM model.

    The 'use' field is a reflection path 'module:ClassName',
    following kuma_agent's convention for pluggable model providers.
    """
    name: str
    use: str          # e.g. "langchain_openai:ChatOpenAI"
    model: str        # model identifier passed to the provider
    api_key: str | None = None
    temperature: float = 0.7
    extra: dict = field(default_factory=dict)


@dataclass
class AgentConfig:
    name: str = "KumaAgent"
    description: str = "A helpful AI assistant built on LangGraph"
    default_model: str | None = None


@dataclass
class ToolsConfig:
    calculator: bool = True
    current_time: bool = True
    web_search: bool = False


@dataclass
class MiddlewaresConfig:
    log: bool = True
    title: bool = True


@dataclass
class AppConfig:
    models: list[ModelConfig] = field(default_factory=list)
    agent: AgentConfig = field(default_factory=AgentConfig)
    tools: ToolsConfig = field(default_factory=ToolsConfig)
    middlewares: MiddlewaresConfig = field(default_factory=MiddlewaresConfig)

    def get_default_model(self) -> ModelConfig | None:
        if not self.models:
            return None
        if self.agent.default_model:
            for m in self.models:
                if m.name == self.agent.default_model:
                    return m
        return self.models[0]

    @classmethod
    def from_file(cls, config_path: str | Path | None = None) -> "AppConfig":
        """Load config from YAML file.

        Priority (same as kuma_agent):
          1. Explicit config_path argument
          2. KUMA_AGENT_CONFIG_PATH environment variable
          3. config.yaml in current directory
          4. config.yaml next to the package root
        """
        path = _resolve_config_path(config_path)
        if path is None or not path.exists():
            logger.warning(
                "No config.yaml found. Using defaults. "
                "Copy config.yaml.example to config.yaml to configure."
            )
            return cls()

        logger.info("Loading config from: %s", path)
        with open(path, encoding="utf-8") as f:
            data = yaml.safe_load(f) or {}

        return cls._parse(data)

    @classmethod
    def _parse(cls, data: dict) -> "AppConfig":
        # Parse models list
        models = []
        for m in data.get("models", []):
            mc = ModelConfig(
                name=m["name"],
                use=m["use"],
                model=m.get("model", m["name"]),
                api_key=_resolve_env(m.get("api_key")),
                temperature=m.get("temperature", 0.7),
                extra={k: v for k, v in m.items() if k not in ("name", "use", "model", "api_key", "temperature")},
            )
            models.append(mc)

        a = data.get("agent", {})
        agent = AgentConfig(
            name=a.get("name", "KumaAgent"),
            description=a.get("description", "A helpful AI assistant built on LangGraph"),
            default_model=a.get("default_model"),
        )

        t = data.get("tools", {})
        tools = ToolsConfig(
            calculator=t.get("calculator", True),
            current_time=t.get("current_time", True),
            web_search=t.get("web_search", False),
        )

        mw = data.get("middlewares", {})
        middlewares = MiddlewaresConfig(
            log=mw.get("log", True),
            title=mw.get("title", True),
        )

        return cls(models=models, agent=agent, tools=tools, middlewares=middlewares)


def _resolve_config_path(config_path: str | Path | None) -> Path | None:
    if config_path:
        return Path(config_path)
    env = os.getenv("KUMA_AGENT_CONFIG_PATH")
    if env:
        return Path(env)
    candidates = [
        Path("config.yaml"),
        Path(__file__).parent.parent.parent / "config.yaml",
    ]
    for p in candidates:
        if p.exists():
            return p
    return None


def _resolve_env(value: Any) -> Any:
    """Resolve environment variable references (values starting with $)."""
    if isinstance(value, str) and value.startswith("$"):
        return os.getenv(value[1:])
    return value
