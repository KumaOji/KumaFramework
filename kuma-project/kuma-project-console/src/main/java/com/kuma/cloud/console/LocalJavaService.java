package com.kuma.cloud.console;

import jakarta.annotation.PreDestroy;


import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;

public class LocalJavaService {
    private final String name, label;
    private final int port;
    private final Path root;
    private final String distribution;
    private final CommandRunner commands;
    private final ObjectMapper mapper;
    private Process launcher;
    private ProcessHandle server;
    private long generation;
    private boolean owned;
    private String phase = "STOPPED", message = "Blog 后端尚未启动";

    public LocalJavaService(String workspace, String distribution, CommandRunner commands, ObjectMapper mapper, String name) {
        if (!Set.of("blog", "uaa", "gateway").contains(name)) throw new IllegalArgumentException("不支持的本机服务");
        this.name = name;
        this.label = switch (name) { case "uaa" -> "UAA"; case "gateway" -> "Gateway"; default -> "Blog 后端"; };
        this.port = switch (name) { case "uaa" -> 33336; case "gateway" -> 18080; default -> 9000; };
        message = label + "尚未启动";
        root = workspace.isBlank() ? LabLifecycle.findWorkspace(Path.of(System.getProperty("user.dir"))) : Path.of(workspace).toAbsolutePath().normalize();
        this.distribution = distribution; this.commands = commands; this.mapper = mapper;
    }

    private boolean listening() {
        try (Socket socket = new Socket()) { socket.connect(new InetSocketAddress("127.0.0.1", port), 200); return true; }
        catch (IOException e) { return false; }
    }

    boolean matches(String executable, String command) {
        String normalized = command.replace('\\', '/').toLowerCase(Locale.ROOT);
        String base = root.toString().replace('\\', '/').toLowerCase(Locale.ROOT) + "/";
        return executable.equalsIgnoreCase("java.exe") && normalized.contains("-jar ")
                && (normalized.contains(base + "logs/" + name + "-local/runtime/") || normalized.contains(base + "kuma-project/kuma-project-" + name + "/"));
    }

    private Optional<ProcessHandle> discover(boolean listenerOnly) {
        if (!System.getProperty("os.name").startsWith("Windows")) return Optional.empty();
        String filter = listenerOnly ? "$listeners = @(Get-NetTCPConnection -State Listen -LocalPort " + port + " -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess); " : "";
        var result = commands.run(List.of("powershell", "-NoProfile", "-Command", filter
                + "Get-CimInstance Win32_Process | Where-Object { $_.Name -eq 'java.exe'" + (listenerOnly ? " -and $_.ProcessId -in $listeners" : "")
                + " } | Select-Object ProcessId,Name,CommandLine | ConvertTo-Json -Compress"), Duration.ofSeconds(5));
        if (!result.success() || result.output().isBlank()) return Optional.empty();
        try {
            var json = mapper.readTree(result.output()); var rows = json.isArray() ? json : mapper.createArrayNode().add(json);
            for (var row : rows) if (matches(row.path("Name").asString(), row.path("CommandLine").asString())) {
                var found = ProcessHandle.of(row.path("ProcessId").asLong()).filter(ProcessHandle::isAlive);
                if (found.isPresent()) return found;
            }
        } catch (Exception ignored) { }
        return Optional.empty();
    }

    public synchronized Map<String, Object> status() {
        if (server != null && !server.isAlive() && !phase.equals("STOPPING")) { server = null; phase = "FAILED"; message = label + "已退出，请查看日志"; }
        boolean occupied = listening(), busy = Set.of("BUILDING", "STARTING", "STOPPING").contains(phase);
        if (server == null && occupied && !busy) discover(true).ifPresent(p -> { server = p; owned = false; phase = "RUNNING"; message = "已识别运行中的 " + label; });
        if (server != null && occupied && phase.equals("STARTING")) { phase = "RUNNING"; message = label + "已监听 " + port; }
        boolean available = System.getProperty("os.name").startsWith("Windows") && Files.exists(root.resolve("scripts/start-services-local.ps1"));
        return Map.of("state", server == null && occupied && !busy ? "EXTERNAL" : phase,
                "message", server == null && occupied && !busy ? "端口 " + port + " 已被其他服务占用" : message,
                "pid", server == null ? 0L : server.pid(), "canStart", available && !busy && server == null && !occupied,
                "canStop", !phase.equals("STOPPING") && (server != null || busy), "url", "http://localhost:" + port + (name.equals("blog") ? "/api" : ""), "distribution", distribution);
    }

    public synchronized Map<String, Object> start() {
        if (!Boolean.TRUE.equals(status().get("canStart"))) throw new IllegalArgumentException("无法启动 " + label + "，请检查 Windows / WSL 环境及端口 " + port);
        phase = "BUILDING"; message = "正在检查 " + label + "构建缓存…";
        long token = ++generation;
        Thread.ofVirtual().name(name + "-backend-start").start(() -> launch(token));
        return status();
    }

    private void launch(long token) {
        try {
            Process building;
            boolean rebuild = BuildArtifacts.needsBuild(root, name);
            synchronized (this) {
                if (token != generation) return;
                Files.createDirectories(root.resolve("build/blog-runtime"));
                message = rebuild ? "正在增量构建 " + label + "…" : "复用已有 JAR，正在启动 " + label + "…";
                var args = new ArrayList<>(List.of("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File",
                        root.resolve("scripts/start-services-local.ps1").toString(), "-Services", name,
                        switch (name) { case "uaa" -> "-UaaDebugPort"; case "gateway" -> "-GatewayDebugPort"; default -> "-BlogDebugPort"; }, "0", "-Distribution", distribution));
                if (rebuild) args.add("-Build");
                launcher = new ProcessBuilder(args)
                        .directory(root.toFile()).redirectErrorStream(true).redirectOutput(root.resolve("build/blog-runtime/" + name + "-launch.log").toFile()).start();
                building = launcher;
            }
            int exit = building.waitFor();
            synchronized (this) {
                if (token != generation) return;
                launcher = null; server = discover(false).orElse(null); owned = server != null;
                if (exit != 0) throw new IOException("后端启动脚本失败（" + exit + "），请查看日志");
                if (rebuild) BuildArtifacts.record(root, name);
                if (server == null) throw new IOException("未找到脚本启动的 " + label + "进程");
                phase = "STARTING"; message = label + "正在启动，等待端口 " + port + "…";
            }
        } catch (Exception e) {
            synchronized (this) { if (token == generation) { phase = "FAILED"; message = e.getMessage(); } }
        }
    }

    public synchronized Map<String, Object> stop() {
        if (phase.equals("STOPPING")) return status();
        status();
        if (server == null && launcher == null && !Set.of("BUILDING", "STARTING").contains(phase)) return status();
        ++generation;
        Process launching = launcher; ProcessHandle stopping = server;
        phase = "STOPPING"; message = "正在停止 " + label + "…";
        Thread.ofVirtual().name(name + "-backend-stop").start(() -> {
            if (launching != null) LabLifecycle.terminate(launching);
            ProcessHandle target = stopping == null ? discover(false).orElse(null) : stopping;
            if (target != null) BlogLifecycle.terminate(target);
            synchronized (this) {
                if (target != null && target.isAlive()) { phase = "FAILED"; message = "停止后端失败，请检查日志"; }
                else { server = null; launcher = null; owned = false; phase = "STOPPED"; message = label + "已停止"; }
            }
        });
        return status();
    }

    public Map<String, String> logs() throws IOException {
        StringBuilder body = new StringBuilder();
        for (String path : List.of("build/blog-runtime/" + name + "-launch.log", "logs/" + name + "-local/stdout.log", "logs/" + name + "-local/stderr.log")) {
            Path file = root.resolve(path);
            if (Files.exists(file)) try (var reader = new java.io.RandomAccessFile(file.toFile(), "r")) {
                reader.seek(Math.max(0, reader.length() - 16384)); byte[] bytes = new byte[(int)(reader.length() - reader.getFilePointer())]; reader.readFully(bytes);
                body.append(path).append('\n').append(new String(bytes, java.nio.charset.StandardCharsets.UTF_8)).append('\n');
            }
        }
        return Map.of("body", body.isEmpty() ? "尚无后端日志" : body.toString());
    }

    @PreDestroy public synchronized void shutdown() {
        ++generation;
        if (launcher != null) {
            LabLifecycle.terminate(launcher);
            if (server == null) server = discover(false).orElse(null);
            owned = server != null;
        }
        if (owned && server != null) BlogLifecycle.terminate(server);
    }
}
