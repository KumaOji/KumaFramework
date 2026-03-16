#!/usr/bin/env python3
"""
KumaAgent HTTP Server

A persistent FastAPI server for conversational AI via REST API + SSE streaming.

Endpoints:
    GET  /health                    — liveness check
    GET  /api/config                — agent configuration summary
    POST /api/chat                  — blocking chat (returns full response)
    POST /api/chat/stream           — SSE streaming chat
    GET  /api/threads               — list active thread metadata
    DELETE /api/threads/{thread_id} — clear a thread's title (history in checkpointer)

Run:
    uv run python app/server.py
    uv run python app/server.py --port 8080
    uv run python app/server.py --config /path/to/config.yaml --host 0.0.0.0
"""

import asyncio
import json
import logging
import sys
import uuid
from contextlib import asynccontextmanager
from pathlib import Path
from typing import AsyncGenerator

import uvicorn
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import StreamingResponse
from pydantic import BaseModel, Field

# ---------------------------------------------------------------------------
# Path setup — allow running directly without package install
# ---------------------------------------------------------------------------
sys.path.insert(0, str(Path(__file__).parent.parent / "packages"))

from kuma_agent.client import KumaAgentClient

# ---------------------------------------------------------------------------
# Logging
# ---------------------------------------------------------------------------
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
    datefmt="%H:%M:%S",
)
logging.getLogger("httpx").setLevel(logging.WARNING)
logging.getLogger("httpcore").setLevel(logging.WARNING)

logger = logging.getLogger(__name__)

# ---------------------------------------------------------------------------
# Global state
# ---------------------------------------------------------------------------
_client: KumaAgentClient | None = None

# Thread registry: thread_id -> metadata dict
# Tracks active threads; history lives in the LangGraph MemorySaver
_threads: dict[str, dict] = {}


def get_client() -> KumaAgentClient:
    """Return the singleton KumaAgentClient, raising if not initialised."""
    if _client is None:
        raise RuntimeError("Agent client not initialised")
    return _client


# ---------------------------------------------------------------------------
# Lifespan (startup / shutdown)
# ---------------------------------------------------------------------------
@asynccontextmanager
async def lifespan(app: FastAPI):
    global _client
    logger.info("Starting KumaAgent server …")
    _client = KumaAgentClient(config_path=app.state.config_path)
    cfg = _client.get_config()
    logger.info("Agent ready  — name=%s  model=%s  tools=%s",
                cfg["agent_name"], cfg["model"], cfg["tools"])
    yield
    logger.info("KumaAgent server shutting down.")


# ---------------------------------------------------------------------------
# FastAPI app
# ---------------------------------------------------------------------------
app = FastAPI(
    title="KumaAgent API",
    description="Persistent conversational AI server powered by LangGraph",
    version="0.1.0",
    lifespan=lifespan,
)

app.state.config_path = None  # overridden by CLI before app start

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


# ---------------------------------------------------------------------------
# Request / Response schemas
# ---------------------------------------------------------------------------
class ChatRequest(BaseModel):
    message: str = Field(..., min_length=1, description="User message text")
    thread_id: str | None = Field(
        default=None,
        description="Conversation thread ID. Omit to auto-generate a new thread.",
    )


class ChatResponse(BaseModel):
    thread_id: str
    response: str
    title: str | None = None


class ThreadInfo(BaseModel):
    thread_id: str
    title: str | None = None
    message_count: int = 0


class ThreadListResponse(BaseModel):
    threads: list[ThreadInfo]


class ConfigResponse(BaseModel):
    agent_name: str
    description: str
    model: str | None
    tools: list[str]
    middlewares: dict


# ---------------------------------------------------------------------------
# Helper: resolve / register thread
# ---------------------------------------------------------------------------
def _resolve_thread(thread_id: str | None) -> str:
    """Return existing thread_id or create a new one."""
    if not thread_id:
        thread_id = str(uuid.uuid4())
    if thread_id not in _threads:
        _threads[thread_id] = {"thread_id": thread_id, "title": None, "message_count": 0}
    return thread_id


def _update_thread_meta(thread_id: str, client: KumaAgentClient) -> None:
    """Refresh title and message count in the registry."""
    if thread_id not in _threads:
        _threads[thread_id] = {"thread_id": thread_id, "title": None, "message_count": 0}

    title = client.get_title(thread_id)
    if title:
        _threads[thread_id]["title"] = title

    # Count messages from checkpointer state
    try:
        agent = client._ensure_agent()
        state = agent.get_state({"configurable": {"thread_id": thread_id}})
        if state and state.values:
            msgs = state.values.get("messages", [])
            _threads[thread_id]["message_count"] = len(msgs)
    except Exception:
        pass


# ---------------------------------------------------------------------------
# Routes
# ---------------------------------------------------------------------------
@app.get("/health", tags=["Health"])
async def health() -> dict:
    """Liveness probe — always returns 200 while the server is running."""
    return {"status": "ok"}


@app.get("/api/config", response_model=ConfigResponse, tags=["Config"])
async def get_config() -> ConfigResponse:
    """Return the current agent configuration."""
    cfg = get_client().get_config()
    return ConfigResponse(**cfg)


@app.post("/api/chat", response_model=ChatResponse, tags=["Chat"])
async def chat(req: ChatRequest) -> ChatResponse:
    """
    Blocking chat endpoint.

    Send a message and wait for the full agent response.
    Use `/api/chat/stream` for incremental streaming.

    - **message**: The user's input text.
    - **thread_id**: Optional conversation ID. Omit to start a new thread.
    """
    client = get_client()
    thread_id = _resolve_thread(req.thread_id)

    loop = asyncio.get_event_loop()
    response = await loop.run_in_executor(
        None, lambda: client.chat(req.message, thread_id=thread_id)
    )

    _update_thread_meta(thread_id, client)

    return ChatResponse(
        thread_id=thread_id,
        response=response,
        title=_threads[thread_id].get("title"),
    )


@app.post("/api/chat/stream", tags=["Chat"])
async def chat_stream(req: ChatRequest) -> StreamingResponse:
    """
    SSE streaming chat endpoint.

    Returns a `text/event-stream` response. Each event is a JSON object:

    ```
    event: token
    data: {"thread_id": "...", "delta": "partial text", "done": false}

    event: done
    data: {"thread_id": "...", "response": "full text", "title": "...", "done": true}
    ```

    - **message**: The user's input text.
    - **thread_id**: Optional conversation ID. Omit to start a new thread.
    """
    client = get_client()
    thread_id = _resolve_thread(req.thread_id)

    async def event_generator() -> AsyncGenerator[str, None]:
        """Stream agent tokens as SSE events."""
        queue: asyncio.Queue[dict | None] = asyncio.Queue()
        loop = asyncio.get_event_loop()

        def _run_stream() -> None:
            """Blocking generator consumed in a thread-pool worker."""
            last_content = ""
            try:
                for event in client.stream(req.message, thread_id=thread_id):
                    messages = event.get("messages", [])
                    if not messages:
                        continue
                    last = messages[-1]
                    content = getattr(last, "content", "")
                    if isinstance(content, str) and not getattr(last, "tool_calls", None):
                        delta = content[len(last_content):]
                        if delta:
                            loop.call_soon_threadsafe(
                                queue.put_nowait,
                                {"type": "token", "delta": delta, "full": content},
                            )
                            last_content = content
            except Exception as exc:
                loop.call_soon_threadsafe(
                    queue.put_nowait,
                    {"type": "error", "message": str(exc)},
                )
            finally:
                loop.call_soon_threadsafe(queue.put_nowait, None)  # sentinel

        # Run the blocking stream in a background thread
        loop.run_in_executor(None, _run_stream)

        full_response = ""
        while True:
            item = await queue.get()
            if item is None:
                break  # Stream finished

            if item["type"] == "error":
                yield f"event: error\ndata: {json.dumps({'error': item['message']})}\n\n"
                break

            if item["type"] == "token":
                full_response = item["full"]
                payload = {
                    "thread_id": thread_id,
                    "delta": item["delta"],
                    "done": False,
                }
                yield f"event: token\ndata: {json.dumps(payload, ensure_ascii=False)}\n\n"

        # Final "done" event with full response and metadata
        _update_thread_meta(thread_id, client)
        done_payload = {
            "thread_id": thread_id,
            "response": full_response,
            "title": _threads[thread_id].get("title"),
            "done": True,
        }
        yield f"event: done\ndata: {json.dumps(done_payload, ensure_ascii=False)}\n\n"

    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "X-Accel-Buffering": "no",  # Disable nginx buffering
        },
    )


@app.get("/api/threads", response_model=ThreadListResponse, tags=["Threads"])
async def list_threads() -> ThreadListResponse:
    """List all known conversation threads with their metadata."""
    client = get_client()
    result = []
    for tid, meta in _threads.items():
        # Refresh title from checkpointer on each list call
        title = client.get_title(tid) or meta.get("title")
        result.append(ThreadInfo(
            thread_id=tid,
            title=title,
            message_count=meta.get("message_count", 0),
        ))
    return ThreadListResponse(threads=result)


@app.delete("/api/threads/{thread_id}", tags=["Threads"])
async def delete_thread(thread_id: str) -> dict:
    """
    Remove a thread from the registry.

    Note: LangGraph's in-memory MemorySaver retains the conversation history
    until the process restarts. This endpoint only removes the server-side
    metadata entry.
    """
    if thread_id not in _threads:
        raise HTTPException(status_code=404, detail=f"Thread '{thread_id}' not found")
    del _threads[thread_id]
    return {"deleted": thread_id}


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------
if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="KumaAgent HTTP Server")
    parser.add_argument("--host", default="127.0.0.1", help="Bind host (default: 127.0.0.1)")
    parser.add_argument("--port", type=int, default=8000, help="Bind port (default: 8000)")
    parser.add_argument("--config", "-c", default=None, help="Path to config.yaml")
    parser.add_argument("--reload", action="store_true", help="Enable auto-reload (dev mode)")
    parser.add_argument("--log-level", default="info",
                        choices=["debug", "info", "warning", "error"],
                        help="Uvicorn log level")
    args = parser.parse_args()

    # Inject config path into app state before uvicorn starts
    app.state.config_path = args.config

    print(f"\n{'='*55}")
    print(f"  KumaAgent Server  —  http://{args.host}:{args.port}")
    print(f"  API Docs          —  http://{args.host}:{args.port}/docs")
    print(f"  Press Ctrl+C to stop")
    print(f"{'='*55}\n")

    uvicorn.run(
        app,
        host=args.host,
        port=args.port,
        log_level=args.log_level,
        reload=args.reload,
    )
