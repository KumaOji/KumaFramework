package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class WslLifecycleTest {
    static class FakeCommands extends CommandRunner {
        final Set<String> running = ConcurrentHashMap.newKeySet();
        final List<List<String>> calls = new CopyOnWriteArrayList<>();
        FakeCommands() { running.add("Ubuntu-24.04"); }
        @Override public Process start(List<String> args) {
            run(args, Duration.ofSeconds(1));
            Process session = org.mockito.Mockito.mock(Process.class);
            org.mockito.Mockito.when(session.isAlive()).thenReturn(true);
            return session;
        }
        @Override public Result run(List<String> args, Duration timeout) {
            calls.add(args);
            if (args.contains("--list")) return new Result(true, args.contains("--running") ? String.join("\n", running) : "Ubuntu-24.04\nMy Linux", "");
            if (args.contains("--distribution")) running.add(args.get(args.indexOf("--distribution") + 1));
            if (args.contains("--terminate")) running.remove(args.get(args.indexOf("--terminate") + 1));
            if (args.contains("--shutdown")) running.clear();
            return new Result(true, "", "");
        }
    }
    @SuppressWarnings("unchecked") private Map<String, Object> distro(WslLifecycle lifecycle, String name) {
        return ((List<Map<String, Object>>)lifecycle.status().get("distributions")).stream().filter(d -> d.get("name").equals(name)).findFirst().orElseThrow();
    }
    @Test void startsStopsAndShutsDownOnlyWithExplicitCommands() {
        var commands = new FakeCommands(); var lifecycle = new WslLifecycle(commands);
        assertThat(distro(lifecycle, "My Linux")).containsEntry("canStart", true).containsEntry("canStop", false);
        assertThat(commands.calls).allSatisfy(args -> assertThat(args).contains("--list"));
        lifecycle.action("My Linux", "start");
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> { while (!distro(lifecycle, "My Linux").get("state").equals("RUNNING")) Thread.sleep(25); });
        assertThat(commands.calls).contains(List.of("wsl.exe", "--distribution", "My Linux", "--exec", "/bin/sh", "-c", "exec sleep infinity"));
        lifecycle.action("My Linux", "stop");
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> { while (!distro(lifecycle, "My Linux").get("state").equals("STOPPED")) Thread.sleep(25); });
        assertThat(commands.calls).contains(List.of("wsl.exe", "--terminate", "My Linux"));
        lifecycle.shutdownAll();
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> { while (!distro(lifecycle, "Ubuntu-24.04").get("state").equals("STOPPED")) Thread.sleep(25); });
        assertThat(commands.calls).contains(List.of("wsl.exe", "--shutdown"));
    }
    @Test void rejectsUnknownDistributionAndArbitraryAction() {
        var commands = new FakeCommands(); var lifecycle = new WslLifecycle(commands);
        assertThatThrownBy(() -> lifecycle.action("Ubuntu-24.04 --shutdown", "stop")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> lifecycle.action("Ubuntu-24.04", "unregister")).isInstanceOf(IllegalArgumentException.class);
        assertThat(commands.calls).noneSatisfy(args -> assertThat(args).contains("--terminate"));
    }
}
