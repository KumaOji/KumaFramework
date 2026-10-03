package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class RemoteServerMonitorTest {
    @Test
    void failedSamplingKeepsLastSuccessAndMarksItOffline() {
        var runner = new CommandRunner() {
            int calls;
            @Override public Result run(List<String> command, Duration timeout) {
                assertThat(command).contains("BatchMode=yes", "StrictHostKeyChecking=yes", "test.pem");
                return calls++ == 0 ? new Result(true, "{\"hostname\":\"test-host\"}", "")
                        : new Result(false, "", "Connection refused");
            }
        };
        var monitor = new RemoteServerMonitor(runner, new ObjectMapper(), "root@127.0.0.1", "test.pem");
        monitor.sample();
        var sampledAt = monitor.snapshot().get("sampledAt");
        assertThat(monitor.snapshot()).containsEntry("status", "ONLINE").containsEntry("ready", true);
        monitor.sample();
        assertThat(monitor.snapshot()).containsEntry("status", "OFFLINE").containsEntry("hostname", "test-host")
                .containsEntry("sampledAt", sampledAt).containsEntry("error", "Connection refused");
    }
}
