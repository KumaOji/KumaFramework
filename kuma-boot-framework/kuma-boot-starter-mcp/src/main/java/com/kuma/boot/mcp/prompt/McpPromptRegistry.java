package com.kuma.boot.mcp.prompt;

import com.kuma.boot.mcp.protocol.McpSchema;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 提示词注册表：收集容器内全部 {@link McpPrompt}，提供列举与按名查找。
 */
public class McpPromptRegistry {

    private final Map<String, McpPrompt> prompts = new LinkedHashMap<>();

    public McpPromptRegistry(List<McpPrompt> prompts) {
        for (McpPrompt prompt : prompts) {
            this.prompts.put(prompt.name(), prompt);
        }
    }

    public List<McpSchema.Prompt> list() {
        return prompts.values().stream().map(McpPrompt::descriptor).toList();
    }

    public Optional<McpPrompt> find(String name) {
        return Optional.ofNullable(prompts.get(name));
    }

    public int size() {
        return prompts.size();
    }
}
