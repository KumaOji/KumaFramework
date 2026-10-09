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
public class LabClient {
    private final URI base;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3))
            .followRedirects(HttpClient.Redirect.NEVER).build();

    public LabClient(ConsoleProperties properties) {
        String url = properties.projects().stream().filter(p -> p.name().equalsIgnoreCase("Lab"))
                .findFirst().map(ConsoleProperties.Project::url).orElse(null);
        if(url==null) { base=null;return; }
        base = URI.create(url.replaceAll("/+$", ""));
        if (!Set.of("http", "https").contains(base.getScheme()) || base.getHost() == null)
            throw new IllegalArgumentException("Lab 地址必须是 HTTP(S) 地址");
    }

    public record Request(String method, String path, String body, String authorization) {}

    public Map<String, Object> execute(Request request) {
        if (request == null || request.method() == null || !Set.of("GET", "POST", "PUT", "DELETE", "PATCH")
                .contains(request.method())) throw new IllegalArgumentException("不支持的请求方法");
        URI uri = target(request.path());
        long start = System.nanoTime();
        try {
            // Trace storage is asynchronous; only these explicit experiments need a longer wait.
            String path = request.path().split("\\?", 2)[0];
            Duration timeout = Duration.ofSeconds(Set.of("/lab/monitoring/otel", "/lab/monitoring/skywalking")
                    .contains(path) ? 120 : 30);
            var builder = HttpRequest.newBuilder(uri).timeout(timeout)
                    .header("Accept", "application/json").header("Content-Type", "application/json");
            if (request.authorization() != null && !request.authorization().isBlank())
                builder.header("Authorization", request.authorization());
            builder.method(request.method(), request.body() == null || request.body().isBlank()
                    ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(request.body()));
            var response = client.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream());
            try (var body = response.body()) {
                byte[] bytes = body.readNBytes(1024 * 1024 + 1);
                boolean truncated = bytes.length > 1024 * 1024;
                String text = new String(bytes, 0, Math.min(bytes.length, 1024 * 1024), StandardCharsets.UTF_8);
                return Map.of("status", response.statusCode(), "body", text, "truncated", truncated,
                        "duration", (System.nanoTime() - start) / 1_000_000, "url", uri.toString());
            }
        } catch (Exception e) {
            return Map.of("status", 0, "body", "Lab 请求失败：" + e.getClass().getSimpleName()
                    + "，请检查 Lab 是否启动和配置地址。", "duration", (System.nanoTime() - start) / 1_000_000,
                    "url", uri.toString(), "truncated", false);
        }
    }

    URI target(String path) {
        if(base==null)throw new IllegalArgumentException("未配置 Lab 服务，请在 console.projects 中添加名称为 Lab 的地址");
        if (path == null || path.length() > 2048 || !(path.startsWith("/lab/") || path.equals("/v3/api-docs")
                || path.startsWith("/v3/api-docs/"))) throw new IllegalArgumentException("仅允许 Lab 实验与 OpenAPI 路径");
        URI relative = URI.create(path);
        if (relative.getHost() != null || relative.getFragment() != null || path.contains("\\")
                || relative.getRawPath().contains("%"))
            throw new IllegalArgumentException("路径不支持编码或外部地址");
        URI result = URI.create(base + path).normalize();
        String prefix = base.getPath() + (path.startsWith("/lab/") ? "/lab/" : "/v3/api-docs");
        if (!result.getPath().startsWith(prefix)) throw new IllegalArgumentException("路径超出 Lab 范围");
        return result;
    }
}
