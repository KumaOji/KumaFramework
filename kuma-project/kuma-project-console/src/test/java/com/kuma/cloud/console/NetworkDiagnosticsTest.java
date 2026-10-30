package com.kuma.cloud.console;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class NetworkDiagnosticsTest {
    @Test
    void extractsHttpsProxyWithoutMistakingHttpOnlyConfigForHttps() {
        assertThat(NetworkDiagnostics.windowsHttpsProxy("http=localhost:80;https=localhost:81"))
                .isEqualTo("http://localhost:81");
        assertThat(NetworkDiagnostics.windowsHttpsProxy("http=localhost:80")).isEmpty();
        assertThat(NetworkDiagnostics.windowsHttpsProxy("localhost:7890")).isEqualTo("http://localhost:7890");
        assertThat(NetworkDiagnostics.validProxy("socks5h://localhost:7890")).isTrue();
        assertThat(NetworkDiagnostics.validProxy("http://user:secret@localhost:7890")).isFalse();
        assertThat(NetworkDiagnostics.redact("http://user:secret@localhost:7890/path?token=private"))
                .doesNotContain("secret", "private", "user:");
    }

    @Test
    void directProbeBypassesEnvironmentAndReportsBlockedHttpAsReachable() throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> { exchange.sendResponseHeaders(403, -1); exchange.close(); });
        server.start();
        try {
            var diagnostics = new NetworkDiagnostics(new CommandRunner(), new ObjectMapper());
            var result = diagnostics.probe("local", "http://127.0.0.1:" + server.getAddress().getPort(), "direct", "");
            assertThat(result.get("reachable")).isEqualTo(true);
            assertThat(result.get("status")).isEqualTo(403);
            assertThat(result.get("totalMs")).isInstanceOf(Double.class);
        } finally { server.stop(0); }
    }

    @Test
    void timeoutRetainsPartialTimingAndError() {
        var commands = new CommandRunner() {
            @Override public Result run(List<String> args, Duration timeout) {
                assertThat(args).contains("--max-time", "8", "--proxy", "--noproxy");
                return new Result(false, "{\"http_code\":0,\"time_total\":8.0}", "curl: (28) timeout");
            }
        };
        var result = new NetworkDiagnostics(commands, new ObjectMapper()).probe("test", "https://example.com", "direct", "");
        assertThat(result.get("reachable")).isEqualTo(false);
        assertThat(result.get("totalMs")).isEqualTo(8000d);
        assertThat(result.get("error")).isEqualTo("curl: (28) timeout");
    }
}
