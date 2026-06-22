package com.kuma.boot.mcp.server;

import com.kuma.boot.mcp.prompt.McpPrompt;
import com.kuma.boot.mcp.prompt.McpPromptRegistry;
import com.kuma.boot.mcp.protocol.JsonRpc;
import com.kuma.boot.mcp.protocol.McpSchema;
import com.kuma.boot.mcp.resource.McpResource;
import com.kuma.boot.mcp.resource.McpResourceRegistry;
import com.kuma.boot.mcp.tool.McpTool;
import com.kuma.boot.mcp.tool.McpToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * MCP 服务端核心:与传输层无关的 JSON-RPC 方法分发器。
 *
 * <p>接收一条 {@link JsonRpc.Request},根据 {@code method} 路由到对应能力(握手 / tools /
 * resources / prompts),返回 {@link JsonRpc.Response};通知类方法(以 {@code notifications/}
 * 开头或无 id)返回 {@code null} 表示无需响应。HTTP / SSE / stdio 等任意传输均可复用本分发器。
 */
public class McpServer {

    private static final Logger log = LoggerFactory.getLogger(McpServer.class);

    private final McpSchema.Implementation serverInfo;
    private final String protocolVersion;
    private final String instructions;
    private final McpToolRegistry toolRegistry;
    private final McpResourceRegistry resourceRegistry;
    private final McpPromptRegistry promptRegistry;

    public McpServer(McpSchema.Implementation serverInfo, String protocolVersion, String instructions,
                     McpToolRegistry toolRegistry, McpResourceRegistry resourceRegistry,
                     McpPromptRegistry promptRegistry) {
        this.serverInfo = serverInfo;
        this.protocolVersion = protocolVersion;
        this.instructions = (instructions == null || instructions.isBlank()) ? null : instructions;
        this.toolRegistry = toolRegistry;
        this.resourceRegistry = resourceRegistry;
        this.promptRegistry = promptRegistry;
    }

    /**
     * 处理一条 JSON-RPC 请求。
     *
     * @return 响应;通知类方法返回 {@code null}
     */
    public JsonRpc.Response handle(JsonRpc.Request request) {
        Object id = request == null ? null : request.id();
        try {
            if (request == null || request.method() == null) {
                return JsonRpc.Response.error(id, JsonRpc.INVALID_REQUEST, "Invalid request");
            }
            String method = request.method();
            Map<String, Object> params = request.params() == null ? Map.of() : request.params();

            // 通知:无 id 或 notifications/* 前缀,均不返回响应
            if (id == null || method.startsWith(McpSchema.NOTIFICATION_PREFIX)) {
                log.debug("MCP notification received: {}", method);
                return null;
            }

            return switch (method) {
                case McpSchema.METHOD_INITIALIZE -> JsonRpc.Response.ok(id, initialize());
                case McpSchema.METHOD_PING -> JsonRpc.Response.ok(id, Map.of());
                case McpSchema.METHOD_TOOLS_LIST ->
                        JsonRpc.Response.ok(id, new McpSchema.ListToolsResult(toolRegistry.list(), null));
                case McpSchema.METHOD_TOOLS_CALL -> JsonRpc.Response.ok(id, callTool(params));
                case McpSchema.METHOD_RESOURCES_LIST ->
                        JsonRpc.Response.ok(id, new McpSchema.ListResourcesResult(resourceRegistry.list(), null));
                case McpSchema.METHOD_RESOURCES_READ -> JsonRpc.Response.ok(id, readResource(params));
                case McpSchema.METHOD_PROMPTS_LIST ->
                        JsonRpc.Response.ok(id, new McpSchema.ListPromptsResult(promptRegistry.list(), null));
                case McpSchema.METHOD_PROMPTS_GET -> JsonRpc.Response.ok(id, getPrompt(params));
                default -> JsonRpc.Response.error(id, JsonRpc.METHOD_NOT_FOUND, "Method not found: " + method);
            };
        } catch (McpException e) {
            return JsonRpc.Response.error(id, e.code(), e.getMessage());
        } catch (Exception e) {
            log.warn("MCP handle error on method '{}': {}",
                    request == null ? null : request.method(), e.getMessage(), e);
            return JsonRpc.Response.error(id, JsonRpc.INTERNAL_ERROR, e.getMessage());
        }
    }

    // ── 各方法实现 ───────────────────────────────────────────────────────────

    private McpSchema.InitializeResult initialize() {
        McpSchema.ServerCapabilities capabilities = new McpSchema.ServerCapabilities(
                new McpSchema.Capability(false),
                new McpSchema.Capability(false),
                new McpSchema.Capability(false));
        return new McpSchema.InitializeResult(protocolVersion, capabilities, serverInfo, instructions);
    }

    @SuppressWarnings("unchecked")
    private McpSchema.CallToolResult callTool(Map<String, Object> params) {
        String name = str(params.get("name"));
        if (name == null) {
            throw new McpException(JsonRpc.INVALID_PARAMS, "Missing tool name");
        }
        McpTool tool = toolRegistry.find(name)
                .orElseThrow(() -> new McpException(JsonRpc.INVALID_PARAMS, "Unknown tool: " + name));
        Map<String, Object> arguments = params.get("arguments") instanceof Map<?, ?> m
                ? (Map<String, Object>) m : Map.of();
        return tool.call(arguments);
    }

    private McpSchema.ReadResourceResult readResource(Map<String, Object> params) {
        String uri = str(params.get("uri"));
        if (uri == null) {
            throw new McpException(JsonRpc.INVALID_PARAMS, "Missing resource uri");
        }
        McpResource resource = resourceRegistry.find(uri)
                .orElseThrow(() -> new McpException(JsonRpc.INVALID_PARAMS, "Unknown resource: " + uri));
        return resource.read(uri);
    }

    @SuppressWarnings("unchecked")
    private McpSchema.GetPromptResult getPrompt(Map<String, Object> params) {
        String name = str(params.get("name"));
        if (name == null) {
            throw new McpException(JsonRpc.INVALID_PARAMS, "Missing prompt name");
        }
        McpPrompt prompt = promptRegistry.find(name)
                .orElseThrow(() -> new McpException(JsonRpc.INVALID_PARAMS, "Unknown prompt: " + name));
        Map<String, Object> arguments = params.get("arguments") instanceof Map<?, ?> m
                ? (Map<String, Object>) m : Map.of();
        return prompt.get(arguments);
    }

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }
}
