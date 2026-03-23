"""Agents RPCs: ListAgents, CheckAgentName, GetAgent, CreateAgent, UpdateAgent, DeleteAgent,
GetUserProfile, UpdateUserProfile."""

import logging
import re
import shutil

import grpc
import yaml

from app.grpc.generated import kuma_agent_pb2
from kuma_agent.config.agents_config import AgentConfig, list_custom_agents, load_agent_config, load_agent_soul
from kuma_agent.config.paths import get_paths

_AGENT_NAME_PATTERN = re.compile(r"^[A-Za-z0-9-]+$")

logger = logging.getLogger(__name__)


def _agent_to_pb(agent_cfg: AgentConfig, soul: str = "") -> kuma_agent_pb2.AgentInfo:
    return kuma_agent_pb2.AgentInfo(
        name=agent_cfg.name,
        description=agent_cfg.description or "",
        model=agent_cfg.model or "",
        tool_groups=list(agent_cfg.tool_groups or []),
        soul=soul,
    )


class AgentsRouter:
    """Mixin: agents CRUD + user profile."""

    def CheckAgentName(self, request, context):
        try:
            if not _AGENT_NAME_PATTERN.match(request.name):
                context.abort(
                    grpc.StatusCode.INVALID_ARGUMENT,
                    f"Invalid agent name '{request.name}'. Must match ^[A-Za-z0-9-]+$",
                )
                return
            normalized = request.name.lower()
            available = not get_paths().agent_dir(normalized).exists()
            return kuma_agent_pb2.CheckAgentNameResponse(available=available, name=normalized)
        except Exception as exc:
            logger.exception("CheckAgentName failed for '%s'", request.name)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def ListAgents(self, request, context):
        try:
            agents = list_custom_agents()
            return kuma_agent_pb2.ListAgentsResponse(
                agents=[_agent_to_pb(a) for a in agents]
            )
        except Exception as exc:
            logger.exception("ListAgents failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def GetAgent(self, request, context):
        try:
            agent_cfg = load_agent_config(request.name)
            soul = load_agent_soul(request.name) or ""
            return _agent_to_pb(agent_cfg, soul=soul)
        except FileNotFoundError:
            context.abort(grpc.StatusCode.NOT_FOUND, f"Agent '{request.name}' not found")
        except Exception as exc:
            logger.exception("GetAgent failed for '%s'", request.name)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def CreateAgent(self, request, context):
        name = request.name.lower()
        agent_dir = get_paths().agent_dir(name)
        if agent_dir.exists():
            context.abort(grpc.StatusCode.ALREADY_EXISTS, f"Agent '{name}' already exists")
            return
        try:
            agent_dir.mkdir(parents=True, exist_ok=True)
            config_data: dict = {"name": name}
            if request.description:
                config_data["description"] = request.description
            if request.model:
                config_data["model"] = request.model
            if request.tool_groups:
                config_data["tool_groups"] = list(request.tool_groups)
            with open(agent_dir / "config.yaml", "w", encoding="utf-8") as f:
                yaml.dump(config_data, f, default_flow_style=False, allow_unicode=True)
            (agent_dir / "SOUL.md").write_text(request.soul, encoding="utf-8")
            agent_cfg = load_agent_config(name)
            soul = load_agent_soul(name) or ""
            return _agent_to_pb(agent_cfg, soul=soul)
        except Exception as exc:
            if agent_dir.exists():
                shutil.rmtree(agent_dir)
            logger.exception("CreateAgent failed for '%s'", name)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def UpdateAgent(self, request, context):
        name = request.name.lower()
        try:
            agent_cfg = load_agent_config(name)
        except FileNotFoundError:
            context.abort(grpc.StatusCode.NOT_FOUND, f"Agent '{name}' not found")
            return
        try:
            agent_dir = get_paths().agent_dir(name)
            updated: dict = {"name": agent_cfg.name}
            updated["description"] = (
                request.description if request.update_description else agent_cfg.description
            )
            new_model = request.model if request.update_model else agent_cfg.model
            if new_model:
                updated["model"] = new_model
            new_groups = list(request.tool_groups) if request.update_tool_groups else agent_cfg.tool_groups
            if new_groups:
                updated["tool_groups"] = new_groups
            with open(agent_dir / "config.yaml", "w", encoding="utf-8") as f:
                yaml.dump(updated, f, default_flow_style=False, allow_unicode=True)
            if request.update_soul:
                (agent_dir / "SOUL.md").write_text(request.soul, encoding="utf-8")
            refreshed = load_agent_config(name)
            soul = load_agent_soul(name) or ""
            return _agent_to_pb(refreshed, soul=soul)
        except Exception as exc:
            logger.exception("UpdateAgent failed for '%s'", name)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def DeleteAgent(self, request, context):
        name = request.name.lower()
        agent_dir = get_paths().agent_dir(name)
        if not agent_dir.exists():
            context.abort(grpc.StatusCode.NOT_FOUND, f"Agent '{name}' not found")
            return
        try:
            shutil.rmtree(agent_dir)
            logger.info("Deleted agent '%s'", name)
            return kuma_agent_pb2.DeleteAgentResponse(deleted=name)
        except Exception as exc:
            logger.exception("DeleteAgent failed for '%s'", name)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def GetUserProfile(self, request, context):
        try:
            user_md_path = get_paths().user_md_file
            if not user_md_path.exists():
                return kuma_agent_pb2.UserProfileResponse(content="")
            raw = user_md_path.read_text(encoding="utf-8").strip()
            return kuma_agent_pb2.UserProfileResponse(content=raw)
        except Exception as exc:
            logger.exception("GetUserProfile failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def UpdateUserProfile(self, request, context):
        try:
            paths = get_paths()
            paths.base_dir.mkdir(parents=True, exist_ok=True)
            paths.user_md_file.write_text(request.content, encoding="utf-8")
            return kuma_agent_pb2.UserProfileResponse(content=request.content)
        except Exception as exc:
            logger.exception("UpdateUserProfile failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))
