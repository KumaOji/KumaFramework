package com.kuma.cloud.console;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Service
public class ProjectMonitor {
    private final ConsoleProperties properties;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2))
            .followRedirects(HttpClient.Redirect.NEVER).build();
    private volatile Map<String, Object> snapshot = Map.of("ready", false);

    public ProjectMonitor(ConsoleProperties properties) { this.properties = properties; }
    public Map<String, Object> snapshot() { return snapshot; }

    @Scheduled(fixedDelay = 10000)
    public void sample() {
        List<Map<String, Object>> results = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = properties.projects().stream().map(project -> executor.submit(() -> probe(project))).toList();
            for (var future : futures) results.add(future.get());
        } catch (Exception e) {
            snapshot = Map.of("ready", true, "projects", results, "error", e.getMessage()); return;
        }
        snapshot = Map.of("ready", true, "sampledAt", Instant.now().toString(), "projects", results);
    }

    private Map<String, Object> probe(ConsoleProperties.Project project) {
        String base = project.url().replaceAll("/+$", "");
        long start = System.nanoTime();
        try {
            var response = client.send(HttpRequest.newBuilder(URI.create(base + "/actuator/health"))
                    .timeout(Duration.ofSeconds(3)).GET().build(), HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();
            String status = code == 200 && response.body().contains("\"UP\"") ? "HEALTHY"
                    : code == 503 && response.body().contains("\"DOWN\"") ? "UNHEALTHY" : "REACHABLE";
            return Map.of("name", project.name(), "url", base, "status", status, "httpStatus", code,
                    "latency", (System.nanoTime() - start) / 1_000_000, "message",
                    status.equals("REACHABLE") ? "HTTP 可达，健康接口未确认 UP" : "Actuator 健康检查");
        } catch (Exception e) {
            return Map.of("name", project.name(), "url", base, "status", "OFFLINE", "httpStatus", 0,
                    "latency", (System.nanoTime() - start) / 1_000_000, "message", "无法连接：" + e.getClass().getSimpleName());
        }
    }
}
