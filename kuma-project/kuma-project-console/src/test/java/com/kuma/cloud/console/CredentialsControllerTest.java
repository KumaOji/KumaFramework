package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.*;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CredentialsControllerTest {
    @TempDir Path workspace;

    private MockHttpServletRequest request() {
        var request = new MockHttpServletRequest("POST", "/api/credentials");
        request.setRemoteAddr("127.0.0.1");
        request.setServerName("localhost");
        request.setServerPort(18888);
        request.addHeader("Origin", "http://localhost:18888");
        request.addHeader("X-Kuma-Credentials", "console");
        return request;
    }

    private CredentialsController controller(CommandRunner commands) throws Exception {
        Files.createDirectories(workspace.resolve("scripts"));
        Files.writeString(workspace.resolve("scripts/get-console-credentials.ps1"), "# fixture only");
        return new CredentialsController(commands, new ObjectMapper(), workspace.toString(), "Ubuntu-24.04");
    }

    @Test void rejectsRemoteCrossOriginAndMissingHeadersBeforeExecuting() throws Exception {
        var commands = mock(CommandRunner.class);
        var controller = controller(commands);
        var remote = request(); remote.setRemoteAddr("192.168.1.4");
        var origin = request(); origin.removeHeader("Origin"); origin.addHeader("Origin", "http://evil.example:18888");
        var absent = request(); absent.removeHeader("Origin");
        var header = request(); header.removeHeader("X-Kuma-Credentials");
        var host = request(); host.setServerName("evil.example"); host.removeHeader("Origin"); host.addHeader("Origin", "http://evil.example:18888");
        for (var request : List.of(remote, origin, absent, header, host)) {
            var response = new MockHttpServletResponse();
            assertThatThrownBy(() -> controller.read(new CredentialsController.Query("all", false), request, response))
                    .isInstanceOfSatisfying(ResponseStatusException.class, e -> assertThat(e.getStatusCode().value()).isEqualTo(403));
            assertThat(response.getHeader("Cache-Control")).isEqualTo("no-store");
        }
        verifyNoInteractions(commands);
    }

    @Test void rejectsBulkRevealAndCommandInjection() throws Exception {
        var commands = mock(CommandRunner.class);
        var controller = controller(commands);
        for (String service : List.of("all", "grafana'; echo secret", "-Reveal", "grafana,nacos"))
            assertThatThrownBy(() -> controller.read(new CredentialsController.Query(service, true), request(), new MockHttpServletResponse()))
                    .isInstanceOfSatisfying(ResponseStatusException.class, e -> assertThat(e.getStatusCode().value()).isEqualTo(400));
        verifyNoInteractions(commands);
    }

    @Test void usesMaskedDefaultAndExplicitSingleServiceReveal() throws Exception {
        org.junit.jupiter.api.Assumptions.assumeTrue(System.getProperty("os.name").startsWith("Windows"));
        var commands = mock(CommandRunner.class);
        var controller = controller(commands);
        when(commands.run(anyList(), any(Duration.class))).thenReturn(new CommandRunner.Result(true,
                "[{\"service\":\"grafana\",\"password\":\"********\"}]", ""));
        controller.read(new CredentialsController.Query(null, false), request(), new MockHttpServletResponse());
        verify(commands).run(argThat(args -> args.getLast().contains("-Service 'all'") && !args.getLast().contains("-Reveal")), eq(Duration.ofSeconds(60)));
        controller.read(new CredentialsController.Query("grafana", true), request(), new MockHttpServletResponse());
        verify(commands).run(argThat(args -> args.getLast().contains("-Service 'grafana'") && args.getLast().endsWith(" -Reveal")), any());
    }

    @Test void upstreamErrorsAndMalformedJsonNeverLeakSourceOutput() throws Exception {
        org.junit.jupiter.api.Assumptions.assumeTrue(System.getProperty("os.name").startsWith("Windows"));
        var commands = mock(CommandRunner.class);
        var controller = controller(commands);
        for (var result : List.of(new CommandRunner.Result(false, "fixture-secret", "fixture-secret"),
                new CommandRunner.Result(true, "fixture-secret", ""))) {
            when(commands.run(anyList(), any())).thenReturn(result);
            assertThatThrownBy(() -> controller.read(new CredentialsController.Query("grafana", true), request(), new MockHttpServletResponse()))
                    .isInstanceOf(ResponseStatusException.class).hasMessageNotContaining("fixture-secret");
        }
    }
}
