package com.kuma.boot.mcp.tool;

import com.kuma.boot.mcp.protocol.McpSchema;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 工具注册表：收集容器内全部 {@link McpTool}，提供列举与按名查找。
 */
public class McpToolRegistry {

    private final Map<String, McpTool> tools = new LinkedHashMap<>();

    public McpToolRegistry(List<McpTool> tools) {
        for (McpTool tool : tools) {
            this.tools.put(tool.name(), tool);
        }
    }

    /** 全部工具描述 */
    public List<McpSchema.Tool> list() {
        return tools.values().stream().map(McpTool::descriptor).toList();
    }

    public Optional<McpTool> find(String name) {
        return Optional.ofNullable(tools.get(name));
    }

    public int size() {
        return tools.size();
    }
}
