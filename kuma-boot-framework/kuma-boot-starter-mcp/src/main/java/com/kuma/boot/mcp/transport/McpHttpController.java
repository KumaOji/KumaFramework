package com.kuma.boot.mcp.transport;

import com.kuma.boot.mcp.protocol.JsonRpc;
import com.kuma.boot.mcp.server.McpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MCP HTTP 传输层。
 *
 * <ul>
 *   <li><b>POST</b> {@code ${kuma.boot.mcp.endpoint}}:Streamable HTTP 传输,客户端 POST 一条
 *       JSON-RPC 请求,服务端同步返回 JSON-RPC 响应(通知类请求返回 202)。这是完整可用的传输。</li>
 *   <li><b>GET</b> {@code ${kuma.boot.mcp.sse-endpoint}}:打开 SSE 流,首帧推送 {@code endpoint}
 *       事件告知消息回传地址,作为服务端 → 客户端的通知通道(骨架,扩展点)。</li>
 * </ul>
 */
@RestController
public class McpHttpController {

    private static final Logger log = LoggerFactory.getLogger(McpHttpController.class);

    private final McpServer server;
    private final String messageEndpoint;
    private final List<SseEmitter> sessions = new CopyOnWriteArrayList<>();

    public McpHttpController(McpServer server, String messageEndpoint) {
        this.server = server;
        this.messageEndpoint = messageEndpoint;
    }

    /** Streamable HTTP:同步处理 JSON-RPC 请求 */
    @PostMapping(path = "${kuma.boot.mcp.endpoint:/mcp}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonRpc.Response> handle(@RequestBody JsonRpc.Request request) {
        JsonRpc.Response response = server.handle(request);
        // 通知类请求无响应体,返回 202 Accepted
        return response == null ? ResponseEntity.accepted().build() : ResponseEntity.ok(response);
    }

    /** SSE 通知通道:打开长连接并推送消息回传地址 */
    @GetMapping(path = "${kuma.boot.mcp.sse-endpoint:/mcp/sse}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(0L); // 不超时
        sessions.add(emitter);
        emitter.onCompletion(() -> sessions.remove(emitter));
        emitter.onTimeout(() -> sessions.remove(emitter));
        emitter.onError(e -> sessions.remove(emitter));
        try {
            emitter.send(SseEmitter.event().name("endpoint").data(messageEndpoint));
        } catch (IOException e) {
            sessions.remove(emitter);
            emitter.completeWithError(e);
        }
        return emitter;
    }

    /** 向所有 SSE 会话广播一条事件(供 list_changed 等服务端通知扩展使用) */
    public void broadcast(String event, Object data) {
        for (SseEmitter emitter : sessions) {
            try {
                emitter.send(SseEmitter.event().name(event).data(data));
            } catch (IOException e) {
                log.debug("MCP SSE broadcast failed, dropping session: {}", e.getMessage());
                sessions.remove(emitter);
            }
        }
    }
}
