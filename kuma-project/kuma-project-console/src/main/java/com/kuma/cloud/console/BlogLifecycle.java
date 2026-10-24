package com.kuma.cloud.console;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class BlogLifecycle {
    private final Path directory;
    private final Path runtime;
    private final int port;
    private final CommandRunner commands;
    private final ObjectMapper mapper;
    private ProcessHandle process;
    private boolean frontendReady, probing;
    private final HttpClient http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(2))
            .followRedirects(HttpClient.Redirect.NORMAL).build();
    private Process synchronizing;
    private long generation;
    private String knowledgeState = "IDLE", knowledgeMessage = "启动 Blog 时自动同步知识仓库";
    private boolean owned;
    private String phase = "STOPPED", message = "Blog 尚未启动";
    private Path log;

    public BlogLifecycle(@Value("${console.workspace:}") String workspace,
                         @Value("${console.blog.directory:}") String directory,
                         @Value("${console.blog.port:4321}") int port, CommandRunner commands, ObjectMapper mapper) {
        Path root = workspace.isBlank() ? LabLifecycle.findWorkspace(Path.of(System.getProperty("user.dir"))) : Path.of(workspace).toAbsolutePath().normalize();
        this.directory = directory.isBlank() ? root.resolveSibling("kuma-blog-fronted").resolve("KumaBlog") : Path.of(directory).toAbsolutePath().normalize();
        runtime = root.resolve("build/blog-runtime");
        if (port < 1024 || port > 65535) throw new IllegalArgumentException("Blog 端口必须在 1024 到 65535 之间");
        this.port = port; this.commands = commands; this.mapper = mapper;
    }

    private boolean reachable() {
        for (String host : List.of("127.0.0.1", "::1")) {
            try (Socket socket = new Socket()) { socket.connect(new InetSocketAddress(host, port), 150); return true; }
            catch (IOException ignored) { }
        }
        return false;
    }

    boolean matches(String executable, String command) {
        String normalized = command.replace('\\', '/').toLowerCase(Locale.ROOT);
        String root = directory.toString().replace('\\', '/').toLowerCase(Locale.ROOT) + "/";
        String name = executable.replace('\\', '/'); name = name.substring(name.lastIndexOf('/') + 1);
        return Set.of("node", "node.exe").contains(name.toLowerCase(Locale.ROOT)) && normalized.contains(root)
                && normalized.contains("/astro/astro.js") && normalized.matches("(?s).*\\sdev(?:\\s.*)?$");
    }

    private Optional<ProcessHandle> discover() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            var result = commands.run(List.of("powershell", "-NoProfile", "-Command",
                    "$listeners = @(Get-NetTCPConnection -State Listen -LocalPort " + port + " -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess); "
                    + "Get-CimInstance Win32_Process | Where-Object { $_.Name -eq 'node.exe' -and $_.ProcessId -in $listeners } | Select-Object ProcessId,Name,CommandLine | ConvertTo-Json -Compress"), Duration.ofSeconds(4));
            if (!result.success() || result.output().isBlank()) return Optional.empty();
            try {
                var json = mapper.readTree(result.output());
                var rows = json.isArray() ? json : mapper.createArrayNode().add(json);
                for (var row : rows) if (matches(row.path("Name").asString(), row.path("CommandLine").asString())) {
                    var handle = ProcessHandle.of(row.path("ProcessId").asLong());
                    if (handle.filter(ProcessHandle::isAlive).isPresent()) return handle;
                }
            } catch (Exception ignored) { }
            return Optional.empty();
        }
        return ProcessHandle.allProcesses().filter(p -> matches(p.info().command().orElse(""), p.info().commandLine().orElse(""))).findFirst();
    }

    // Astro can listen before the first page has compiled. Prepare that page
    // outside the status lock, so polling and Stop remain responsive.
    private void preparePreview() {
        if (process == null || frontendReady || probing || phase.equals("STOPPING")) return;
        ProcessHandle candidate = process;
        probing = true;
        Thread.ofVirtual().name("blog-preview-ready").start(() -> {
            while (candidate.isAlive()) {
                synchronized (this) { if (process != candidate || phase.equals("STOPPING")) return; }
                boolean ready = false;
                try {
                    var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/"))
                            .timeout(Duration.ofSeconds(45)).GET().build();
                    var response = http.send(request, HttpResponse.BodyHandlers.discarding());
                    ready = response.statusCode() >= 200 && response.statusCode() < 300;
                } catch (Exception ignored) { }
                synchronized (this) {
                    if (process != candidate || phase.equals("STOPPING")) return;
                    if (ready) {
                        frontendReady = true; probing = false;
                        phase = "RUNNING"; message = "Blog 页面已就绪";
                        return;
                    }
                }
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
            synchronized (this) { if (process == candidate) probing = false; }
        });
    }

    public synchronized Map<String, Object> status() {
        if (process != null && !process.isAlive() && !phase.equals("STOPPING")) {
            process = null; frontendReady = false; probing = false; phase = "STOPPED"; message = "Blog 已退出，请查看日志";
        }
        boolean occupied = reachable();
        if (process == null && occupied && !Set.of("STOPPING", "SYNCING").contains(phase)) {
            discover().ifPresent(p -> { process = p; owned = false; frontendReady = false; probing = false; phase = "STARTING"; message = "正在准备 Blog 页面…"; });
        }
        preparePreview();
        boolean external = process == null && occupied;
        boolean ready = Files.exists(directory.resolve("package.json")) && Files.exists(directory.resolve("node_modules/astro/astro.js"));
        boolean syncBusy = knowledgeState.equals("SYNCING");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("state", external ? "EXTERNAL" : phase);
        result.put("message", external ? "端口 " + port + " 已被其他服务占用" : !ready && process == null ? "未找到 Blog 或 Astro 依赖，请检查项目目录并执行 pnpm install" : message);
        result.put("pid", process == null ? 0L : process.pid());
        result.put("canStart", ready && process == null && !occupied && !syncBusy && !phase.equals("STOPPING"));
        result.put("canStop", (process != null || syncBusy) && !phase.equals("STOPPING"));
        result.put("canSync", Files.exists(directory.resolve("scripts/sync-knowledge.mjs")) && !syncBusy && !Set.of("STOPPING", "STARTING").contains(phase));
        result.put("knowledgeState", knowledgeState); result.put("knowledgeMessage", knowledgeMessage);
        result.put("knowledgePid", synchronizing == null ? 0L : synchronizing.pid());
        result.put("knowledgeUrl", "http://localhost:" + port + "/knowledge");
        result.put("canOpen", process != null && occupied && frontendReady && phase.equals("RUNNING"));
        result.put("url", "http://localhost:" + port); result.put("directory", directory.toString());
        result.put("logPath", log == null ? "" : log.toString());
        return result;
    }

    public synchronized Map<String, Object> start() {
        if (!Boolean.TRUE.equals(status().get("canStart"))) throw new IllegalArgumentException("当前状态无法启动 Blog，请检查项目、依赖和端口");
        return beginSync(true);
    }

    public synchronized Map<String, Object> syncKnowledge() {
        if (!Boolean.TRUE.equals(status().get("canSync"))) throw new IllegalArgumentException("当前无法同步知识仓库，请等待当前任务完成或检查同步脚本");
        return beginSync(false);
    }

    private Map<String, Object> beginSync(boolean startAfterSync) {
        if (!Files.exists(directory.resolve("scripts/sync-knowledge.mjs"))) throw new IllegalArgumentException("未找到知识仓库同步脚本 scripts/sync-knowledge.mjs");
        knowledgeState = "SYNCING"; knowledgeMessage = "正在构建并同步知识仓库…";
        if (startAfterSync) { phase = "SYNCING"; message = "正在同步知识仓库，完成后启动 Blog…"; }
        long token = ++generation;
        Thread.ofVirtual().name("blog-knowledge-sync").start(() -> synchronizeKnowledge(token, startAfterSync));
        return status();
    }

    private void synchronizeKnowledge(long token, boolean startAfterSync) {
        try {
            Process build;
            synchronized (this) {
                if (token != generation) return;
                Files.createDirectories(runtime);
                log = runtime.resolve("blog-" + UUID.randomUUID() + ".log");
                synchronizing = new ProcessBuilder("node", directory.resolve("scripts/sync-knowledge.mjs").toString())
                        .directory(directory.toFile()).redirectErrorStream(true).redirectOutput(log.toFile()).start();
                build = synchronizing;
            }
            int exit = build.waitFor();
            synchronized (this) {
                if (token != generation) return;
                synchronizing = null;
                if (exit != 0) throw new IOException("知识仓库同步失败（" + exit + "），请查看日志");
                knowledgeState = "SUCCESS"; knowledgeMessage = "知识仓库已同步";
                if (startAfterSync) launchFrontend();
            }
        } catch (Exception e) {
            synchronized (this) {
                if (token != generation) return;
                synchronizing = null; knowledgeState = "FAILED"; knowledgeMessage = e.getMessage();
                if (startAfterSync) { phase = "FAILED"; message = "知识仓库同步失败，Blog 未启动，请查看日志"; }
            }
        }
    }

    private void launchFrontend() {
        try {
            if (reachable()) throw new IOException("Blog 端口已被占用");
            Process launched = new ProcessBuilder("node", directory.resolve("node_modules/astro/astro.js").toString(), "dev", "--host", "127.0.0.1", "--port", Integer.toString(port))
                    .directory(directory.toFile()).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.appendTo(log.toFile())).start();
            process = launched.toHandle(); owned = true; frontendReady = false; probing = false;
            phase = "STARTING"; message = "正在启动并准备 Blog 页面…";
        } catch (IOException e) { phase = "FAILED"; message = "Blog 启动失败：" + e.getMessage(); }
    }

    public synchronized Map<String, Object> stop() {
        if (phase.equals("STOPPING")) return status();
        status();
        if (process == null && !knowledgeState.equals("SYNCING")) return status();
        ++generation;
        ProcessHandle stopping = process;
        Process sync = synchronizing;
        phase = "STOPPING"; message = "正在停止 Blog 和知识仓库构建…";
        Thread.ofVirtual().name("blog-stop").start(() -> {
            if (sync != null) LabLifecycle.terminate(sync);
            if (stopping != null) terminate(stopping);
            synchronized (this) {
                if ((stopping != null && stopping.isAlive()) || (sync != null && sync.isAlive())) { phase = "FAILED"; message = "停止失败，请检查日志和进程"; }
                else {
                    process = null; synchronizing = null; owned = false; frontendReady = false; probing = false; phase = "STOPPED"; message = "Blog 和知识仓库页面已关闭";
                    if (knowledgeState.equals("SYNCING")) { knowledgeState = "CANCELLED"; knowledgeMessage = "知识仓库同步已取消"; }
                }
            }
        });
        return status();
    }

    static void terminate(ProcessHandle process) {
        var children = process.descendants().toList(); children.forEach(ProcessHandle::destroy); process.destroy();
        try { process.onExit().get(5, TimeUnit.SECONDS); } catch (Exception ignored) { }
        children.stream().filter(ProcessHandle::isAlive).forEach(ProcessHandle::destroyForcibly);
        if (process.isAlive()) process.destroyForcibly();
        try { process.onExit().get(2, TimeUnit.SECONDS); } catch (Exception ignored) { }
    }

    public synchronized Map<String, String> logs() throws IOException {
        if (log == null || !Files.exists(log)) return Map.of("body", "尚无控制台启动日志；已有进程的日志请查看原启动终端");
        try (RandomAccessFile file = new RandomAccessFile(log.toFile(), "r")) {
            file.seek(Math.max(0, file.length() - 32768)); byte[] bytes = new byte[(int) (file.length() - file.getFilePointer())]; file.readFully(bytes);
            return Map.of("body", new String(bytes, StandardCharsets.UTF_8));
        }
    }

    @PreDestroy public synchronized void shutdown() {
        ++generation;
        if (synchronizing != null) LabLifecycle.terminate(synchronizing);
        if (owned && process != null) terminate(process);
    }
}
