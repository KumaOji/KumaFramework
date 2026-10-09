package com.kuma.cloud.lab.observability;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Real middleware experiments. Each run writes only its own log, trace or short-lived test alert. */
public final class MonitoringMiddlewareLabs implements AutoCloseable {
    public record UiLink(String name, String url, String location) {}
    public record Report(String component, String runId, Map<String, Object> evidence,
                         Map<String, Boolean> checks, List<UiLink> uiLinks) {}
    public record Endpoints(String loki, String prometheus, String alertmanager, String grafana,
                            String otlp, String skywalking, String skywalkingUi) {
        public static Endpoints local() {
            return new Endpoints(setting("LOKI_URL", "http://127.0.0.1:3100"),
                    setting("PROMETHEUS_URL", "http://127.0.0.1:9090"),
                    setting("ALERTMANAGER_URL", "http://127.0.0.1:9093"),
                    setting("GRAFANA_URL", "http://127.0.0.1:3000"),
                    setting("OTLP_URL", "http://127.0.0.1:4318/v1/traces"),
                    setting("SKYWALKING_QUERY_URL", "http://127.0.0.1:9412"),
                    setting("SKYWALKING_UI_URL", "http://127.0.0.1:12880"));
        }
    }

    private final Endpoints endpoints;
    private final String grafanaAuth;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    private final JsonMapper json = JsonMapper.builder().build();

    public MonitoringMiddlewareLabs(Endpoints endpoints, String grafanaUser, String grafanaPassword) {
        this.endpoints = endpoints;
        this.grafanaAuth = grafanaPassword == null || grafanaPassword.isBlank() ? null : "Basic " +
                Base64.getEncoder().encodeToString((grafanaUser + ":" + grafanaPassword).getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) throws Exception {
        String selected = args.length == 0 ? "all" : args[0];
        try (var labs = new MonitoringMiddlewareLabs(Endpoints.local(), setting("GRAFANA_USER", "admin"),
                setting("GRAFANA_PASSWORD", ""))) {
            for (String component : selected.equals("all")
                    ? List.of("loki", "prometheus", "alertmanager", "otel", "skywalking", "grafana") : List.of(selected)) {
                Report report = labs.run(component);
                System.out.println(jsonReport(report));
            }
        }
    }

    public Report run(String component) throws Exception {
        return switch (component) {
            case "loki" -> loki();
            case "prometheus" -> prometheus();
            case "alertmanager" -> alertmanager();
            case "otel", "skywalking" -> trace(component);
            case "grafana" -> grafana();
            default -> throw new IllegalArgumentException("Choose loki, prometheus, alertmanager, otel, skywalking or grafana");
        };
    }

    public List<UiLink> uiLinks() {
        String grafanaUi = setting("GRAFANA_UI_URL", endpoints.grafana());
        return List.of(new UiLink("Grafana", grafanaUi, "Dashboards → Kuma → Kuma Services"),
                new UiLink("Loki 日志", grafanaUi + "/explore", "Explore → Loki；Loki 自身是日志 API"),
                new UiLink("Prometheus", setting("PROMETHEUS_UI_URL", endpoints.prometheus()), "查询页输入 up；Targets 查看采集状态"),
                new UiLink("Alertmanager", setting("ALERTMANAGER_UI_URL", endpoints.alertmanager()), "Alerts 查看告警；Silences 查看静默"),
                new UiLink("SkyWalking", endpoints.skywalkingUi(), "Zipkin Trace / Lens 按 traceId 查询；原生 Agent 使用 Trace 页面"));
    }

    private Report loki() throws Exception {
        String id = id();
        Instant now = Instant.now();
        String timestamp = Long.toString(now.getEpochSecond() * 1_000_000_000L + now.getNano());
        String line = json.writeValueAsString(Map.of("runId", id, "message", "Loki lab log", "level", "INFO"));
        var pushed = request("POST", endpoints.loki() + "/loki/api/v1/push", Map.of("streams", List.of(Map.of(
                "stream", Map.of("service_name", "kuma-lab-monitoring", "environment", "wsl"),
                "values", List.of(List.of(timestamp, line))))), false);
        requireStatus(pushed, 204);
        String query = "{service_name=\"kuma-lab-monitoring\"} |= \"" + id + "\"";
        JsonNode found = pollJson(endpoints.loki() + "/loki/api/v1/query_range?query=" + encode(query)
                + "&since=5m&limit=20", node -> node.path("data").path("result").toString().contains(id));
        return report("loki", id, Map.of("logql", query, "pushStatus", pushed.statusCode(),
                "streams", found.path("data").path("result")), Map.of("written", true, "queriedBack", true));
    }

    private Report prometheus() throws Exception {
        JsonNode instant = getJson(endpoints.prometheus() + "/api/v1/query?query=up", false);
        long end = Instant.now().getEpochSecond();
        JsonNode range = getJson(endpoints.prometheus() + "/api/v1/query_range?query=up&start="
                + (end - 60) + "&end=" + end + "&step=15", false);
        var checks = Map.of("instantVector", instant.path("data").path("resultType").asString().equals("vector"),
                "rangeMatrix", range.path("data").path("resultType").asString().equals("matrix"),
                "hasTargets", !instant.path("data").path("result").isEmpty());
        return report("prometheus", id(), Map.of("promql", "up", "instant", instant.path("data"),
                "range", range.path("data"), "interpretation", "up=1 表示采集成功，up=0 表示失联；请求速率需对 counter 使用 rate()"), checks);
    }

    private Report alertmanager() throws Exception {
        String id = id();
        Instant start = Instant.now();
        Instant end = start.plusSeconds(60);
        var sent = request("POST", endpoints.alertmanager() + "/api/v2/alerts", List.of(Map.of(
                "labels", Map.of("alertname", "KumaLabAlert", "severity", "info", "run_id", id),
                "annotations", Map.of("summary", "短时实验告警 " + id),
                "startsAt", start.toString(), "endsAt", end.toString())), false);
        requireStatus(sent, 200);
        JsonNode alerts = pollJson(endpoints.alertmanager() + "/api/v2/alerts?filter=" + encode("run_id=\"" + id + "\""),
                node -> node.isArray() && !node.isEmpty() && node.toString().contains(id));
        return report("alertmanager", id, Map.of("alerts", alerts, "expiresAt", end.toString(),
                "uiFilter", "alertname=KumaLabAlert", "boundary", "验证 API 接收与查回；不代表 Prometheus 已触发规则或外部通知已发送"),
                Map.of("accepted", true, "queriedBack", true, "boundedLifetime", true));
    }

    private Report trace(String component) throws Exception {
        // SDK acceptance alone is insufficient: verify all six correlated span IDs in persistent storage.
        OtelLearningDemo.Report trace = OtelLearningDemo.run(endpoints.otlp());
        OtelLearningDemo.verifyBackend(trace, endpoints.skywalking(), Duration.ofSeconds(90));
        JsonNode spans = getJson(endpoints.skywalking() + "/zipkin/api/v2/trace/" + trace.traceId(), false);
        var evidence = new LinkedHashMap<String, Object>();
        evidence.put("traceId", trace.traceId());
        evidence.put("traceparent", trace.traceparent());
        evidence.put("storedSpans", spans);
        evidence.put("sdkChecks", trace.checks());
        evidence.put("boundary", component.equals("otel")
                ? "API/SDK → OTLP HTTP → Collector receiver/processors/exporter → SkyWalking；现有管道只接收 traces"
                : "OTLP trace 在 SkyWalking 的 Zipkin/Lens 查询；与已有原生 Agent 实验分开观察");
        return report(component, trace.traceId(), evidence, Map.of("exportedAndStored", true,
                "sdkChecksPassed", trace.checks().values().stream().allMatch(Boolean::booleanValue)));
    }

    private Report grafana() throws Exception {
        JsonNode health = getJson(endpoints.grafana() + "/api/health", false);
        var checks = new LinkedHashMap<String, Boolean>();
        checks.put("databaseHealthy", health.path("database").asString().equals("ok"));
        var evidence = new LinkedHashMap<String, Object>();
        evidence.put("health", health);
        if (grafanaAuth == null) {
            var response = request("GET", endpoints.grafana() + "/api/dashboards/uid/kuma-services", null, false);
            checks.put("dashboardRequiresLogin", response.statusCode() == 401);
            evidence.put("dashboardStatus", response.statusCode());
            evidence.put("nextStep", "使用 WSL 启动脚本读取现有本地凭据，或设置 KUMA_LAB_MONITORING_GRAFANA_PASSWORD 后验证面板/数据源");
        } else {
            JsonNode dashboard = getJson(endpoints.grafana() + "/api/dashboards/uid/kuma-services", true);
            checks.put("dashboardHasPanels", !dashboard.path("dashboard").path("panels").isEmpty());
            evidence.put("dashboardTitle", dashboard.path("dashboard").path("title").asString());
            evidence.put("panelCount", dashboard.path("dashboard").path("panels").size());
            var sources = new LinkedHashMap<String, JsonNode>();
            for (String uid : List.of("prometheus", "loki", "skywalking-zipkin")) {
                JsonNode source = getJson(endpoints.grafana() + "/api/datasources/uid/" + uid + "/health", true);
                checks.put(uid + "Healthy", source.path("status").asString().equals("OK"));
                sources.put(uid, source);
            }
            evidence.put("datasources", sources);
        }
        return report("grafana", id(), evidence, checks);
    }

    private Report report(String component, String id, Map<String, Object> evidence, Map<String, Boolean> checks) {
        if (checks.values().stream().anyMatch(value -> !value)) {
            throw new IllegalStateException(component + " checks failed: " + checks);
        }
        return new Report(component, id, evidence, checks, uiLinks());
    }

    private JsonNode pollJson(String url, java.util.function.Predicate<JsonNode> check) throws Exception {
        long deadline = System.nanoTime() + Duration.ofSeconds(20).toNanos();
        do {
            JsonNode result = getJson(url, false);
            if (check.test(result)) return result;
            Thread.sleep(500);
        } while (System.nanoTime() < deadline);
        throw new IllegalStateException("Evidence not queryable within 20 seconds: " + url);
    }

    private JsonNode getJson(String url, boolean auth) throws Exception {
        var response = request("GET", url, null, auth);
        requireStatus(response, 200);
        return json.readTree(response.body());
    }

    private HttpResponse<String> request(String method, String url, Object body, boolean auth) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(3));
        if (auth) builder.header("Authorization", grafanaAuth);
        if (body == null) builder.GET();
        else builder.header("Content-Type", "application/json").method(method,
                HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)));
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static void requireStatus(HttpResponse<String> response, int expected) {
        if (response.statusCode() != expected) throw new IllegalStateException(
                response.uri().getPath() + ": expected HTTP " + expected + ", got " + response.statusCode());
    }

    private static String setting(String name, String fallback) {
        return System.getenv().getOrDefault("KUMA_LAB_MONITORING_" + name, fallback);
    }
    private static String encode(String text) { return URLEncoder.encode(text, StandardCharsets.UTF_8); }
    private static String id() { return UUID.randomUUID().toString().replace("-", ""); }
    private static String jsonReport(Report report) { return JsonMapper.builder().build().writerWithDefaultPrettyPrinter().writeValueAsString(report); }
    @Override public void close() { client.close(); }
}
