package com.kuma.cloud.lab.observability;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

class MonitoringMiddlewareLabsTest {
    private HttpServer server;
    private String url;
    private final JsonMapper json = JsonMapper.builder().build();
    private volatile JsonNode written;
    private volatile boolean fail;

    @BeforeEach void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", this::serve);
        server.start();
        url = "http://127.0.0.1:" + server.getAddress().getPort();
    }
    @AfterEach void stop() { server.stop(0); }

    @Test void lokiUsesNanosecondStringsAndDoesNotTurnRunIdsIntoLabels() throws Exception {
        try (var labs = labs(null)) {
            var report = labs.run("loki");
            var stream = written.path("streams").get(0);
            assertFalse(stream.path("stream").has("runId"));
            assertEquals("kuma-lab-monitoring", stream.path("stream").path("service_name").asString());
            assertTrue(Long.parseLong(stream.path("values").get(0).get(0).asString()) > 1_000_000_000_000_000L);
            assertTrue(stream.path("values").get(0).get(1).asString().contains(report.runId()));
            assertTrue(report.checks().values().stream().allMatch(Boolean::booleanValue));
        }
    }

    @Test void prometheusPreservesRealDownTargetsWhileDistinguishingQueryTypes() throws Exception {
        try (var labs = labs(null)) {
            var report = labs.run("prometheus");
            assertTrue(report.checks().get("rangeMatrix"));
            assertTrue(json.writeValueAsString(report.evidence()).contains("offline-service"));
        }
    }

    @Test void alertmanagerUsesAnIndependentShortLivedAlert() throws Exception {
        try (var labs = labs(null)) {
            var report = labs.run("alertmanager");
            var alert = written.get(0);
            assertEquals(report.runId(), alert.path("labels").path("run_id").asString());
            assertEquals("KumaLabAlert", alert.path("labels").path("alertname").asString());
            assertEquals(60, Duration.between(Instant.parse(alert.path("startsAt").asString()),
                    Instant.parse(alert.path("endsAt").asString())).toSeconds());
        }
    }

    @Test void grafanaCanDemonstrateLoginBoundaryOrAuthenticatedDataSourcesWithoutLeakingCredentials() throws Exception {
        try (var labs = labs(null)) {
            assertTrue(labs.run("grafana").checks().get("dashboardRequiresLogin"));
        }
        try (var labs = labs("fixture-secret")) {
            var report = labs.run("grafana");
            assertTrue(report.checks().get("skywalking-zipkinHealthy"));
            assertFalse(json.writeValueAsString(report).contains("fixture-secret"));
            assertFalse(json.writeValueAsString(report).contains("Authorization"));
        }
    }

    @Test void backendErrorsAndUnknownExperimentsAreNotReportedAsSuccessful() {
        try (var labs = labs(null)) {
            assertThrows(IllegalArgumentException.class, () -> labs.run("unknown"));
            fail = true;
            assertThrows(IllegalStateException.class, () -> labs.run("prometheus"));
        }
    }

    private MonitoringMiddlewareLabs labs(String password) {
        return new MonitoringMiddlewareLabs(new MonitoringMiddlewareLabs.Endpoints(url, url, url, url,
                url + "/v1/traces", url, url), "admin", password);
    }
    private void serve(HttpExchange exchange) throws IOException {
        if (fail) { respond(exchange, 503, "private backend details"); return; }
        String path = exchange.getRequestURI().getPath();
        if (exchange.getRequestMethod().equals("POST")) {
            written = json.readTree(exchange.getRequestBody().readAllBytes());
            respond(exchange, path.endsWith("/push") ? 204 : 200, "");
        } else if (path.endsWith("/query_range") && path.startsWith("/loki")) {
            respond(exchange, 200, "{\"data\":{\"result\":" + written.path("streams") + "}}");
        } else if (path.equals("/api/v2/alerts")) {
            respond(exchange, 200, written.toString());
        } else if (path.startsWith("/api/v1/query")) {
            String type = path.endsWith("query_range") ? "matrix" : "vector";
            respond(exchange, 200, "{\"status\":\"success\",\"data\":{\"resultType\":\"" + type +
                    "\",\"result\":[{\"metric\":{\"job\":\"offline-service\"},\"value\":[1,\"0\"]}]}}");
        } else if (path.equals("/api/health")) {
            respond(exchange, 200, "{\"database\":\"ok\"}");
        } else if (!exchange.getRequestHeaders().containsKey("Authorization")) {
            respond(exchange, 401, "{}");
        } else if (path.contains("dashboards")) {
            respond(exchange, 200, "{\"dashboard\":{\"title\":\"Kuma Services\",\"panels\":[{}]}}");
        } else {
            respond(exchange, 200, "{\"status\":\"OK\"}");
        }
    }
    private static void respond(HttpExchange exchange, int status, String text) throws IOException {
        byte[] body = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, body.length == 0 ? -1 : body.length);
        try (var output = exchange.getResponseBody()) { if (body.length != 0) output.write(body); }
    }
}
