package com.kuma.boot.mcp.prompt;

import com.kuma.boot.mcp.protocol.McpSchema;

import java.util.List;
import java.util.Map;

/**
 * MCP 提示词 SPI。业务侧实现并注册为 Spring Bean，向客户端暴露可复用的提示词模板。
 */
public interface McpPrompt {

    /** 提示词唯一名称 */
    String name();

    default String description() {
        return "";
    }

    /** 模板参数定义 */
    default List<McpSchema.PromptArgument> arguments() {
        return List.of();
    }

    /** 按参数渲染提示词消息 */
    McpSchema.GetPromptResult get(Map<String, Object> arguments);

    /** 生成对外暴露的提示词描述 */
    default McpSchema.Prompt descriptor() {
        return new McpSchema.Prompt(name(), description(), arguments());
    }
}
