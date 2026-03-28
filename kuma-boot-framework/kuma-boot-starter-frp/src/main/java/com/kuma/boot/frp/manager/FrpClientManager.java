package com.kuma.boot.frp.manager;

import com.kuma.boot.frp.autoconfigure.properties.FrpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * FRP 客户端进程管理器
 *
 * <p>职责：
 * <ol>
 *   <li>从 classpath 释放内置 frpc.exe（首次运行）</li>
 *   <li>根据 {@link FrpProperties} 动态生成 frpc TOML 配置文件</li>
 *   <li>启动 frpc 子进程，将 stdout/stderr 转发到应用日志</li>
 *   <li>后台线程监控进程存活，崩溃后按配置自动重启</li>
 *   <li>ApplicationContext 关闭时优雅终止进程</li>
 * </ol>
 *
 * @author kuma
 */
public class FrpClientManager implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(FrpClientManager.class);

    /** classpath 内置资源目录 */
    private static final String CLASSPATH_FRPC_DIR = "frpc";

    /** Windows 内置二进制文件名 */
    private static final String FRPC_WINDOWS_EXE = "frpc-windows-amd64.exe";

    private final FrpProperties properties;

    private volatile Process frpcProcess;
    private volatile boolean running = false;
    private volatile String resolvedFrpcPath;
    private volatile Path configFilePath;

    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private ScheduledExecutorService watchdog;

    public FrpClientManager(FrpProperties properties) {
        this.properties = properties;
    }

    // ── SmartLifecycle ──────────────────────────────────────────────────────

    @Override
    public void start() {
        stopped.set(false);
        try {
            resolvedFrpcPath = resolveFrpcExecutable();
            configFilePath = generateTomlConfig();
            startProcess();
            running = true;
            if (properties.isAutoRestart()) {
                startWatchdog();
            }
            log.info("[FRP] frpc 已启动，配置: {}", configFilePath);
        } catch (Exception e) {
            log.error("[FRP] frpc 启动失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        stopped.set(true);
        running = false;
        shutdownWatchdog();
        killProcess();
        deleteConfigFile();
        log.info("[FRP] frpc 已停止");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /** 在 Web 容器启动后再启动，避免阻塞主流程 */
    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 100;
    }

    // ── 核心逻辑 ─────────────────────────────────────────────────────────────

    /**
     * 解析 frpc 可执行文件路径：
     * 1. 用户配置的 frpcPath
     * 2. toolDir 下已有的 frpc.exe
     * 3. 从 classpath 内置资源释放
     */
    private String resolveFrpcExecutable() throws IOException {
        // 用户显式指定路径
        if (StringUtils.hasText(properties.getFrpcPath())) {
            File f = new File(properties.getFrpcPath());
            if (!f.exists()) {
                throw new FileNotFoundException("配置的 frpc 路径不存在: " + properties.getFrpcPath());
            }
            return f.getAbsolutePath();
        }

        Path toolDir = resolveToolDir();
        Files.createDirectories(toolDir);

        // 根据 OS 选择二进制名称
        String exeName = resolveExeName();
        Path exePath = toolDir.resolve(exeName);

        if (Files.exists(exePath)) {
            log.debug("[FRP] 使用已有 frpc: {}", exePath);
            return exePath.toAbsolutePath().toString();
        }

        // 从 classpath 内置资源释放
        return extractFromClasspath(toolDir, exeName);
    }

    private String resolveExeName() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            return FRPC_WINDOWS_EXE;
        }
        String arch = System.getProperty("os.arch", "").toLowerCase();
        String archSuffix = arch.contains("aarch64") || arch.contains("arm64") ? "arm64" : "amd64";
        if (os.contains("mac")) {
            return "frpc-darwin-" + archSuffix;
        }
        return "frpc-linux-" + archSuffix;
    }

    private String extractFromClasspath(Path toolDir, String exeName) throws IOException {
        String resourcePath = CLASSPATH_FRPC_DIR + "/" + exeName;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new FileNotFoundException(
                    "classpath 中未找到内置 frpc: " + resourcePath +
                    "，请通过 kuma.boot.frp.frpc-path 指定外部路径");
            }
            Path dest = toolDir.resolve(exeName);
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            dest.toFile().setExecutable(true);
            log.info("[FRP] 已从 classpath 释放 frpc 到: {}", dest);
            return dest.toAbsolutePath().toString();
        }
    }

    private Path resolveToolDir() {
        if (StringUtils.hasText(properties.getToolDir())) {
            return Paths.get(properties.getToolDir());
        }
        return Paths.get(System.getProperty("user.home"), ".kuma", "frp");
    }

    /**
     * 根据 FrpProperties 动态生成 frpc TOML 配置文件
     */
    private Path generateTomlConfig() throws IOException {
        Path toolDir = resolveToolDir();
        Files.createDirectories(toolDir);
        Path configPath = toolDir.resolve("frpc.toml");

        StringBuilder sb = new StringBuilder();

        // 服务端连接
        sb.append("serverAddr = \"").append(properties.getServerAddr()).append("\"\n");
        sb.append("serverPort = ").append(properties.getServerPort()).append("\n\n");

        // 认证
        FrpProperties.Auth auth = properties.getAuth();
        sb.append("[auth]\n");
        sb.append("method = \"").append(auth.getMethod()).append("\"\n");
        if (StringUtils.hasText(auth.getToken())) {
            sb.append("token = \"").append(auth.getToken()).append("\"\n");
        }
        sb.append("\n");

        // 日志
        FrpProperties.Log logCfg = properties.getLog();
        sb.append("[log]\n");
        sb.append("level = \"").append(logCfg.getLevel()).append("\"\n");
        sb.append("maxDays = ").append(logCfg.getMaxDays()).append("\n");
        sb.append("disableColor = ").append(logCfg.isDisableColor()).append("\n\n");

        // 代理
        for (FrpProperties.Proxy proxy : properties.getProxies()) {
            sb.append("[[proxies]]\n");
            sb.append("name = \"").append(proxy.getName()).append("\"\n");
            sb.append("type = \"").append(proxy.getType()).append("\"\n");
            sb.append("localIP = \"").append(proxy.getLocalIp()).append("\"\n");
            sb.append("localPort = ").append(proxy.getLocalPort()).append("\n");

            String type = proxy.getType().toLowerCase();
            if ("tcp".equals(type) || "udp".equals(type)) {
                sb.append("remotePort = ").append(proxy.getRemotePort()).append("\n");
            }
            if (StringUtils.hasText(proxy.getCustomDomains())) {
                sb.append("customDomains = [");
                String[] domains = proxy.getCustomDomains().split(",");
                List<String> quoted = new ArrayList<>();
                for (String d : domains) {
                    quoted.add("\"" + d.trim() + "\"");
                }
                sb.append(String.join(", ", quoted));
                sb.append("]\n");
            }
            if (StringUtils.hasText(proxy.getSubdomain())) {
                sb.append("subdomain = \"").append(proxy.getSubdomain()).append("\"\n");
            }
            if (StringUtils.hasText(proxy.getLocations())) {
                sb.append("locations = [\"").append(proxy.getLocations()).append("\"]\n");
            }
            sb.append("transport.useEncryption = ").append(proxy.isUseEncryption()).append("\n");
            sb.append("transport.useCompression = ").append(proxy.isUseCompression()).append("\n");
            sb.append("\n");
        }

        Files.writeString(configPath, sb.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        log.debug("[FRP] 生成配置文件:\n{}", sb);
        return configPath;
    }

    /**
     * 启动 frpc 子进程
     */
    private synchronized void startProcess() throws IOException {
        List<String> cmd = List.of(resolvedFrpcPath, "-c", configFilePath.toAbsolutePath().toString());
        log.info("[FRP] 执行命令: {}", String.join(" ", cmd));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(false);
        frpcProcess = pb.start();

        // stdout → INFO
        pipeStream(frpcProcess.getInputStream(), false);
        // stderr → WARN
        pipeStream(frpcProcess.getErrorStream(), true);
    }

    private void pipeStream(InputStream stream, boolean isError) {
        CompletableFuture.runAsync(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isError) {
                        log.warn("[FRP] {}", line);
                    } else {
                        log.info("[FRP] {}", line);
                    }
                }
            } catch (IOException e) {
                if (!stopped.get()) {
                    log.debug("[FRP] 流读取结束: {}", e.getMessage());
                }
            }
        });
    }

    // ── Watchdog ──────────────────────────────────────────────────────────

    private void startWatchdog() {
        watchdog = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "frp-watchdog");
            t.setDaemon(true);
            return t;
        });
        watchdog.scheduleWithFixedDelay(this::checkAndRestart, 5, 5, TimeUnit.SECONDS);
    }

    private void checkAndRestart() {
        if (stopped.get()) return;
        Process p = frpcProcess;
        if (p != null && !p.isAlive()) {
            log.warn("[FRP] frpc 进程已退出（exit={}），{}ms 后重启...",
                    p.exitValue(), properties.getRestartDelayMs());
            try {
                Thread.sleep(properties.getRestartDelayMs());
                if (!stopped.get()) {
                    startProcess();
                    log.info("[FRP] frpc 已重启");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                log.error("[FRP] frpc 重启失败: {}", e.getMessage(), e);
            }
        }
    }

    private void shutdownWatchdog() {
        if (watchdog != null) {
            watchdog.shutdownNow();
            watchdog = null;
        }
    }

    // ── 清理 ─────────────────────────────────────────────────────────────

    private void killProcess() {
        Process p = frpcProcess;
        if (p != null && p.isAlive()) {
            p.destroy();
            try {
                if (!p.waitFor(5, TimeUnit.SECONDS)) {
                    p.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                p.destroyForcibly();
            }
        }
        frpcProcess = null;
    }

    private void deleteConfigFile() {
        if (configFilePath != null) {
            try {
                Files.deleteIfExists(configFilePath);
            } catch (IOException e) {
                log.debug("[FRP] 删除配置文件失败: {}", e.getMessage());
            }
        }
    }
}
