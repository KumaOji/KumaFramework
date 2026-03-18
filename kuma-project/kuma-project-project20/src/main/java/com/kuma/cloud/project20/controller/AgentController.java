package com.kuma.cloud.project20.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.kuma.ai.agent.grpc.*;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AgentController —— HTTP 入口，将请求转发给 kuma-ai-agent gRPC 服务
 *
 * <p>接口列表：
 * <pre>
 *   POST   /agent/chat                    阻塞式对话
 *   GET    /agent/chat/stream             流式对话（SSE）
 *   GET    /agent/threads                 列出所有会话线程
 *   DELETE /agent/threads/{threadId}      删除指定线程
 *   GET    /agent/config                  查询 Agent 配置
 *   GET    /agent/health                  健康检查
 * </pre>
 *
 * <p>非流接口均以 {@link JsonFormat} 将 Protobuf 消息序列化为 JSON 字符串返回。
 * 流接口使用 SSE，每条 {@link StreamToken} 转为 {@link StreamTokenEvent} 后推送。
 */
@Slf4j
@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

    /** Protobuf → JSON（含默认值字段，方便调试） */
    private static final JsonFormat.Printer JSON_PRINTER =
            JsonFormat.printer().includingDefaultValueFields();

    private final KumaAgentServiceGrpc.KumaAgentServiceBlockingStub blockingStub;

    /** SSE 专用线程池，避免占用 Tomcat 请求线程 */
    private final ExecutorService sseExecutor = Executors.newCachedThreadPool();

    // ─────────────────────────────────────────────────────────────────────────
    // Chat
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * 阻塞式对话，等待完整回复。
     *
     * <p>示例：
     * <pre>POST /agent/chat?message=你好&amp;threadId=</pre>
     *
     * @param message  用户消息
     * @param threadId 会话 ID（空字符串时由服务端自动生成）
     */
    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public String chat(
            @RequestParam String message,
            @RequestParam(required = false, defaultValue = "") String threadId)
            throws InvalidProtocolBufferException {
        ChatRequest request = ChatRequest.newBuilder()
                .setMessage(message)
                .setThreadId(threadId)
                .build();
        ChatResponse response = blockingStub.chat(request);
        return JSON_PRINTER.print(response);
    }

    /**
     * 流式对话（Server-Sent Events），逐 token 推送。
     *
     * <p>示例：
     * <pre>GET /agent/chat/stream?message=你好&amp;threadId=</pre>
     *
     * @param message  用户消息
     * @param threadId 会话 ID（空字符串时由服务端自动生成）
     */
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(
            @RequestParam String message,
            @RequestParam(required = false, defaultValue = "") String threadId) {
        SseEmitter emitter = new SseEmitter(0L);
        ChatRequest request = ChatRequest.newBuilder()
                .setMessage(message)
                .setThreadId(threadId)
                .build();

        sseExecutor.submit(() -> {
            try {
                Iterator<StreamToken> iterator = blockingStub.chatStream(request);
                while (iterator.hasNext()) {
                    StreamToken token = iterator.next();
                    emitter.send(SseEmitter.event().data(StreamTokenEvent.from(token)));
                    if (token.getDone()) {
                        break;
                    }
                }
                emitter.complete();
            } catch (StatusRuntimeException e) {
                log.error("[Agent gRPC] chatStream RPC error: {}", e.getStatus(), e);
                emitter.completeWithError(e);
            } catch (Exception e) {
                log.error("[Agent gRPC] chatStream error", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Threads
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * 列出所有会话线程。
     *
     * <p>示例：{@code GET /agent/threads}
     */
    @GetMapping(value = "/threads", produces = MediaType.APPLICATION_JSON_VALUE)
    public String listThreads() throws InvalidProtocolBufferException {
        ListThreadsResponse response = blockingStub.listThreads(
                ListThreadsRequest.newBuilder().build());
        return JSON_PRINTER.print(response);
    }

    /**
     * 删除指定会话线程。
     *
     * <p>示例：{@code DELETE /agent/threads/abc-123}
     *
     * @param threadId 要删除的线程 ID
     */
    @DeleteMapping(value = "/threads/{threadId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteThread(@PathVariable String threadId)
            throws InvalidProtocolBufferException {
        DeleteThreadResponse response = blockingStub.deleteThread(
                DeleteThreadRequest.newBuilder().setThreadId(threadId).build());
        return JSON_PRINTER.print(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Config & Health
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * 查询 Agent 配置（模型名称、可用工具等）。
     *
     * <p>示例：{@code GET /agent/config}
     */
    @GetMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getConfig() throws InvalidProtocolBufferException {
        GetConfigResponse response = blockingStub.getConfig(
                GetConfigRequest.newBuilder().build());
        return JSON_PRINTER.print(response);
    }

    /**
     * 健康检查，返回 {@code {"status":"ok"}} 表示服务正常。
     *
     * <p>示例：{@code GET /agent/health}
     */
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public String healthCheck() throws InvalidProtocolBufferException {
        HealthCheckResponse response = blockingStub.healthCheck(
                HealthCheckRequest.newBuilder().build());
        return JSON_PRINTER.print(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SSE DTO
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * SSE 推送事件 DTO（Jackson 可序列化）。
     * 对应 proto {@code StreamToken} 的字段，驼峰命名。
     */
    record StreamTokenEvent(
            String threadId,
            String delta,
            boolean done,
            String response,
            String title) {

        static StreamTokenEvent from(StreamToken token) {
            return new StreamTokenEvent(
                    token.getThreadId(),
                    token.getDelta(),
                    token.getDone(),
                    token.getResponse(),
                    token.getTitle());
        }
    }
}
