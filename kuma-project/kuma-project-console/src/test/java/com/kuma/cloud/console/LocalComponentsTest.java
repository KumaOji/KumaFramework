package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.ObjectMapper;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.assertj.core.api.Assertions.*;

class LocalComponentsTest {
    @TempDir Path root;
    @Test void localServiceDiscoveryIsScopedToServiceAndRepository() {
        for (String name : List.of("uaa", "gateway")) {
            var service = new LocalJavaService(root.toString(), "Ubuntu-24.04", new CommandRunner(), new ObjectMapper(), name);
            assertThat(service.matches("java.exe", "java -jar \"" + root.resolve("logs/" + name + "-local/runtime/hash.jar") + "\"")).isTrue();
            assertThat(service.matches("java.exe", "java -jar \"" + root.resolve("logs/blog-local/runtime/hash.jar") + "\"")).isFalse();
            assertThat(service.matches("java.exe", "java -jar D:/other/" + name + ".jar")).isFalse();
            assertThat(service.matches("wslrelay.exe", "java -jar " + root.resolve("logs/" + name + "-local/runtime/hash.jar"))).isFalse();
        }
    }
    @Test void rejectsUnknownComponentAndActions() {
        var components = new LocalComponents(root.toString(), "Ubuntu-24.04", new CommandRunner(), new ObjectMapper());
        assertThatThrownBy(() -> components.action("mysql", "stop")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> components.action("uaa", "delete")).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void gatewayStatusAndLogsNeverInvokeWsl() throws Exception {
        var calls = new CopyOnWriteArrayList<List<String>>();
        var commands = new CommandRunner() {
            @Override public Result run(List<String> args, Duration timeout) { calls.add(args); return new Result(true, "", ""); }
        };
        var gateway = new GatewayLifecycle(root, "Ubuntu-24.04", commands);
        gateway.status(); gateway.logs(); gateway.shutdown();
        assertThat(calls).allSatisfy(args -> assertThat(args).doesNotContain("wsl", "wsl.exe", "systemctl"));
        assertThat(gateway.status()).containsEntry("url", "http://localhost:18080");
    }
}
