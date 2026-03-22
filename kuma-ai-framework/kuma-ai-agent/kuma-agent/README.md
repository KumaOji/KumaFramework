# KumaAgent

KumaAgent is a LangGraph-based AI agent backend with sandbox execution, persistent memory, multi-channel IM integration, and gRPC support. Agents can execute code, browse the web, manage files, delegate tasks to subagents, and retain context across conversations — all in isolated, per-thread environments.

> Chinese documentation: [README_ZH.md](README_ZH.md)

---

## Architecture

```
                        ┌──────────────────────────────────────┐
                        │          Nginx (Port 2026)           │
                        │        Unified reverse proxy         │
                        └───────┬──────────────────┬───────────┘
                                │                  │
              /api/langgraph/*  │                  │  /api/* (other)
                                ▼                  ▼
               ┌────────────────────┐  ┌────────────────────────┐
               │  LangGraph Server  │  │  Gateway API (8001)    │
               │    (Port 2024)     │  │  app/gateway/server.py │
               │                    │  │                        │
               │  ┌──────────────┐  │  │  Models, MCP, Skills,  │
               │  │  Lead Agent  │  │  │  Memory, Uploads,      │
               │  │  Middleware  │  │  │  Artifacts, Agents,    │
               │  │  Tools       │  │  │  Suggestions, Channels │
               │  │  Subagents   │  │  └────────────────────────┘
               │  └──────────────┘  │
               └────────────────────┘
                                          ┌────────────────────────┐
                                          │  gRPC Server (50051)   │
                                          │  app/channels/         │
                                          │  grpc_server.py        │
                                          │  Java / Go clients     │
                                          └────────────────────────┘
                                          ┌────────────────────────┐
                                          │  IM Channels           │
                                          │  app/channels/         │
                                          │  Feishu / Slack /      │
                                          │  Telegram              │
                                          └────────────────────────┘
```

**Request Routing:**
- `/api/langgraph/*` → LangGraph Server — agent interactions, threads, streaming
- `/api/*` → Gateway API — models, MCP, skills, memory, artifacts, uploads
- gRPC — Java/Go clients via `app/channels/grpc_server.py`
- IM — Feishu/Slack/Telegram via `app/channels/`

---

## Project Structure

```
kuma-agent/
├── app/
│   ├── demo.py                    # Interactive demo / test client
│   ├── gateway/                   # HTTP Gateway (FastAPI)
│   │   ├── server.py              # Entry point — uv run python app/gateway/server.py
│   │   ├── app.py                 # FastAPI app factory + lifespan
│   │   ├── config.py              # GatewayConfig (host, port, CORS)
│   │   ├── path_utils.py          # Virtual path resolution helper
│   │   └── routers/               # Route modules
│   │       ├── agents.py          # /api/agents
│   │       ├── artifacts.py       # /api/threads/{id}/artifacts
│   │       ├── channels.py        # /api/channels
│   │       ├── memory.py          # /api/memory
│   │       ├── mcp.py             # /api/mcp
│   │       ├── models.py          # /api/models
│   │       ├── skills.py          # /api/skills
│   │       ├── suggestions.py     # /api/threads/{id}/suggestions
│   │       └── uploads.py         # /api/threads/{id}/uploads
│   └── channels/                  # Communication channels
│       ├── grpc_server.py         # Entry point — uv run python app/channels/grpc_server.py
│       ├── base.py                # Abstract Channel base class
│       ├── manager.py             # ChannelManager — dispatches to LangGraph Server
│       ├── message_bus.py         # Async inbound/outbound message bus
│       ├── service.py             # ChannelService lifecycle manager
│       ├── store.py               # chat_id → thread_id mapping store
│       ├── feishu.py              # Feishu/Lark channel (streaming)
│       ├── slack.py               # Slack channel
│       └── telegram.py            # Telegram channel
├── kuma_agent/                    # Core package
│   ├── agents/
│   │   ├── lead_agent/            # Main LangGraph agent
│   │   ├── middlewares/           # 9 middleware components
│   │   ├── memory/                # Memory extraction & storage
│   │   ├── checkpointer/          # LangGraph checkpointer providers
│   │   └── thread_state.py        # ThreadState schema
│   ├── client.py                  # KumaAgentClient (embedded usage)
│   ├── config/                    # Configuration system
│   ├── community/                 # Community tool providers
│   ├── grpc/                      # gRPC servicer & generated stubs
│   ├── mcp/                       # Model Context Protocol integration
│   ├── middlewares/               # KumaAgent-specific middlewares
│   ├── models/                    # LLM model factory
│   ├── reflection/                # Dynamic module loading
│   ├── sandbox/                   # Sandboxed execution environment
│   ├── skills/                    # Skill discovery & loading
│   ├── subagents/                 # Subagent delegation engine
│   ├── tools/                     # Built-in and community tools
│   └── utils/                     # Utilities
├── proto/                         # Protobuf definitions
├── scripts/
│   └── gen_proto.py               # Regenerate gRPC stubs
├── docs/                          # Documentation
├── config.yaml                    # Main configuration
├── pyproject.toml                 # Python dependencies
└── ruff.toml                      # Linter / formatter config
```

---

## Core Components

### Lead Agent

The single LangGraph agent (`lead_agent`) is the runtime entry point, created via `make_lead_agent(config)`. It combines:

- **Dynamic model selection** with thinking and vision support
- **Middleware chain** for cross-cutting concerns (9 middlewares)
- **Tool system** with sandbox, MCP, community, and built-in tools
- **Subagent delegation** for parallel task execution
- **System prompt** with skills injection, memory context, and working directory guidance

### Middleware Chain

Middlewares execute in strict order:

| # | Middleware | Purpose |
|---|-----------|---------|
| 1 | **ThreadDataMiddleware** | Creates per-thread isolated directories (workspace, uploads, outputs) |
| 2 | **UploadsMiddleware** | Injects newly uploaded files into conversation context |
| 3 | **SandboxMiddleware** | Acquires sandbox environment for code execution |
| 4 | **SummarizationMiddleware** | Reduces context when approaching token limits |
| 5 | **TodoListMiddleware** | Tracks multi-step tasks in plan mode |
| 6 | **TitleMiddleware** | Auto-generates conversation titles after first exchange |
| 7 | **MemoryMiddleware** | Queues conversations for async memory extraction |
| 8 | **ViewImageMiddleware** | Injects image data for vision-capable models |
| 9 | **ClarificationMiddleware** | Intercepts clarification requests (must be last) |

### Sandbox System

Per-thread isolated execution with virtual path translation:

- **Abstract interface**: `execute_command`, `read_file`, `write_file`, `list_dir`
- **Providers**: `LocalSandboxProvider` (filesystem) and `AioSandboxProvider` (Docker, in `community/`)
- **Virtual paths**: `/mnt/user-data/{workspace,uploads,outputs}` → thread-specific physical directories

### Subagent System

Async task delegation with concurrent execution:

- **Built-in agents**: `general-purpose` (full toolset) and `bash` (command specialist)
- **Concurrency**: Max 3 subagents per turn, 15-minute timeout
- **Flow**: Agent calls `task()` tool → executor runs subagent in background → returns result

### Memory System

LLM-powered persistent context across conversations:

- **Automatic extraction**: Analyzes conversations for user context, facts, and preferences
- **Debounced updates**: Batches LLM calls with configurable wait time
- **System prompt injection**: Top facts + context injected into agent prompts

### Tool Ecosystem

| Category | Tools |
|----------|-------|
| **Sandbox** | `bash`, `ls`, `read_file`, `write_file`, `str_replace` |
| **Built-in** | `present_files`, `ask_clarification`, `view_image`, `task` (subagent) |
| **Community** | Tavily (search), Jina AI (fetch), Firecrawl (scraping), DuckDuckGo (images) |
| **MCP** | Any Model Context Protocol server (stdio, SSE, HTTP) |
| **Skills** | Domain-specific workflows injected via system prompt |

### Gateway API

| Route | Purpose |
|-------|---------|
| `GET /api/models` | List available LLM models |
| `GET/PUT /api/mcp/config` | Manage MCP server configurations |
| `GET/PUT /api/skills` | List and manage skills |
| `POST /api/threads/{id}/uploads` | Upload files (auto-converts PDF/PPT/Excel/Word) |
| `GET /api/threads/{id}/artifacts/{path}` | Serve generated artifacts |
| `GET /api/memory` | Retrieve memory data |
| `GET /api/agents` | List custom agents |
| `GET /api/channels` | IM channel status |

### IM Channels

Feishu, Slack, and Telegram are supported. Feishu uses streaming (`runs.stream`) and updates a live card in-thread. Slack and Telegram use `runs.wait` for final responses.

### gRPC Server

Java/Go clients connect via gRPC at port 50051. Proto definition: `proto/kuma_agent.proto`. Regenerate stubs after editing:

```bash
uv run python scripts/gen_proto.py
```

---

## Quick Start

### Prerequisites

- Python 3.12+
- [uv](https://docs.astral.sh/uv/) package manager
- API key for your chosen LLM provider

### Installation

```bash
git clone <repo>
cd kuma-agent

# Copy and edit configuration
cp config.yaml.example config.yaml

# Install dependencies
uv sync
```

### Configuration

Edit `config.yaml`:

```yaml
models:
  - name: gpt-4o
    display_name: GPT-4o
    use: langchain_openai:ChatOpenAI
    model: gpt-4o
    api_key: $OPENAI_API_KEY
    supports_thinking: false
    supports_vision: true
```

Set API keys:

```bash
export OPENAI_API_KEY="your-key"
export ANTHROPIC_API_KEY="your-key"   # optional
export TAVILY_API_KEY="your-key"      # optional, for web search
```

### Running

```bash
# Gateway (HTTP REST API)
uv run python app/gateway/server.py

# gRPC server (for Java/Go clients)
uv run python app/channels/grpc_server.py

# Interactive demo
uv run python app/demo.py --interactive
uv run python app/demo.py --config config.yaml
```

---

## Configuration Reference

### `config.yaml`

| Section | Description |
|---------|-------------|
| `models` | LLM configurations — class path, API key, thinking/vision flags |
| `tools` | Tool definitions with module paths and groups |
| `sandbox` | Execution environment provider class path |
| `skills` | Skills directory paths |
| `memory` | Memory system settings (enabled, storage, debounce, facts limit) |
| `summarization` | Context summarization settings |
| `subagents` | Subagent system toggle |
| `channels` | IM channel configs (feishu, slack, telegram) |

### Environment Variables

| Variable | Purpose |
|----------|---------|
| `KUMA_AGENT_CONFIG_PATH` | Override config.yaml location |
| `GATEWAY_HOST` / `GATEWAY_PORT` | Gateway bind address (default: `0.0.0.0:8001`) |
| `OPENAI_API_KEY` | OpenAI API key |
| `ANTHROPIC_API_KEY` | Anthropic API key |
| `DEEPSEEK_API_KEY` | DeepSeek API key |
| `TAVILY_API_KEY` | Tavily search API key |

---

## Development

### Code Style

```bash
uv run ruff check .      # lint
uv run ruff format .     # format
uv run pytest            # tests
```

- **Linter/Formatter**: `ruff`
- **Line length**: 240 characters
- **Python**: 3.12+ with type hints
- **Quotes**: Double quotes

---

## Technology Stack

- **LangGraph** 1.0.6+ — Agent framework and multi-agent orchestration
- **LangChain** 1.2.3+ — LLM abstractions and tool system
- **FastAPI** 0.115.0+ — Gateway REST API
- **gRPC / protobuf** — Java/Go client integration
- **langchain-mcp-adapters** — Model Context Protocol support
- **agent-sandbox** — Sandboxed code execution
- **markitdown** — Multi-format document conversion (PDF, PPT, Excel, Word)
- **tavily-python / firecrawl-py** — Web search and scraping

---

## License

See [LICENSE](LICENSE).
