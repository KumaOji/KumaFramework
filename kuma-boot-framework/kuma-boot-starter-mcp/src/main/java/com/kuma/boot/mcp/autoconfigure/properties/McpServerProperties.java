package com.kuma.boot.mcp.autoconfigure.properties;

import com.kuma.boot.mcp.protocol.McpSchema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MCP 服务端配置（前缀 {@code kuma.boot.mcp}）。
 */
@Data
@ConfigurationProperties(prefix = McpServerProperties.PREFIX)
public class McpServerProperties {

    public static final String PREFIX = "kuma.boot.mcp";

    /** 是否启用 MCP 服务端 */
    private boolean enabled = true;

    /** 服务端名称（initialize 握手返回） */
    private String name = "kuma-mcp-server";

    /** 服务端版本 */
    private String version = "1.0.0";

    /** 协议版本 */
    private String protocolVersion = McpSchema.PROTOCOL_VERSION;

    /** 面向客户端/模型的使用说明（可选，握手时返回） */
    private String instructions;

    /** JSON-RPC 消息端点（Streamable HTTP，POST） */
    private String endpoint = "/mcp";

    /** SSE 通知通道端点（GET） */
    private String sseEndpoint = "/mcp/sse";

    /** 是否启用 HTTP 传输层（需 Web 环境） */
    private boolean httpEnabled = true;
}
