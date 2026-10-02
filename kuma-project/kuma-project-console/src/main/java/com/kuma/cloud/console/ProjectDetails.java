package com.kuma.cloud.console;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

@Service
public class ProjectDetails {
    private static final Set<String> ENDPOINTS = Set.of("health", "info", "metrics", "threaddump", "httpexchanges",
            "metrics/jvm.memory.used", "metrics/jvm.threads.live", "metrics/process.uptime", "metrics/system.cpu.usage");
    private final ConsoleProperties properties;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2))
            .followRedirects(HttpClient.Redirect.NEVER).build();

    public ProjectDetails(ConsoleProperties properties) { this.properties = properties; }

    public Object read(String name, String endpoint) {
        if (!ENDPOINTS.contains(endpoint)) throw new IllegalArgumentException("不支持的项目诊断端点");
        var project = properties.projects().stream().filter(p -> p.name().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("请选择已配置的项目"));
        URI target = URI.create(project.url().replaceAll("/+$", "") + "/actuator/" + endpoint);
        try {
            var response = client.send(HttpRequest.newBuilder(target).timeout(Duration.ofSeconds(8)).GET().build(),
                    HttpResponse.BodyHandlers.ofInputStream());
            try (var stream = response.body()) {
                byte[] body = stream.readNBytes(1024 * 1024 + 1);
                return Map.of("status", response.statusCode(), "url", target.toString(), "truncated", body.length > 1024 * 1024,
                        "body", new String(body, 0, Math.min(body.length, 1024 * 1024), StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            return Map.of("status", 0, "url", target.toString(), "truncated", false,
                    "body", "项目未连接：" + e.getClass().getSimpleName());
        }
    }
}
