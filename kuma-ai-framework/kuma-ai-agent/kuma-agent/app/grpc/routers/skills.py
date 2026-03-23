"""Skills RPCs: ListSkills, GetSkill, UpdateSkill."""

import json
import logging
from pathlib import Path

import grpc

from app.grpc.generated import kuma_agent_pb2
from kuma_agent.config.extensions_config import (
    ExtensionsConfig, SkillStateConfig, get_extensions_config, reload_extensions_config,
)
from kuma_agent.skills import load_skills

logger = logging.getLogger(__name__)


def _skill_to_pb(skill) -> kuma_agent_pb2.SkillInfo:
    return kuma_agent_pb2.SkillInfo(
        name=skill.name,
        description=skill.description or "",
        license=skill.license or "",
        category=skill.category or "",
        enabled=skill.enabled,
    )


class SkillsRouter:
    """Mixin: ListSkills, GetSkill, UpdateSkill."""

    def ListSkills(self, request, context):
        try:
            skills = load_skills(enabled_only=False)
            return kuma_agent_pb2.ListSkillsResponse(
                skills=[_skill_to_pb(s) for s in skills]
            )
        except Exception as exc:
            logger.exception("ListSkills failed")
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def GetSkill(self, request, context):
        try:
            skills = load_skills(enabled_only=False)
            skill = next((s for s in skills if s.name == request.name), None)
            if skill is None:
                context.abort(grpc.StatusCode.NOT_FOUND, f"Skill '{request.name}' not found")
                return
            return _skill_to_pb(skill)
        except Exception as exc:
            logger.exception("GetSkill failed for '%s'", request.name)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))

    def UpdateSkill(self, request, context):
        try:
            skills = load_skills(enabled_only=False)
            skill = next((s for s in skills if s.name == request.name), None)
            if skill is None:
                context.abort(grpc.StatusCode.NOT_FOUND, f"Skill '{request.name}' not found")
                return

            config_path = ExtensionsConfig.resolve_config_path()
            if config_path is None:
                config_path = Path.cwd().parent / "extensions_config.json"

            extensions_config = get_extensions_config()
            extensions_config.skills[request.name] = SkillStateConfig(enabled=request.enabled)
            config_data = {
                "mcpServers": {n: s.model_dump() for n, s in extensions_config.mcp_servers.items()},
                "skills": {n: {"enabled": sc.enabled} for n, sc in extensions_config.skills.items()},
            }
            with open(config_path, "w", encoding="utf-8") as f:
                json.dump(config_data, f, indent=2)
            reload_extensions_config()

            skills = load_skills(enabled_only=False)
            updated = next((s for s in skills if s.name == request.name), None)
            if updated is None:
                context.abort(grpc.StatusCode.INTERNAL, f"Failed to reload skill '{request.name}'")
                return
            return _skill_to_pb(updated)
        except Exception as exc:
            logger.exception("UpdateSkill failed for '%s'", request.name)
            context.abort(grpc.StatusCode.INTERNAL, str(exc))
