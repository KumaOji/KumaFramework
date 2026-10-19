package com.kuma.cloud.console;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@Service
public class LocalComponents {
    private final LocalJavaService uaa;
    private final LocalJavaService gateway;

    public LocalComponents(@Value("${console.workspace:}") String workspace,
                           @Value("${console.components.distribution:Ubuntu-24.04}") String distribution,
                           CommandRunner commands, ObjectMapper mapper) {
        Path root = workspace.isBlank() ? LabLifecycle.findWorkspace(Path.of(System.getProperty("user.dir"))) : Path.of(workspace).toAbsolutePath().normalize();
        uaa = new LocalJavaService(root.toString(), distribution, commands, mapper, "uaa");
        gateway = new LocalJavaService(root.toString(), distribution, commands, mapper, "gateway");
    }

    public Map<String, Object> status() { return Map.of("uaa", uaa.status(), "gateway", gateway.status()); }
    public Object action(String name, String action) {
        if (!java.util.Set.of("uaa", "gateway").contains(name) || !java.util.Set.of("start", "stop").contains(action)) throw new IllegalArgumentException("不支持的组件或操作");
        return name.equals("uaa") ? action.equals("start") ? uaa.start() : uaa.stop() : action.equals("start") ? gateway.start() : gateway.stop();
    }
    public Object logs(String name) throws IOException {
        return switch (name) { case "uaa" -> uaa.logs(); case "gateway" -> gateway.logs(); default -> throw new IllegalArgumentException("不支持的组件"); };
    }
    @PreDestroy public void shutdown() { uaa.shutdown(); gateway.shutdown(); }
}
