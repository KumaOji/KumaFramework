package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import javax.tools.ToolProvider;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.jar.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@EnabledOnOs(OS.WINDOWS)
class LocalJavaServiceRestartTest {
    @TempDir Path root;

    private LocalJavaService console(String name, int port) {
        var service = new LocalJavaService(root.toString(), "Ubuntu-24.04", new CommandRunner(), new ObjectMapper(), name);
        ReflectionTestUtils.setField(service, "port", port);
        return service;
    }

    @Test void reopensConsoleAndStopsMatchingBlogUaaGatewayJvms() throws Exception {
        Path source = root.resolve("FixtureService.java");
        Files.writeString(source, """
                public class FixtureService {
                    public static void main(String[] args) throws Exception {
                        try (var listener = new java.net.ServerSocket(Integer.parseInt(args[0]))) {
                            while (true) { listener.accept().close(); }
                        }
                    }
                }
                """);
        assertThat(ToolProvider.getSystemJavaCompiler().run(null, null, null, source.toString())).isZero();
        for (String name : List.of("blog", "uaa", "gateway")) {
            Path jar = root.resolve("logs/" + name + "-local/runtime/fixture.jar");
            Files.createDirectories(jar.getParent());
            var manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "FixtureService");
            try (var output = new JarOutputStream(Files.newOutputStream(jar), manifest)) {
                output.putNextEntry(new JarEntry("FixtureService.class"));
                Files.copy(root.resolve("FixtureService.class"), output);
                output.closeEntry();
            }
            int port; try (var listener = new ServerSocket(0)) { port = listener.getLocalPort(); }
            Process running = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin/java.exe").toString(),
                    "-jar", jar.toString(), Integer.toString(port)).redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD).start();
            try {
                var original = console(name, port);
                assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
                    while (!Boolean.TRUE.equals(original.status().get("canStop"))) Thread.sleep(50);
                });
                try (var context = new AnnotationConfigApplicationContext()) {
                    context.registerBean(LocalJavaService.class, () -> original);
                    context.refresh();
                }
                assertThat(running.isAlive()).as("%s survives Console exit", name).isTrue();
                var reopened = console(name, port);
                assertThat(reopened.status()).containsEntry("state", "RUNNING").containsEntry("canStop", true)
                        .containsEntry("pid", running.pid());
                reopened.stop();
                assertTimeoutPreemptively(Duration.ofSeconds(15), () -> {
                    while (running.isAlive() || !reopened.status().get("state").equals("STOPPED")) Thread.sleep(50);
                });
            } finally { LabLifecycle.terminate(running); }
        }
    }
}
