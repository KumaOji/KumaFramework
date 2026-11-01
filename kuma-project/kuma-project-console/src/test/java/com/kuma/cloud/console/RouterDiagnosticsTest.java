package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class RouterDiagnosticsTest {
    @Test
    void probesOnlyFixedRouterDirectlyAndRetainsFailedHttpsDetails() {
        var calls = new AtomicInteger();
        var mapper = new ObjectMapper();
        var commands = new CommandRunner() {
            @Override public Result run(List<String> args, Duration timeout) {
                if (args.getFirst().equals("powershell"))
                    return new Result(true, "{\"pingStatus\":\"Success\",\"pingMs\":2,\"neighbors\":[{\"mac\":\"AA-BB-CC-DD-EE-FF\",\"index\":1}],\"routes\":[]}", "");
                calls.incrementAndGet();
                assertThat(args).contains("--head", "--noproxy", "*");
                assertThat(args.get(args.indexOf("--proxy") + 1)).isEmpty();
                assertThat(args.getLast()).isIn("http://192.168.0.1/", "https://192.168.0.1/");
                if (args.getLast().startsWith("https:"))
                    return new Result(false, "{\"http_code\":0,\"time_total\":0.1}", "certificate verify failed");
                return new Result(true, "{\"http_code\":401,\"time_total\":0.02}", "");
            }
        };
        var router = new RouterDiagnostics(commands, new NetworkDiagnostics(commands, mapper), mapper);
        var result = mapper.valueToTree(router.inspect());
        assertThat(result.path("address").asString()).isEqualTo("192.168.0.1");
        assertThat(result.path("services").get(0).path("reachable").asBoolean()).isTrue();
        assertThat(result.path("services").get(0).path("status").asInt()).isEqualTo(401);
        assertThat(result.path("services").get(1).path("reachable").asBoolean()).isFalse();
        assertThat(result.path("services").get(1).path("error").asString()).contains("certificate");
        assertThat(result.path("testedAt").asString()).isNotBlank();
        router.inspect();
        assertThat(calls.get()).isEqualTo(4);
    }

    @Test
    void unavailableLocalInventoryDoesNotDiscardManagementProbes() {
        var mapper = new ObjectMapper();
        var commands = new CommandRunner() {
            @Override public Result run(List<String> args, Duration timeout) {
                return new Result(false, "", "unavailable");
            }
        };
        var result = new RouterDiagnostics(commands, new NetworkDiagnostics(commands, mapper), mapper).inspect();
        assertThat(result).containsKey("warning");
        assertThat((List<?>) result.get("services")).hasSize(2);
        assertThat((Map<?, ?>) result.get("local")).isEmpty();
    }
}
