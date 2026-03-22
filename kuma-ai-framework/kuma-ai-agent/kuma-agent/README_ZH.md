# KumaAgent

KumaAgent 是一个基于 LangGraph 的 AI Agent 后端，具备沙箱代码执行、持久化记忆、多渠道 IM 集成和 gRPC 支持。Agent 可以执行代码、浏览网页、管理文件、将任务委托给子 Agent，并在会话间保留上下文——所有操作均在每个对话线程的隔离环境中进行。

> English documentation: [README.md](README.md)

---

## 架构

```
                        ┌──────────────────────────────────────┐
                        │          Nginx (端口 2026)            │
                        │           统一反向代理                │
                        └───────┬──────────────────┬───────────┘
                                │                  │
              /api/langgraph/*  │                  │  /api/* (其他)
                                ▼                  ▼
               ┌────────────────────┐  ┌────────────────────────┐
               │  LangGraph Server  │  │  Gateway API (8001)    │
               │    (端口 2024)     │  │  app/gateway/server.py │
               │                    │  │                        │
               │  ┌──────────────┐  │  │  Models / MCP /        │
               │  │  Lead Agent  │  │  │  Skills / Memory /     │
               │  │  中间件链    │  │  │  Uploads / Artifacts / │
               │  │  工具系统    │  │  │  Agents / Channels     │
               │  │  子 Agent    │  │  └────────────────────────┘
               │  └──────────────┘  │
               └────────────────────┘
                                          ┌────────────────────────┐
                                          │  gRPC Server (50051)   │
                                          │  app/channels/         │
                                          │  grpc_server.py        │
                                          │  供 Java / Go 客户端   │
                                          └────────────────────────┘
                                          ┌────────────────────────┐
                                          │  IM 渠道               │
                                          │  app/channels/         │
                                          │  飞书 / Slack /        │
                                          │  Telegram              │
                                          └────────────────────────┘
```

**请求路由：**
- `/api/langgraph/*` → LangGraph Server — Agent 交互、会话线程、流式输出
- `/api/*` → Gateway API — 模型、MCP、技能、记忆、文件、工件
- gRPC — Java/Go 客户端通过 `app/channels/grpc_server.py` 接入
- IM — 飞书/Slack/Telegram 通过 `app/channels/` 接入

---

## 项目结构

```
kuma-agent/
├── app/
│   ├── gateway/                   # HTTP Gateway（FastAPI）
│   │   ├── server.py              # 入口 — uv run python app/gateway/server.py
│   │   ├── app.py                 # FastAPI 应用工厂 + 生命周期管理
│   │   ├── config.py              # GatewayConfig（host、port、CORS）
│   │   ├── path_utils.py          # 虚拟路径解析工具
│   │   └── routers/               # 路由模块
│   │       ├── agents.py          # /api/agents
│   │       ├── artifacts.py       # /api/threads/{id}/artifacts
│   │       ├── channels.py        # /api/channels
│   │       ├── memory.py          # /api/memory
│   │       ├── mcp.py             # /api/mcp
│   │       ├── models.py          # /api/models
│   │       ├── skills.py          # /api/skills
│   │       ├── suggestions.py     # /api/threads/{id}/suggestions
│   │       └── uploads.py         # /api/threads/{id}/uploads
│   └── channels/                  # 通信渠道
│       ├── grpc_server.py         # 入口 — uv run python app/channels/grpc_server.py
│       ├── base.py                # 抽象 Channel 基类
│       ├── manager.py             # ChannelManager — 分发消息到 LangGraph Server
│       ├── message_bus.py         # 异步消息总线（入站/出站）
│       ├── service.py             # ChannelService 生命周期管理
│       ├── store.py               # chat_id → thread_id 映射存储
│       ├── feishu.py              # 飞书渠道（流式更新卡片）
│       ├── slack.py               # Slack 渠道
│       └── telegram.py            # Telegram 渠道
├── kuma_agent/                    # 核心包
│   ├── agents/
│   │   ├── lead_agent/            # 主 LangGraph Agent
│   │   ├── middlewares/           # 9 个中间件组件
│   │   ├── memory/                # 记忆提取与存储
│   │   ├── checkpointer/          # LangGraph 检查点提供者
│   │   └── thread_state.py        # ThreadState 状态结构
│   ├── client.py                  # KumaAgentClient（嵌入式用法）
│   ├── config/                    # 配置系统
│   ├── community/                 # 社区工具提供者
│   ├── grpc/                      # gRPC Servicer 及生成的 Stub
│   ├── mcp/                       # Model Context Protocol 集成
│   ├── middlewares/               # KumaAgent 专属中间件
│   ├── models/                    # LLM 模型工厂
│   ├── reflection/                # 动态模块加载
│   ├── sandbox/                   # 沙箱执行环境
│   ├── skills/                    # 技能发现与加载
│   ├── subagents/                 # 子 Agent 委托引擎
│   ├── tools/                     # 内置及社区工具
│   └── utils/                     # 工具函数
├── proto/                         # Protobuf 定义文件
├── scripts/
│   └── gen_proto.py               # 重新生成 gRPC Stub
├── docs/                          # 文档
├── config.yaml                    # 主配置文件
├── pyproject.toml                 # Python 依赖
└── ruff.toml                      # Linter / Formatter 配置
```

---

## 核心组件

### Lead Agent

`lead_agent` 是运行时入口点，通过 `make_lead_agent(config)` 创建，包含：

- **动态模型选择** — 支持思维链（thinking）和视觉（vision）
- **中间件链** — 9 个按序执行的中间件处理横切关注点
- **工具系统** — 沙箱、MCP、社区工具和内置工具
- **子 Agent 委托** — 并行任务执行
- **系统提示** — 注入技能、记忆上下文和工作目录指引

### 中间件链

中间件按严格顺序执行：

| 序号 | 中间件 | 职责 |
|------|--------|------|
| 1 | **ThreadDataMiddleware** | 创建每个线程的隔离目录（workspace、uploads、outputs） |
| 2 | **UploadsMiddleware** | 将新上传文件注入对话上下文 |
| 3 | **SandboxMiddleware** | 获取用于代码执行的沙箱环境 |
| 4 | **SummarizationMiddleware** | 接近 token 上限时压缩上下文 |
| 5 | **TodoListMiddleware** | 在计划模式下追踪多步骤任务 |
| 6 | **TitleMiddleware** | 首次交换后自动生成会话标题 |
| 7 | **MemoryMiddleware** | 将对话加入异步记忆提取队列 |
| 8 | **ViewImageMiddleware** | 为支持视觉的模型注入图像数据 |
| 9 | **ClarificationMiddleware** | 拦截澄清请求并中断执行（必须最后执行） |

### 沙箱系统

带虚拟路径转换的每线程隔离执行：

- **抽象接口**：`execute_command`、`read_file`、`write_file`、`list_dir`
- **提供者**：`LocalSandboxProvider`（本地文件系统）和 `AioSandboxProvider`（Docker，在 `community/` 中）
- **虚拟路径**：`/mnt/user-data/{workspace,uploads,outputs}` → 线程专属物理目录

### 子 Agent 系统

异步任务委托，支持并发执行：

- **内置 Agent**：`general-purpose`（完整工具集）和 `bash`（命令专家）
- **并发限制**：每轮最多 3 个子 Agent，15 分钟超时
- **执行流程**：Agent 调用 `task()` 工具 → 执行器在后台运行子 Agent → 返回结果

### 记忆系统

基于 LLM 的跨会话持久化上下文：

- **自动提取**：分析对话，提取用户背景、事实和偏好
- **防抖更新**：批量合并 LLM 调用，减少 API 开销
- **系统提示注入**：将重要事实和上下文注入 Agent 提示词

### 工具生态

| 类别 | 工具 |
|------|------|
| **沙箱** | `bash`、`ls`、`read_file`、`write_file`、`str_replace` |
| **内置** | `present_files`、`ask_clarification`、`view_image`、`task`（子 Agent） |
| **社区** | Tavily（搜索）、Jina AI（抓取）、Firecrawl（爬取）、DuckDuckGo（图片） |
| **MCP** | 任意 Model Context Protocol 服务（stdio、SSE、HTTP） |
| **技能** | 通过系统提示注入的领域专属工作流 |

### Gateway API

| 路由 | 说明 |
|------|------|
| `GET /api/models` | 查询可用 LLM 模型列表 |
| `GET/PUT /api/mcp/config` | 管理 MCP 服务配置 |
| `GET/PUT /api/skills` | 查询和管理技能 |
| `POST /api/threads/{id}/uploads` | 上传文件（自动转换 PDF/PPT/Excel/Word） |
| `GET /api/threads/{id}/artifacts/{path}` | 获取生成的工件文件 |
| `GET /api/memory` | 读取记忆数据 |
| `GET /api/agents` | 查询自定义 Agent 列表 |
| `GET /api/channels` | IM 渠道状态 |
| `GET /health` | 健康检查 |

### IM 渠道

支持飞书、Slack 和 Telegram。飞书使用流式推送（`runs.stream`），实时更新消息卡片；Slack 和 Telegram 使用 `runs.wait` 等待最终结果后回复。

### gRPC 服务

Java/Go 客户端通过 gRPC（端口 50051）接入。Proto 定义文件：`proto/kuma_agent.proto`。修改 proto 后重新生成 Stub：

```bash
uv run python scripts/gen_proto.py
```

---

## 快速开始

### 前置条件

- Python 3.12+
- [uv](https://docs.astral.sh/uv/) 包管理器
- 所选 LLM 提供商的 API Key

### 安装

```bash
git clone <repo>
cd kuma-agent

# 复制并编辑配置文件
cp config.yaml.example config.yaml

# 安装依赖
uv sync
```

### 配置

编辑 `config.yaml`：

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

设置 API Key：

```bash
export OPENAI_API_KEY="your-key"
export ANTHROPIC_API_KEY="your-key"   # 可选
export TAVILY_API_KEY="your-key"      # 可选，用于网页搜索
```

### 运行

```bash
# HTTP Gateway（REST API）
uv run python app/gateway/server.py

# gRPC 服务（供 Java/Go 客户端）— 独立运行，按需启动
uv run python app/channels/grpc_server.py
```

---

## 配置参考

### `config.yaml` 主要配置项

| 配置段 | 说明 |
|--------|------|
| `models` | LLM 配置 — 类路径、API Key、thinking/vision 开关 |
| `tools` | 工具定义，包含模块路径和分组 |
| `sandbox` | 沙箱提供者类路径 |
| `skills` | 技能目录路径 |
| `memory` | 记忆系统设置（启用、存储路径、防抖时间、事实上限） |
| `summarization` | 上下文摘要设置 |
| `subagents` | 子 Agent 系统开关 |
| `channels` | IM 渠道配置（feishu、slack、telegram） |

### 环境变量

| 变量 | 说明 |
|------|------|
| `KUMA_AGENT_CONFIG_PATH` | 覆盖 config.yaml 路径 |
| `GATEWAY_HOST` / `GATEWAY_PORT` | Gateway 绑定地址（默认：`0.0.0.0:8001`） |
| `OPENAI_API_KEY` | OpenAI API Key |
| `ANTHROPIC_API_KEY` | Anthropic API Key |
| `DEEPSEEK_API_KEY` | DeepSeek API Key |
| `TAVILY_API_KEY` | Tavily 搜索 API Key |

---

## 开发

### 代码风格

```bash
uv run ruff check .      # 代码检查
uv run ruff format .     # 代码格式化
uv run pytest            # 运行测试
```

- **工具**：`ruff`
- **行长度**：240 字符
- **Python 版本**：3.12+，使用类型注解
- **引号**：双引号

---

## 技术栈

- **LangGraph** 1.0.6+ — Agent 框架和多 Agent 编排
- **LangChain** 1.2.3+ — LLM 抽象层和工具系统
- **FastAPI** 0.115.0+ — Gateway REST API
- **gRPC / protobuf** — Java/Go 客户端集成
- **langchain-mcp-adapters** — Model Context Protocol 支持
- **agent-sandbox** — 沙箱代码执行
- **markitdown** — 多格式文档转换（PDF、PPT、Excel、Word）
- **tavily-python / firecrawl-py** — 网页搜索与爬取

---

## License

见 [LICENSE](LICENSE)。
