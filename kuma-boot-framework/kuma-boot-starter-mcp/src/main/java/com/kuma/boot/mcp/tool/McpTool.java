package com.kuma.boot.mcp.tool;

import com.kuma.boot.mcp.protocol.McpSchema;

import java.util.Map;

/**
 * MCP 工具 SPI。业务侧实现本接口并注册为 Spring Bean，即可被 MCP 客户端发现与调用。
 *
 * <pre>{@code
 * @Component
 * public class WeatherTool implements McpTool {
 *     public String name() { return "get_weather"; }
 *     public String description() { return "查询指定城市天气"; }
 *     public Map<String,Object> inputSchema() {
 *         return Map.of("type","object",
 *                 "properties", Map.of("city", Map.of("type","string")),
 *                 "required", List.of("city"));
 *     }
 *     public McpSchema.CallToolResult call(Map<String,Object> args) {
 *         return McpSchema.CallToolResult.text("晴 26℃");
 *     }
 * }
 * }</pre>
 */
public interface McpTool {

    /** 工具唯一名称（客户端按名调用） */
    String name();

    /** 工具用途说明，供模型理解何时调用 */
    default String description() {
        return "";
    }

    /** 入参的 JSON Schema，默认空对象（无参数） */
    default Map<String, Object> inputSchema() {
        return Map.of("type", "object");
    }

    /** 执行工具调用 */
    McpSchema.CallToolResult call(Map<String, Object> arguments);

    /** 生成对外暴露的工具描述 */
    default McpSchema.Tool descriptor() {
        return new McpSchema.Tool(name(), description(), inputSchema());
    }
}
