package com.kuma.boot.mcp.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * MCP（Model Context Protocol）领域报文定义。
 *
 * <p>集中存放协议中的方法名常量与各类结果对象，作为传输层与业务 SPI 之间的公共契约。
 * 仅覆盖最常用的 tools / resources / prompts 能力，足以构成可用的服务端骨架。
 *
 * @see <a href="https://modelcontextprotocol.io/specification">MCP Specification</a>
 */
public final class McpSchema {

    private McpSchema() {
    }

    /** 默认协议版本（与主流客户端兼容） */
    public static final String PROTOCOL_VERSION = "2024-11-05";

    // ── 方法名 ───────────────────────────────────────────────────────────────
    public static final String METHOD_INITIALIZE = "initialize";
    public static final String METHOD_PING = "ping";
    public static final String METHOD_TOOLS_LIST = "tools/list";
    public static final String METHOD_TOOLS_CALL = "tools/call";
    public static final String METHOD_RESOURCES_LIST = "resources/list";
    public static final String METHOD_RESOURCES_READ = "resources/read";
    public static final String METHOD_PROMPTS_LIST = "prompts/list";
    public static final String METHOD_PROMPTS_GET = "prompts/get";
    /** 通知方法前缀（无需响应） */
    public static final String NOTIFICATION_PREFIX = "notifications/";

    // ── 握手 ────────────────────────────────────────────────────────────────

    /** 服务端 / 客户端实现信息 */
    public record Implementation(String name, String version) {
    }

    /** 单项能力声明 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Capability(Boolean listChanged) {
    }

    /** 服务端能力集合 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ServerCapabilities(Capability tools, Capability resources, Capability prompts) {
    }

    /** {@code initialize} 响应 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record InitializeResult(String protocolVersion, ServerCapabilities capabilities,
                                   Implementation serverInfo, String instructions) {
    }

    // ── 通用内容 ─────────────────────────────────────────────────────────────

    /** 内容块（骨架仅支持 text 类型） */
    public record Content(String type, String text) {
        public static Content text(String text) {
            return new Content("text", text);
        }
    }

    // ── tools ────────────────────────────────────────────────────────────────

    /** 工具描述（name + 说明 + JSON Schema 入参） */
    public record Tool(String name, String description, Map<String, Object> inputSchema) {
    }

    /** 工具调用结果 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CallToolResult(List<Content> content, Boolean isError) {
        public static CallToolResult text(String text) {
            return new CallToolResult(List.of(Content.text(text)), false);
        }

        public static CallToolResult error(String text) {
            return new CallToolResult(List.of(Content.text(text)), true);
        }
    }

    public record ListToolsResult(List<Tool> tools, String nextCursor) {
    }

    // ── resources ──────────────────────────────────────────────────────────

    public record Resource(String uri, String name, String description, String mimeType) {
    }

    public record ResourceContents(String uri, String mimeType, String text) {
    }

    public record ReadResourceResult(List<ResourceContents> contents) {
    }

    public record ListResourcesResult(List<Resource> resources, String nextCursor) {
    }

    // ── prompts ──────────────────────────────────────────────────────────────

    public record PromptArgument(String name, String description, boolean required) {
    }

    public record Prompt(String name, String description, List<PromptArgument> arguments) {
    }

    public record PromptMessage(String role, Content content) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GetPromptResult(String description, List<PromptMessage> messages) {
    }

    public record ListPromptsResult(List<Prompt> prompts, String nextCursor) {
    }
}
