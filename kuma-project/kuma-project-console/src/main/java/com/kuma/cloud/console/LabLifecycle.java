package com.kuma.cloud.console;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class LabLifecycle {
    private final Path workspace;
    private final URI target;
    private Process process;
    private String phase = "STOPPED", message = "Lab 尚未启动";
    private long generation;
    private Path log;

    public LabLifecycle(ConsoleProperties properties, @Value("${console.workspace:}") String root) {
        workspace = root.isBlank() ? findWorkspace(Path.of(System.getProperty("user.dir"))) : Path.of(root).toAbsolutePath().normalize();
        target = properties.projects().stream().filter(p -> p.name().equalsIgnoreCase("Lab"))
                .map(p -> URI.create(p.url())).findFirst().orElse(null);
    }

    static Path findWorkspace(Path start) {
        for (Path path = start.toAbsolutePath().normalize(); path != null; path = path.getParent()) {
            if (Files.isDirectory(path.resolve("kuma-project/kuma-project-lab")) && Files.exists(path.resolve("settings.gradle"))) return path;
        }
        return start.toAbsolutePath().normalize();
    }

    private int port() { return target.getPort() < 0 ? ("https".equals(target.getScheme()) ? 443 : 80) : target.getPort(); }
    private boolean local() { return target != null && "http".equals(target.getScheme()) && Set.of("localhost", "127.0.0.1", "[::1]").contains(target.getHost()); }
    private boolean reachable() {
        if (!local()) return false;
        try (Socket socket = new Socket()) { socket.connect(new InetSocketAddress(target.getHost(), port()), 200); return true; }
        catch (IOException e) { return false; }
    }

    public synchronized Map<String, Object> status() {
        if (process != null && !process.isAlive() && Set.of("STARTING", "RUNNING").contains(phase)) {
            phase = "FAILED"; message = "Lab 已退出（" + process.exitValue() + "），请查看启动日志"; process = null;
        }
        if (phase.equals("STARTING") && reachable()) { phase = "RUNNING"; message = "Lab 已监听端口 " + port(); }
        boolean busy = Set.of("BUILDING", "STARTING", "STOPPING").contains(phase);
        boolean occupied = process == null && !busy && reachable();
        boolean available = local() && Files.isDirectory(workspace.resolve("kuma-project/kuma-project-lab"));
        return Map.of("state", occupied ? "EXTERNAL" : phase, "message", occupied ? "端口 " + port() + " 已被其他进程占用；请检查占用服务或调整 KUMA_LAB_URL" : message,
                "pid", process == null ? 0L : process.pid(), "canStart", available && !busy && process == null && !occupied,
                "canStop", !phase.equals("STOPPING") && (process != null || phase.equals("BUILDING")),
                "logPath", log == null ? "" : log.toString());
    }

    public synchronized Map<String, Object> start() {
        if (!Boolean.TRUE.equals(status().get("canStart"))) throw new IllegalArgumentException("当前状态无法启动本机 Lab，请检查仓库路径、Lab 地址和端口");
        phase = "BUILDING"; message = "正在检查 Lab 构建缓存…";
        long token = ++generation;
        Thread.ofVirtual().name("lab-launch").start(() -> launch(token));
        return status();
    }

    private void launch(long token) {
        try {
            Path module = workspace.resolve("kuma-project/kuma-project-lab");
            Path runtime = module.resolve("build/console-runtime");
            Files.createDirectories(runtime);
            Path output = runtime.resolve("lab-" + UUID.randomUUID() + ".log");
            boolean windows = System.getProperty("os.name").startsWith("Windows");
            List<String> build = windows ? List.of("cmd.exe", "/d", "/c", "gradlew.bat", ":kuma-project:kuma-project-lab:bootJar", "--console=plain", "--no-daemon")
                    : List.of("sh", "gradlew", ":kuma-project:kuma-project-lab:bootJar", "--console=plain", "--no-daemon");
            boolean rebuild = BuildArtifacts.needsBuild(workspace, "lab");
            Process building = null;
            synchronized (this) {
                if (token != generation) return;
                log = output;
                Files.writeString(output, rebuild ? "Lab 缓存失效，执行增量构建\n" : "复用已构建的 Lab JAR，跳过 Gradle\n");
                if (rebuild) {
                    process = new ProcessBuilder(build).directory(workspace.toFile()).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.appendTo(output.toFile())).start();
                    building = process;
                }
            }
            int exit = building == null ? 0 : building.waitFor();
            synchronized (this) {
                if (token != generation) return;
                process = null;
                if (exit != 0) throw new IOException("Lab 构建失败（" + exit + "）");
                if (rebuild) BuildArtifacts.record(workspace, "lab");
                if (reachable()) throw new IOException("Lab 端口已被占用");
                Path jar;
                try (var files = Files.list(module.resolve("build/libs"))) {
                    jar = files.filter(p -> p.getFileName().toString().matches("lab.*\\.jar") && !p.getFileName().toString().endsWith("-plain.jar"))
                            .max(Comparator.comparingLong(p -> p.toFile().lastModified())).orElseThrow(() -> new IOException("未找到 Lab JAR"));
                }
                Path copy = BuildArtifacts.runtimeCopy(jar, runtime, "lab");
                String java = Path.of(System.getProperty("java.home"), "bin", windows ? "java.exe" : "java").toString();
                List<String> command = new ArrayList<>(List.of(java, "--enable-preview", "--enable-native-access=ALL-UNNAMED", "-jar", copy.toString(),
                        "--spring.profiles.active=dev", "--server.port=" + port(), "--server.servlet.context-path=" + (target.getPath().isBlank() ? "/" : target.getPath())));
                Path config = module.resolve("src/main/resources/bootstrap-local.yml");
                if (Files.exists(config)) command.add("--spring.config.additional-location=" + config.toUri());
                process = new ProcessBuilder(command).directory(workspace.toFile()).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.appendTo(output.toFile())).start();
                phase = "STARTING"; message = "Lab 正在启动，等待端口 " + port() + "…";
            }
            long deadline = System.nanoTime() + Duration.ofMinutes(3).toNanos();
            while (System.nanoTime() < deadline) {
                synchronized (this) { if (token != generation || !status().get("state").equals("STARTING")) return; }
                Thread.sleep(500);
            }
            synchronized (this) { if (token == generation) message = "Lab 启动超过 3 分钟，请查看日志或停止后重试"; }
        } catch (Exception e) {
            synchronized (this) { if (token == generation) { phase = "FAILED"; message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage(); } }
        }
    }

    public synchronized Map<String, Object> stop() {
        if (phase.equals("STOPPING")) return status();
        ++generation;
        Process owned = process;
        if (owned == null) { phase = "STOPPED"; message = "Lab 已停止"; return status(); }
        phase = "STOPPING"; message = "正在停止 Lab…";
        Thread.ofVirtual().name("lab-stop").start(() -> {
            terminate(owned);
            synchronized (this) {
                if (owned.isAlive()) { phase = "FAILED"; message = "停止失败，请查看进程 " + owned.pid(); }
                else { process = null; phase = "STOPPED"; message = "Lab 已停止"; }
            }
        });
        return status();
    }

    static void terminate(Process process) {
        var children = process.descendants().toList();
        children.forEach(ProcessHandle::destroy); process.destroy();
        try { process.waitFor(8, TimeUnit.SECONDS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        children.stream().filter(ProcessHandle::isAlive).forEach(ProcessHandle::destroyForcibly);
        if (process.isAlive()) process.destroyForcibly();
        try { process.waitFor(3, TimeUnit.SECONDS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public synchronized Map<String, String> logs() throws IOException {
        if (log == null || !Files.exists(log)) return Map.of("body", "尚无启动日志");
        try (RandomAccessFile file = new RandomAccessFile(log.toFile(), "r")) {
            file.seek(Math.max(0, file.length() - 32768));
            byte[] bytes = new byte[(int) (file.length() - file.getFilePointer())]; file.readFully(bytes);
            return Map.of("body", new String(bytes, StandardCharsets.UTF_8));
        }
    }

    @PreDestroy
    public synchronized void shutdown() { ++generation; if (process != null) terminate(process); }
}
