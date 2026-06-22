package com.kuma.boot.mcp.resource;

import com.kuma.boot.mcp.protocol.McpSchema;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 资源注册表：收集容器内全部 {@link McpResource}，提供列举与按 URI 查找。
 */
public class McpResourceRegistry {

    private final Map<String, McpResource> resources = new LinkedHashMap<>();

    public McpResourceRegistry(List<McpResource> resources) {
        for (McpResource resource : resources) {
            this.resources.put(resource.uri(), resource);
        }
    }

    public List<McpSchema.Resource> list() {
        return resources.values().stream().map(McpResource::descriptor).toList();
    }

    public Optional<McpResource> find(String uri) {
        return Optional.ofNullable(resources.get(uri));
    }

    public int size() {
        return resources.size();
    }
}
