"""
KumaAgentServicer — composite gRPC service implementation.

Mirrors the gateway router pattern: each domain lives in its own
router mixin under app/grpc/routers/, and this class simply inherits
from all of them.

Router mapping (parallel to app/gateway/routers/):
  SystemRouter  → HealthCheck, GetConfig
  ChatRouter    → Chat, ChatStream, ListThreads, DeleteThread
  ModelsRouter  → ListModels
  AgentsRouter  → ListAgents, GetAgent, CreateAgent, UpdateAgent, DeleteAgent,
                  GetUserProfile, UpdateUserProfile
  MemoryRouter  → GetMemory, ReloadMemory
  SkillsRouter  → ListSkills, GetSkill, UpdateSkill
"""

import logging

from kuma_agent.client import KumaAgentClient
from app.grpc.generated import kuma_agent_pb2_grpc
from app.grpc.routers.system import SystemRouter
from app.grpc.routers.chat import ChatRouter
from app.grpc.routers.models import ModelsRouter
from app.grpc.routers.agents import AgentsRouter
from app.grpc.routers.memory import MemoryRouter
from app.grpc.routers.skills import SkillsRouter

logger = logging.getLogger(__name__)


class KumaAgentServicer(
    SystemRouter,
    ChatRouter,
    ModelsRouter,
    AgentsRouter,
    MemoryRouter,
    SkillsRouter,
    kuma_agent_pb2_grpc.KumaAgentServiceServicer,
):
    """Composite servicer — all RPC methods come from router mixins."""

    def __init__(self, client: KumaAgentClient):
        self._client = client
        # Thread registry: thread_id -> {title, message_count}
        # Used by ChatRouter helpers (_resolve_thread, _refresh_meta)
        self._threads: dict[str, dict] = {}
