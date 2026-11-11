package com.kuma.cloud.console;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;

@Service
public class DshLifecycle {
    private final Path directory, log;
    private final int port;
    private ProcessHandle process;
    private boolean stopping;
    private final HttpClient http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofMillis(500)).build();

    public DshLifecycle(@Value("${console.dsh.directory:}") String directory,
                        @Value("${console.dsh.port:3080}") int port,
                        @Value("${console.workspace:}") String workspace) {
        this.directory = directory.isBlank() ? null : Path.of(directory).toAbsolutePath().normalize();
        if (port < 1024 || port > 65535) throw new IllegalArgumentException("dsh 端口必须在 1024 到 65535 之间");
        this.port = port;
        Path root = workspace.isBlank() ? LabLifecycle.findWorkspace(Path.of(System.getProperty("user.dir"))) : Path.of(workspace);
        log = root.resolve("build/dsh-runtime/dsh.log").toAbsolutePath();
    }

    private String url() { return "http://127.0.0.1:" + port; }

    private String launchUrl() {
        try {
            var match = java.util.regex.Pattern.compile(java.util.regex.Pattern.quote(url()) + "/\\?token=[A-Za-z0-9_-]+")
                    .matcher(logs().get("body"));
            String latest = url();
            while (match.find()) latest = match.group();
            return latest;
        } catch (IOException ignored) { /* No startup log is available yet. */ }
        return url();
    }

    private String probe() {
        try {
            var request = HttpRequest.newBuilder(URI.create(launchUrl())).timeout(Duration.ofSeconds(1)).GET().build();
            var response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 303 && response.headers().allValues("set-cookie").stream().anyMatch(c -> c.startsWith("dsh-auth-"))) return "RUNNING";
            if (response.statusCode() == 401 && response.body().contains("dsh web authentication required")) return "AUTH_REQUIRED";
            return response.statusCode() == 200 && response.body().contains("__DSH_BOOT__") ? "RUNNING" : "EXTERNAL";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return "STOPPED";
        } catch (IOException e) { return "STOPPED"; }
    }

    List<String> launchCommand() {
        // Same public dsh launcher as the checkout's `pnpm dsh` script.
        return List.of("node", "--import", "tsx/esm", directory.resolve("apps/cli/src/bin.ts").toString(),
                "web", "--host", "127.0.0.1", "--port", Integer.toString(port), "--no-open");
    }

    boolean matches(String executable, String command) {
        if (directory == null) return false;
        String name = executable.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1);
        String normalized = command.replace('\\', '/');
        String cli = directory.resolve("apps/cli/src/bin.ts").toString().replace('\\', '/');
        boolean configuredPort = normalized.matches("(?s).*\\s--port(?:\\s+|=)" + port + "(?:\\s.*|$)")
                || port == 3080 && !normalized.contains("--port");
        return Set.of("node", "node.exe").contains(name.toLowerCase(Locale.ROOT))
                && java.util.regex.Pattern.compile("(?:^|\\s)[\"']?" + java.util.regex.Pattern.quote(cli)
                        + "[\"']?\\s+web(?:\\s|$)", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(normalized).find()
                && configuredPort;
    }

    private Optional<ProcessHandle> discover() {
        if (directory == null) return Optional.empty();
        if (!System.getProperty("os.name").startsWith("Windows")) {
            return ProcessHandle.allProcesses().filter(p -> matches(p.info().command().orElse(""),
                    p.info().commandLine().orElse(""))).findFirst();
        }
        var result = new CommandRunner().run(List.of("powershell", "-NoProfile", "-Command",
                "$listeners = @(Get-NetTCPConnection -State Listen -LocalPort " + port
                        + " -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess); "
                        + "Get-CimInstance Win32_Process | Where-Object { $_.Name -eq 'node.exe' -and $_.ProcessId -in $listeners } "
                        + "| Select-Object ProcessId,Name,CommandLine | ConvertTo-Json -Compress"), Duration.ofSeconds(4));
        if (!result.success() || result.output().isBlank()) return Optional.empty();
        try {
            var mapper = new tools.jackson.databind.ObjectMapper();
            var json = mapper.readTree(result.output());
            var rows = json.isArray() ? json : mapper.createArrayNode().add(json);
            for (var row : rows) {
                if (matches(row.path("Name").asString(), row.path("CommandLine").asString())) {
                    var handle = ProcessHandle.of(row.path("ProcessId").asLong()).filter(ProcessHandle::isAlive);
                    if (handle.isPresent()) return handle;
                }
            }
        } catch (Exception ignored) { }
        return Optional.empty();
    }

    public synchronized Map<String, Object> status() {
        String state = probe();
        boolean alive = process != null && process.isAlive();
        if (!alive && !stopping && Set.of("RUNNING", "AUTH_REQUIRED").contains(state)) {
            process = discover().orElse(null);
            alive = process != null && process.isAlive();
        }
        boolean installed = directory != null && Files.isRegularFile(directory.resolve("apps/cli/src/bin.ts"))
                && Files.isDirectory(directory.resolve("node_modules/tsx"));
        if (state.equals("STOPPED") && alive) state = "STARTING";
        if (stopping) state = "STOPPING";
        String message = switch (state) {
            case "RUNNING" -> "dsh 已就绪";
            case "STARTING" -> "正在启动 dsh…";
            case "STOPPING" -> "正在停止 dsh…";
            case "EXTERNAL" -> "端口 " + port + " 的服务不是 dsh Web UI";
            case "AUTH_REQUIRED" -> "dsh 已运行，可打开工作台使用当前浏览器的登录会话；若页面提示认证失败，请使用本次启动的登录链接";
            default -> !installed ? "未找到 dsh 源码或依赖，请检查目录配置" : process == null ? "dsh 尚未启动" : "dsh 已退出，请查看启动日志";
        };
        return Map.of("state", state, "message", message, "directory", directory == null ? "" : directory.toString(),
                // The server probe has no browser cookies. A 401 cannot tell us whether
                // the user's browser is signed in. Omit a rejected/stale launch token
                // so an existing browser session can authenticate the clean URL.
                "url", state.equals("AUTH_REQUIRED") ? url() : launchUrl(), "canStart", installed && !alive && state.equals("STOPPED"),
                "canStop", alive && !stopping, "canOpen", Set.of("RUNNING", "AUTH_REQUIRED").contains(state), "logPath", log.toString());
    }

    public synchronized Map<String, Object> start() throws IOException {
        if (!Boolean.TRUE.equals(status().get("canStart"))) throw new IllegalArgumentException("当前无法启动 dsh，请检查目录、依赖或端口");
        Files.createDirectories(log.getParent());
        process = new ProcessBuilder(launchCommand()).directory(directory.toFile())
                .redirectErrorStream(true).redirectOutput(log.toFile()).start().toHandle();
        return status();
    }

    public synchronized Map<String, Object> stop() {
        if (stopping) return status();
        status();
        if (process != null && process.isAlive()) {
            ProcessHandle target = process;
            stopping = true;
            Thread.ofVirtual().name("dsh-stop").start(() -> {
                try { BlogLifecycle.terminate(target); }
                finally { synchronized (this) { stopping = false; } }
            });
        }
        return status();
    }

    public Map<String, String> logs() throws IOException {
        if (!Files.exists(log)) return Map.of("body", "尚无 dsh 启动日志");
        try (var reader = new RandomAccessFile(log.toFile(), "r")) {
            reader.seek(Math.max(0, reader.length() - 32_768));
            byte[] tail = new byte[(int) (reader.length() - reader.getFilePointer())];
            reader.readFully(tail);
            return Map.of("body", new String(tail, StandardCharsets.UTF_8));
        }
    }
}
