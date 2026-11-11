package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class LifeLifecycleTest {
    static class Commands extends CommandRunner {
        boolean running = true, accessible = true, failRead = false;
        String nodeIp = "172.23.89.45";
        volatile int replicas = 1;
        final List<List<String>> calls = new CopyOnWriteArrayList<>();
        @Override public Result run(List<String> command, Duration timeout) {
            calls.add(command);
            if (command.contains("--list")) return new Result(true, running ? "Ubuntu-24.04\n" : "", "");
            if (command.contains("nodes")) return new Result(true, "{\"items\":[{\"status\":{\"addresses\":[{\"type\":\"InternalIP\",\"address\":\"" + nodeIp + "\"}]}}]}", "");
            if (command.contains("json")) {
                if (failRead) return new Result(false, "", "k3s unavailable");
                String items = String.join(",", List.of("qinglong", "rsshub", "redis").stream()
                        .map(name -> "{\"metadata\":{\"name\":\"" + name + "\"},\"spec\":{\"replicas\":" + replicas
                                + "},\"status\":{\"readyReplicas\":" + replicas + "}}").toList());
                return new Result(true, "{\"items\":[" + items + "]}", "");
            }
            if (command.getLast().startsWith("curl ")) return new Result(accessible, "", "");
            if (command.getLast().contains("--replicas=0")) replicas = 0;
            if (command.getLast().contains("--replicas=1")) { running = true; replicas = 1; }
            return new Result(true, "resources", "");
        }
    }
    private LifeLifecycle life(Commands commands) {
        return new LifeLifecycle(commands, new ObjectMapper(), "Ubuntu-24.04", "D:/Vscode_project/life", 5700, 1200);
    }
    @Test void stoppedWslIsNotStartedByStatusOrDetails() {
        var commands = new Commands(); commands.running = false;
        var life = life(commands);
        assertThat(life.status()).containsEntry("state", "WSL_STOPPED").containsEntry("canStart", true).containsEntry("canStop", false);
        life.details();
        assertThat(commands.calls).allSatisfy(call -> assertThat(call).contains("--list"));
    }
    @Test void readsExistingDeploymentAndChecksDirectAccess() {
        var commands = new Commands(); var life = life(commands);
        assertThat(life.status()).containsEntry("state", "RUNNING").containsEntry("canStop", true)
                .containsEntry("qinglongUrl", "http://172.23.89.45:5700").containsEntry("rsshubUrl", "http://172.23.89.45:1200");
        commands.nodeIp = "172.23.90.10";
        assertThat(life.status()).containsEntry("qinglongUrl", "http://172.23.90.10:5700");
        commands.accessible = false;
        assertThat(life.status()).containsEntry("state", "DEGRADED").containsEntry("canStart", true);
        commands.failRead = true;
        assertThat(life.status()).containsEntry("state", "UNKNOWN").containsEntry("canStart", false).containsEntry("canStop", false);
    }
    @Test void stopScalesOnlyLifeDeploymentsAndStartRestoresThem() {
        var commands = new Commands(); var life = life(commands);
        life.action("stop");
        assertTimeoutPreemptively(Duration.ofSeconds(3), () -> { while (life.status().get("state").equals("STOPPING")) Thread.sleep(10); });
        assertThat(commands.replicas).isZero();
        commands.accessible = false;
        life.action("start");
        assertTimeoutPreemptively(Duration.ofSeconds(3), () -> { while (life.status().get("state").equals("STARTING")) Thread.sleep(10); });
        assertThat(commands.replicas).isEqualTo(1);
        var scripts = commands.calls.stream().map(List::getLast).filter(s -> s.contains("--replicas=")).toList();
        assertThat(scripts).hasSize(2).allSatisfy(script -> {
            assertThat(script).contains("-n life-deals", "deployment/qinglong deployment/rsshub deployment/redis");
            assertThat(script).doesNotContain("delete", "--shutdown", "stop k3s");
        });
        assertThatThrownBy(() -> life.action("credentials")).isInstanceOf(IllegalArgumentException.class);
        assertThat(commands.calls).noneSatisfy(call -> assertThat(call).contains("credentials", "cat"));
        assertThat(commands.calls).allSatisfy(call -> assertThat(String.join(" ", call)).doesNotContain("port-forward", "life-deals-qinglong", "life-deals-rsshub"));
    }
}
