package com.kuma.cloud.console;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpServletRequest;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.net.ServerSocket;
import java.time.Duration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class DshLifecycleTest {
    @TempDir Path temporary;

    @Test void missingCheckoutCannotStartAndLaunchUsesConfiguredCheckout() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        int port = server.getAddress().getPort(); server.stop(0);
        var dsh = new DshLifecycle(temporary.toString(), port, temporary.toString());
        assertThat(dsh.status()).containsEntry("canStart", false).containsEntry("canOpen", false);
        assertThatThrownBy(dsh::start).isInstanceOf(IllegalArgumentException.class);
        Files.createDirectories(temporary.resolve("apps/cli/src"));
        Files.writeString(temporary.resolve("apps/cli/src/bin.ts"), "");
        Files.createDirectories(temporary.resolve("node_modules/tsx"));
        assertThat(dsh.status()).containsEntry("canStart", true);
        assertThat(dsh.launchCommand()).containsSubsequence("node", "--import", "tsx/esm",
                temporary.resolve("apps/cli/src/bin.ts").toString(), "web", "--host", "127.0.0.1", "--port", Integer.toString(port), "--no-open");
    }

    @Test void doesNotStopAnUnidentifiedProcessEvenIfItsHttpResponseLooksLikeDsh() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] body = "<script>globalThis.__DSH_BOOT__ = {};</script>".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length); exchange.getResponseBody().write(body); exchange.close();
        }); server.start();
        try {
            var dsh = new DshLifecycle(temporary.toString(), server.getAddress().getPort(), temporary.toString());
            assertThat(dsh.status()).containsEntry("state", "RUNNING").containsEntry("canOpen", true)
                    .containsEntry("canStart", false).containsEntry("canStop", false);
            assertThat(dsh.stop()).containsEntry("state", "RUNNING");
            assertThatThrownBy(dsh::start).isInstanceOf(IllegalArgumentException.class);
        } finally { server.stop(0); }
    }

    @Test void identifiesOnlyConfiguredCheckoutWebCommandAndPort() {
        var dsh = new DshLifecycle(temporary.toString(), 3088, temporary.toString());
        String cli = temporary.resolve("apps/cli/src/bin.ts").toString();
        assertThat(dsh.matches("node.exe", "node --import tsx/esm \"" + cli + "\" web --port 3088 --no-open")).isTrue();
        assertThat(dsh.matches("node.exe", "node \"" + cli + "\" web --port 30880")).isFalse();
        assertThat(dsh.matches("node.exe", "node \"" + cli + "\" web")).isFalse();
        assertThat(dsh.matches("node.exe", "node \"" + cli + "\" chat --port 3088")).isFalse();
        assertThat(dsh.matches("node.exe", "node D:/other/apps/cli/src/bin.ts web --port 3088")).isFalse();
        assertThat(dsh.matches("java.exe", "node \"" + cli + "\" web --port 3088")).isFalse();
        assertThat(dsh.matches("node.exe", "node \"" + cli + ".other\" web --port 3088")).isFalse();
    }

    @Test void rediscoversAndStopsDshAfterConsoleRestartIncludingLoginRequiredState() throws Exception {
        Path checkout = temporary.resolve("dsh checkout");
        Path cli = checkout.resolve("apps/cli/src/bin.ts");
        Files.createDirectories(cli.getParent());
        Files.createDirectories(checkout.resolve("node_modules/tsx"));
        Files.writeString(cli, """
                const http = require('node:http');
                const port = Number(process.argv[process.argv.indexOf('--port') + 1]);
                const auth = process.argv.includes('--auth');
                http.createServer((req, res) => {
                    res.writeHead(auth ? 401 : 200);
                    res.end(auth ? 'dsh web authentication required' : '<script>__DSH_BOOT__ = {}</script>');
                }).listen(port, '127.0.0.1');
                """);
        for (boolean auth : new boolean[]{false, true}) {
            int port; try (var socket = new ServerSocket(0)) { port = socket.getLocalPort(); }
            Process running = new ProcessBuilder("node", cli.toString(), "web", "--port", Integer.toString(port), auth ? "--auth" : "--no-open")
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD).redirectError(ProcessBuilder.Redirect.DISCARD).start();
            try {
                var beforeRestart = new DshLifecycle(checkout.toString(), port, temporary.toString());
                assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
                    while (!Boolean.TRUE.equals(beforeRestart.status().get("canStop"))) Thread.sleep(100);
                });
                assertThat(beforeRestart.status()).containsEntry("state", auth ? "AUTH_REQUIRED" : "RUNNING");
                try (var console = new AnnotationConfigApplicationContext()) {
                    console.registerBean(DshLifecycle.class, () -> beforeRestart);
                    console.refresh();
                }
                assertThat(running.isAlive()).as("exiting Console preserves dsh").isTrue();
                var afterRestart = new DshLifecycle(checkout.toString(), port, temporary.toString());
                assertThat(afterRestart.status()).containsEntry("canStop", true);
                afterRestart.stop();
                assertTimeoutPreemptively(Duration.ofSeconds(15), () -> {
                    while (running.isAlive() || !afterRestart.status().get("state").equals("STOPPED")) Thread.sleep(100);
                });
                assertThat(afterRestart.status()).containsEntry("canStop", false).containsEntry("canStart", true);
            } finally { LabLifecycle.terminate(running); }
        }
    }

    @Test void unrelatedHttpServiceIsNotEmbeddedOrStopped() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> { exchange.sendResponseHeaders(200, -1); exchange.close(); }); server.start();
        try {
            var dsh = new DshLifecycle(temporary.toString(), server.getAddress().getPort(), temporary.toString());
            assertThat(dsh.status()).containsEntry("state", "EXTERNAL").containsEntry("canOpen", false).containsEntry("canStart", false);
        } finally { server.stop(0); }
    }

    @Test void crossSiteLaunchIsRejected() {
        var request = new MockHttpServletRequest(); request.setLocalPort(18090);
        request.addHeader("Origin", "https://evil.example");
        assertThatThrownBy(() -> AiController.checkOrigin(request)).isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
        request.removeHeader("Origin"); request.addHeader("Origin", "http://127.0.0.1:18090");
        assertThatCode(() -> AiController.checkOrigin(request)).doesNotThrowAnyException();
    }

    @Test void startupTokenAuthenticatesThePreviewAndIsRedactedFromLogs() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            if ("token=fixture-token".equals(exchange.getRequestURI().getQuery())) {
                exchange.getResponseHeaders().add("Set-Cookie", "dsh-auth-fixture=value; HttpOnly; SameSite=Strict");
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(303, -1);
            } else {
                byte[] body = "dsh web authentication required".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(401, body.length); exchange.getResponseBody().write(body);
            }
            exchange.close();
        }); server.start();
        try {
            int port = server.getAddress().getPort();
            var dsh = new DshLifecycle(temporary.toString(), port, temporary.toString());
            assertThat(dsh.status()).containsEntry("state", "AUTH_REQUIRED").containsEntry("canStart", false)
                    .containsEntry("canOpen", true).containsEntry("url", "http://127.0.0.1:" + port);
            Path log = temporary.resolve("build/dsh-runtime/dsh.log"); Files.createDirectories(log.getParent());
            Files.writeString(log, "dsh web: http://127.0.0.1:" + port + "/?token=expired-token\n");
            assertThat(dsh.status()).as("A stale token must not lock out an authenticated browser")
                    .containsEntry("state", "AUTH_REQUIRED").containsEntry("canOpen", true)
                    .containsEntry("url", "http://127.0.0.1:" + port);
            Files.writeString(log, "dsh web: http://127.0.0.1:" + port + "/?token=fixture-token\n", StandardOpenOption.APPEND);
            assertThat(dsh.status()).containsEntry("state", "RUNNING").containsEntry("canOpen", true);
            assertThat(dsh.status().get("url")).isEqualTo("http://127.0.0.1:" + port + "/?token=fixture-token");
            var response = new org.springframework.mock.web.MockHttpServletResponse();
            assertThat(new AiController(dsh).logs(response).toString()).contains("[redacted]").doesNotContain("fixture-token");
            assertThat(response.getHeader("Cache-Control")).isEqualTo("no-store");
        } finally { server.stop(0); }
    }
}
