package com.kuma.cloud.console;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpServletRequest;
import tools.jackson.databind.ObjectMapper;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;

class DeepSeekClientTest {
    @TempDir Path temporary;
    final ObjectMapper mapper = new ObjectMapper();
    DeepSeekClient.Chat chat() { return new DeepSeekClient.Chat("deepseek-chat", List.of(new DeepSeekClient.Message("user", "你好"))); }

    @Test void streamsBeforeCompletionAndPreservesUnicodeAndHistory() throws Exception {
        var received = new CompletableFuture<String>(); var firstEvent = new CountDownLatch(1);
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/chat", exchange -> {
            try {
                received.complete(exchange.getRequestHeaders().getFirst("Authorization") + "\n" + new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                exchange.getResponseHeaders().add("Content-Type", "text/event-stream");exchange.sendResponseHeaders(200, 0);
                var out = exchange.getResponseBody();
                out.write("data: {\"choices\":[{\"delta\":{\"reasoning_content\":\"思考\"}}]}\n\n".getBytes(StandardCharsets.UTF_8)); out.flush();
                if (!firstEvent.await(3, TimeUnit.SECONDS)) throw new AssertionError("Response was buffered");
                out.write("data: {\"choices\":[{\"delta\":{\"content\":\"你好🌸\"}}]}\n\ndata: [DONE]\n\n".getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) { throw new RuntimeException(e); } finally { exchange.close(); }
        });server.start();
        try {
            var properties = temporary.resolve("gradle.properties");Files.writeString(properties,"deepseek.api.key=test-secret\n");
            var client = new DeepSeekClient(mapper, "", temporary.resolve("key"), URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/chat"),properties);
            var events = new ArrayList<String>();
            client.stream(chat(), (event, data) -> { events.add(event + ":" + mapper.writeValueAsString(data)); firstEvent.countDown(); });
            assertThat(events).hasSize(3);assertThat(events.get(0)).contains("reasoning", "思考");
            assertThat(events.get(1)).contains("delta", "你好🌸");assertThat(events.getLast()).startsWith("done:");
            assertThat(received.get()).contains("Bearer test-secret", "\"stream\":true", "你好");
        } finally { server.stop(0); }
    }

    @Test void credentialsStayOutOfStatusAndEnvironmentTakesPrecedence() throws Exception {
        var file = temporary.resolve("config/key");var client = new DeepSeekClient(mapper, "", file, URI.create("http://localhost"));
        assertThat(client.status().get("configured")).isEqualTo(false);
        client.saveKey("sk-local-test-secret-123456");
        assertThat(Files.readString(file)).isEqualTo("sk-local-test-secret-123456");
        assertThat(mapper.writeValueAsString(client.status())).doesNotContain("sk-local", "123456");
        var env = new DeepSeekClient(mapper, "environment-secret", file, URI.create("http://localhost"));
        assertThat(env.status().get("source")).isEqualTo("environment");
        assertThatThrownBy(() -> env.saveKey("sk-local-test-secret-999999")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test void gradlePropertiesOverrideLocalFileAndReloadWithoutEmbeddingSecrets() throws Exception {
        var properties = temporary.resolve("gradle.properties");var local = temporary.resolve("local.key");
        Files.writeString(local,"sk-local-fallback-123456");
        Files.writeString(properties,"other.setting=unchanged\ndeepseek.api.key=sk-gradle-secret-123456\n");
        var client = new DeepSeekClient(mapper,"",local,URI.create("http://localhost"),properties);
        assertThat(client.status().get("configured")).isEqualTo(true);
        assertThat(client.status().get("source")).isEqualTo("gradle");
        assertThat(mapper.writeValueAsString(client.status())).doesNotContain("sk-", "123456");
        assertThatThrownBy(() -> client.saveKey("sk-replacement-123456789")).isInstanceOf(IllegalArgumentException.class);
        assertThat(Files.readString(properties)).contains("other.setting=unchanged", "sk-gradle-secret-123456");
        var environment = new DeepSeekClient(mapper,"environment-secret",local,URI.create("http://localhost"),properties);
        assertThat(environment.status().get("source")).isEqualTo("environment");
        Files.writeString(properties,"deepseek.api.key=\n");
        assertThat(client.status().get("source")).isEqualTo("local");
        Files.delete(local);assertThat(client.status().get("configured")).isEqualTo(false);
        Files.writeString(properties,"deepseek.api.key=sk-updated-secret-123456\n");
        assertThat(client.status().get("source")).isEqualTo("gradle");
    }

    @Test void upstreamAuthenticationErrorIsUsefulAndDoesNotLeakBody() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> { byte[] data="secret-upstream-body".getBytes();exchange.sendResponseHeaders(401,data.length);exchange.getResponseBody().write(data);exchange.close(); });
        server.start();
        try {
            var client = new DeepSeekClient(mapper, "test", temporary.resolve("key"), URI.create("http://127.0.0.1:" + server.getAddress().getPort()));
            var events = new ArrayList<String>();client.stream(chat(), (event,data) -> events.add(event+mapper.writeValueAsString(data)));
            assertThat(events).hasSize(1);assertThat(events.getFirst()).contains("401", "API Key").doesNotContain("secret-upstream-body");
        } finally { server.stop(0); }
    }

    @Test void cancellationClosesAnUpstreamStreamWithoutWaitingForAnotherToken() throws Exception {
        var first = new CountDownLatch(1);var release = new CountDownLatch(1);
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            try {
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().write("data: {\"choices\":[{\"delta\":{\"content\":\"partial\"}}]}\n\n".getBytes(StandardCharsets.UTF_8));
                exchange.getResponseBody().flush();release.await(5, TimeUnit.SECONDS);
            } catch (Exception ignored) {} finally { exchange.close(); }
        });server.start();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var client = new DeepSeekClient(mapper,"test",temporary.resolve("key"),URI.create("http://127.0.0.1:"+server.getAddress().getPort()));
            var task = executor.submit(() -> { client.stream(chat(), (event,data)->first.countDown(), "cancel-test");return true; });
            assertThat(first.await(3,TimeUnit.SECONDS)).isTrue();client.cancel("cancel-test");
            assertThat(task.get(2,TimeUnit.SECONDS)).isTrue();
        } finally { release.countDown();server.stop(0); }
    }

    @Test void validatesInputAndBlocksCrossSiteCalls() {
        var client = new DeepSeekClient(mapper,"",temporary.resolve("key"),URI.create("http://localhost"));
        assertThatThrownBy(() -> client.validate(new DeepSeekClient.Chat("unknown", chat().messages()))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> client.validate(new DeepSeekClient.Chat("deepseek-chat",List.of(new DeepSeekClient.Message("user","x".repeat(120001)))))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> client.validate(new DeepSeekClient.Chat("deepseek-chat",List.of(new DeepSeekClient.Message("tool","x"))))).isInstanceOf(IllegalArgumentException.class);
        var request = new MockHttpServletRequest();request.setLocalPort(18090);request.addHeader("Origin","https://evil.example");
        assertThatThrownBy(() -> AiController.checkOrigin(request)).isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
        request.removeHeader("Origin");request.addHeader("Origin","http://127.0.0.1:18090");
        assertThatCode(() -> AiController.checkOrigin(request)).doesNotThrowAnyException();
    }
}
