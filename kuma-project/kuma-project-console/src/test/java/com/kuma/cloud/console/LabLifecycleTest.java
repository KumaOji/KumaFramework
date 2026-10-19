package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class LabLifecycleTest {
    @TempDir Path workspace;

    private LabLifecycle lifecycle(String url) throws Exception {
        Files.createDirectories(workspace.resolve("kuma-project/kuma-project-lab"));
        Files.writeString(workspace.resolve("settings.gradle"), "");
        return new LabLifecycle(new ConsoleProperties(false, "", "", List.of(new ConsoleProperties.Project("Lab", url))), workspace.toString());
    }

    @Test void resolvesWorkspaceFromModuleDirectory() throws Exception {
        lifecycle("http://127.0.0.1:9090/api");
        assertThat(LabLifecycle.findWorkspace(workspace.resolve("kuma-project/kuma-project-lab"))).isEqualTo(workspace);
    }

    @Test void rejectsRemoteTargetAndOccupiedPort() throws Exception {
        var remote = lifecycle("http://example.com:9090/api");
        assertThat(remote.status().get("canStart")).isEqualTo(false);
        assertThatThrownBy(remote::start).isInstanceOf(IllegalArgumentException.class);
        try (ServerSocket listener = new ServerSocket(0)) {
            var occupied = lifecycle("http://127.0.0.1:" + listener.getLocalPort() + "/api");
            assertThat(occupied.status()).containsEntry("state", "EXTERNAL").containsEntry("canStart", false).containsEntry("canStop", false);
            assertThatThrownBy(occupied::start).isInstanceOf(IllegalArgumentException.class);
            occupied.stop();
            assertThat(listener.isClosed()).isFalse();
        }
    }

    @Test void duplicateStartIsRejectedAndBuildCanBeCancelled() throws Exception {
        boolean windows = System.getProperty("os.name").startsWith("Windows");
        Files.writeString(workspace.resolve(windows ? "gradlew.bat" : "gradlew"), windows
                ? "@echo off\r\necho fake-build-started\r\nping -n 30 127.0.0.1 >nul\r\n" : "echo fake-build-started\nsleep 30\n");
        var lab = lifecycle("http://127.0.0.1:1/api");
        try {
            assertThat(lab.start().get("state")).isEqualTo("BUILDING");
            assertThatThrownBy(lab::start).isInstanceOf(IllegalArgumentException.class);
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (((Number) lab.status().get("pid")).longValue() == 0 || !lab.logs().get("body").contains("fake-build-started")) Thread.sleep(25);
            });
            long pid = ((Number) lab.status().get("pid")).longValue();
            lab.stop(); lab.stop();
            assertTimeoutPreemptively(Duration.ofSeconds(15), () -> {
                while (!lab.status().get("state").equals("STOPPED")) Thread.sleep(25);
            });
            assertThat(ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false)).isFalse();
            assertThat(lab.status()).containsEntry("canStart", true).containsEntry("canStop", false);
            assertThat(lab.logs().get("body")).contains("fake-build-started");
        } finally { lab.shutdown(); }
    }

    @Test void buildFailureIsReportedInStatusAndLog() throws Exception {
        boolean windows = System.getProperty("os.name").startsWith("Windows");
        Files.writeString(workspace.resolve(windows ? "gradlew.bat" : "gradlew"), windows
                ? "@echo off\r\necho build-failed\r\nexit /b 7\r\n" : "echo build-failed\nexit 7\n");
        var lab = lifecycle("http://127.0.0.1:1/api");
        try {
            lab.start();
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                while (!lab.status().get("state").equals("FAILED")) Thread.sleep(25);
            });
            assertThat(lab.status().get("message").toString()).contains("7");
            assertThat(lab.logs().get("body")).contains("build-failed");
        } finally { lab.shutdown(); }
    }

    @Test void launchesJarDetectsReadinessAndStopsOwnedJvm() throws Exception {
        int port;
        try (ServerSocket listener = new ServerSocket(0)) { port = listener.getLocalPort(); }
        var lab = lifecycle("http://127.0.0.1:" + port + "/api");
        Path source = workspace.resolve("FakeLab.java");
        Files.writeString(source, """
                public class FakeLab {
                    public static void main(String[] args) throws Exception {
                        int port = java.util.Arrays.stream(args).filter(s -> s.startsWith("--server.port="))
                                .mapToInt(s -> Integer.parseInt(s.substring(s.indexOf('=') + 1))).findFirst().orElseThrow();
                        System.out.println("fake-lab-running");
                        try (var socket = new java.net.ServerSocket(port)) { Thread.sleep(60000); }
                    }
                }
                """);
        assertThat(javax.tools.ToolProvider.getSystemJavaCompiler().run(null, null, null, source.toString())).isZero();
        Path libs = workspace.resolve("kuma-project/kuma-project-lab/build/libs");
        Files.createDirectories(libs);
        var manifest = new java.util.jar.Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue("Main-Class", "FakeLab");
        try (var jar = new java.util.jar.JarOutputStream(Files.newOutputStream(libs.resolve("lab-test.jar")), manifest)) {
            jar.putNextEntry(new java.util.jar.JarEntry("FakeLab.class"));
            jar.write(Files.readAllBytes(workspace.resolve("FakeLab.class"))); jar.closeEntry();
        }
        boolean windows = System.getProperty("os.name").startsWith("Windows");
        Files.writeString(workspace.resolve(windows ? "gradlew.bat" : "gradlew"), windows ? "@exit /b 0\r\n" : "exit 0\n");
        try {
            lab.start();
            assertTimeoutPreemptively(Duration.ofSeconds(15), () -> {
                while (!lab.status().get("state").equals("RUNNING")) Thread.sleep(25);
            });
            long pid = ((Number) lab.status().get("pid")).longValue();
            assertThat(lab.logs().get("body")).contains("fake-lab-running");
            assertThat(lab.status()).containsEntry("canStart", false).containsEntry("canStop", true);
            lab.stop();
            assertTimeoutPreemptively(Duration.ofSeconds(15), () -> {
                while (!lab.status().get("state").equals("STOPPED")) Thread.sleep(25);
            });
            assertThat(ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false)).isFalse();
        } finally { lab.shutdown(); }
    }
}
