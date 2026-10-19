package com.kuma.cloud.lab.elasticsearch;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElasticsearchLearningDemoTest {
    @Test void rejectsEndpointsWithCredentialsOrPaths() {
        for (String endpoint : List.of("file:///tmp/es", "http://user:secret@localhost:9200", "http://localhost:9200/business", "http://localhost:9200?x=1")) {
            assertThatThrownBy(() -> new ElasticsearchLearningDemo(endpoint)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test void rejectsUnknownLessonBeforeSendingRequests() {
        try (var lab = new ElasticsearchLearningDemo("http://127.0.0.1:1")) {
            assertThatThrownBy(() -> lab.run("production-index")).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test void cleansOnlyItsGeneratedIndexWhenMappingRequestFails() throws Exception {
        verifyCleanup(false, false);
    }

    @Test void cleansIndexWhenAnEvidenceCheckFails() throws Exception {
        verifyCleanup(true, false);
    }

    @Test void keepsOriginalFailureWhenCleanupAlsoFails() throws Exception {
        verifyCleanup(false, true);
    }

    private void verifyCleanup(boolean invalidMapping, boolean cleanupFails) throws Exception {
        List<String> requests = new ArrayList<>();
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            requests.add(method + " " + path);
            int status = 200;
            String body = "{\"acknowledged\":true}";
            if (method.equals("GET")) {
                status = invalidMapping ? 200 : 500;
                body = invalidMapping ? "{}" : "{\"error\":\"mapping unavailable\"}";
            } else if (method.equals("DELETE") && cleanupFails) {
                status = 503;
                body = "{\"error\":\"cleanup unavailable\"}";
            }
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        try (var lab = new ElasticsearchLearningDemo("http://127.0.0.1:" + server.getAddress().getPort())) {
            assertThatThrownBy(() -> lab.run("all")).satisfies(error -> {
                assertThat(error.getMessage()).contains(invalidMapping ? "mappingVerified" : "HTTP 500");
                assertThat(error.getSuppressed()).hasSize(cleanupFails ? 1 : 0);
            });
            assertThat(requests).hasSize(3);
            String indexPath = requests.getFirst().substring(4);
            assertThat(indexPath).matches("/kuma_lab_es_[a-f0-9]{32}");
            assertThat(requests).containsExactly("PUT " + indexPath, "GET " + indexPath + "/_mapping", "DELETE " + indexPath);
        } finally { server.stop(0); }
    }
}
