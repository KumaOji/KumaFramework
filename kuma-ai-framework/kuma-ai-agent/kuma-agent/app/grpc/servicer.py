"""
KumaAgentServicer — composite gRPC service implementation.

Mirrors the gateway router pattern: each domain lives in its own
router mixin under app/grpc/routers/, and this class simply inherits
from all of them.

Router mapping (parallel to app/gateway/routers/):
  SystemRouter   → HealthCheck
  ModelsRouter   → ListModels, GetModel
  AgentsRouter   → ListAgents, CheckAgentName, GetAgent, CreateAgent, UpdateAgent,
                   DeleteAgent, GetUserProfile, UpdateUserProfile
  MemoryRouter   → GetMemory, ReloadMemory, GetMemoryConfig, GetMemoryStatus
  SkillsRouter   → ListSkills, GetSkill, UpdateSkill
  McpRouter      → GetMcpConfig, UpdateMcpConfig
  ChannelsRouter → GetChannelsStatus, RestartChannel
"""

import logging

from kuma_agent.client import KumaAgentClient
from app.grpc.generated import kuma_agent_pb2_grpc
from app.grpc.routers.system import SystemRouter
from app.grpc.routers.models import ModelsRouter
from app.grpc.routers.agents import AgentsRouter
from app.grpc.routers.memory import MemoryRouter
from app.grpc.routers.skills import SkillsRouter
from app.grpc.routers.mcp import McpRouter
from app.grpc.routers.channels import ChannelsRouter

logger = logging.getLogger(__name__)


class KumaAgentServicer(
    SystemRouter,
    ModelsRouter,
    AgentsRouter,
    MemoryRouter,
    SkillsRouter,
    McpRouter,
    ChannelsRouter,
    kuma_agent_pb2_grpc.KumaAgentServiceServicer,
):
    """Composite servicer — all RPC methods come from router mixins."""

    def __init__(self, client: KumaAgentClient):
        self._client = client
