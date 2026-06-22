package com.kuma.boot.mcp.server;

/**
 * MCP 处理异常，携带 JSON-RPC 错误码，由 {@link McpServer} 转换为标准错误响应。
 */
public class McpException extends RuntimeException {

    private final int code;

    public McpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int code() {
        return code;
    }
}
