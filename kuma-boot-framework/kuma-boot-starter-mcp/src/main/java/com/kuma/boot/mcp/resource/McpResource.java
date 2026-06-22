package com.kuma.boot.mcp.resource;

import com.kuma.boot.mcp.protocol.McpSchema;

/**
 * MCP 资源 SPI。业务侧实现并注册为 Spring Bean，向客户端暴露可读取的上下文资源
 * （文件、配置、数据库视图等）。
 */
public interface McpResource {

    /** 资源 URI（唯一标识，如 {@code file:///docs/readme.md}） */
    String uri();

    /** 资源名称 */
    default String name() {
        return uri();
    }

    default String description() {
        return "";
    }

    default String mimeType() {
        return "text/plain";
    }

    /** 读取资源内容 */
    McpSchema.ReadResourceResult read(String uri);

    /** 生成对外暴露的资源描述 */
    default McpSchema.Resource descriptor() {
        return new McpSchema.Resource(uri(), name(), description(), mimeType());
    }
}
