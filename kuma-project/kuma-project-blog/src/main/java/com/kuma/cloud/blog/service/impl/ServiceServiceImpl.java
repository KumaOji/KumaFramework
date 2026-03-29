package com.kuma.cloud.blog.service.impl;

import com.kuma.cloud.blog.service.ServiceService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * 服务管理服务实现类
 *
 * @author Kuma
 */
@Service
@SuppressWarnings("all")
public class ServiceServiceImpl implements ServiceService, ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);

    private final Executor asyncExecutor;

    /** LibreTranslate 进程引用 */
    private Process libreTranslateProcess;

    @Value("${python.libreTranslate.enabled:false}")
    private boolean libreTranslateEnabled;

    @Value("${python.libreTranslate.path:libretranslate}")
    private String libreTranslatePath;

    @Value("${python.libreTranslate.host:0.0.0.0}")
    private String libreTranslateHost;

    @Value("${python.libreTranslate.port:5000}")
    private int libreTranslatePort;

    @Value("${python.libreTranslate.load-only:en,zh}")
    private String libreTranslateLoadOnly;

    @Value("${python.libreTranslate.threads:2}")
    private int libreTranslateThreads;

    @Value("${python.libreTranslate.update-models:false}")
    private boolean libreTranslateUpdateModels;

    public ServiceServiceImpl(@Qualifier("asyncThreadPoolTaskExecutor") Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (libreTranslateEnabled) {
            logger.info("配置了自动启动 LibreTranslate 服务，正在启动...");
            startLibreTranslate();
        }
    }

    @PreDestroy
    public void onDestroy() {
        logger.info("Spring Boot 正在关闭，开始清理 LibreTranslate 服务进程...");
        if (libreTranslateProcess != null) {
            destroyProcessTree(libreTranslateProcess, "LibreTranslate");
            libreTranslateProcess = null;
        }
        logger.info("LibreTranslate 服务进程清理完成");
    }

    @Override
    public Map<String, Object> startLibreTranslate() {
        if (isLibreTranslateRunning()) {
            Map<String, Object> status = new HashMap<>();
            status.put("running", true);
            status.put("host", libreTranslateHost);
            status.put("port", libreTranslatePort);
            status.put("pid", getProcessId(libreTranslateProcess));
            status.put("message", "LibreTranslate 服务已在运行");
            return status;
        }

        CompletableFuture.runAsync(() -> {
            try {
                logger.info("正在异步启动 LibreTranslate 服务...");

                // 如果路径包含路径分隔符，则视为文件路径并校验；否则视为 PATH 中的命令名，直接使用
                boolean isFilePath = libreTranslatePath.contains(File.separator) || libreTranslatePath.contains("/");
                if (isFilePath) {
                    File libreTranslateExe = new File(libreTranslatePath);
                    if (!libreTranslateExe.exists() || !libreTranslateExe.canExecute()) {
                        logger.error("LibreTranslate 可执行文件不存在或不可执行: {}", libreTranslatePath);
                        return;
                    }
                }

                List<String> command = new ArrayList<>();
                command.add(libreTranslatePath);
                command.add("--host");
                command.add(libreTranslateHost);
                command.add("--port");
                command.add(String.valueOf(libreTranslatePort));
                command.add("--load-only");
                command.add(libreTranslateLoadOnly);
                command.add("--threads");
                command.add(String.valueOf(libreTranslateThreads));
                command.add("--disable-web-ui");

                if (libreTranslateUpdateModels) {
                    command.add("--update-models");
                    logger.info("已启用自动更新语言模型，首次启动可能需要较长时间下载...");
                }

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                libreTranslateProcess = processBuilder.start();
                Thread.sleep(2000);
                if (libreTranslateProcess.isAlive()) {
                    logger.info("LibreTranslate 服务启动成功，PID: {}", getProcessId(libreTranslateProcess));
                } else {
                    int exitCode = libreTranslateProcess.exitValue();
                    logger.error("LibreTranslate 服务启动失败，退出码: {}", exitCode);
                    libreTranslateProcess = null;
                }
            } catch (IOException e) {
                logger.error("启动 LibreTranslate 服务时发生 IO 异常: {}", e.getMessage(), e);
                libreTranslateProcess = null;
            } catch (InterruptedException e) {
                logger.error("启动 LibreTranslate 服务时被中断: {}", e.getMessage(), e);
                Thread.currentThread().interrupt();
                libreTranslateProcess = null;
            } catch (Exception e) {
                logger.error("启动 LibreTranslate 服务时发生异常: {}", e.getMessage(), e);
                libreTranslateProcess = null;
            }
        }, asyncExecutor).exceptionally(throwable -> {
            logger.error("异步启动 LibreTranslate 服务时发生异常: {}", throwable.getMessage(), throwable);
            return null;
        });

        Map<String, Object> result = new HashMap<>();
        result.put("message", "LibreTranslate 服务启动中，请稍后查询状态");
        result.put("host", libreTranslateHost);
        result.put("port", libreTranslatePort);
        return result;
    }

    @Override
    public String stopLibreTranslate() {
        if (!isLibreTranslateRunning()) {
            return "服务未运行";
        }

        try {
            logger.info("停止 LibreTranslate 服务，PID: {}", getProcessId(libreTranslateProcess));
            destroyProcessTree(libreTranslateProcess, "LibreTranslate");
            libreTranslateProcess = null;
            return "服务已关闭";
        } catch (Exception e) {
            logger.error("关闭 LibreTranslate 服务时发生异常: {}", e.getMessage(), e);
            if (libreTranslateProcess != null && libreTranslateProcess.isAlive()) {
                try {
                    libreTranslateProcess.toHandle().descendants().forEach(d -> {
                        try { d.destroyForcibly(); } catch (Exception ex) { /* 忽略 */ }
                    });
                    libreTranslateProcess.destroyForcibly();
                } catch (Exception ex) {
                    logger.error("强制关闭 LibreTranslate 服务时出错: {}", ex.getMessage());
                }
            }
            libreTranslateProcess = null;
            return "关闭服务失败: " + e.getMessage();
        }
    }

    @Override
    public Map<String, Object> getLibreTranslateStatus() {
        Map<String, Object> status = new HashMap<>();
        boolean running = isLibreTranslateRunning();
        status.put("running", running);
        status.put("enabled", libreTranslateEnabled);
        status.put("host", libreTranslateHost);
        status.put("path", libreTranslatePath);
        status.put("port", libreTranslatePort);
        status.put("loadOnly", libreTranslateLoadOnly);
        status.put("threads", libreTranslateThreads);
        status.put("updateModels", libreTranslateUpdateModels);
        if (running && libreTranslateProcess != null) {
            status.put("pid", getProcessId(libreTranslateProcess));
        }
        return status;
    }

    private boolean isLibreTranslateRunning() {
        return libreTranslateProcess != null && libreTranslateProcess.isAlive();
    }

    private String getProcessId(Process process) {
        try {
            ProcessHandle handle = process.toHandle();
            return handle.isAlive() ? String.valueOf(handle.pid()) : "unknown";
        } catch (Exception e) {
            return process.toString();
        }
    }

    private void destroyProcessTree(Process process, String serviceName) {
        if (process == null || !process.isAlive()) {
            return;
        }
        try {
            String pid = getProcessId(process);
            logger.info("正在关闭 {} 服务进程树，根进程 PID: {}", serviceName, pid);

            ProcessHandle processHandle = process.toHandle();
            processHandle.children().forEach(child -> {
                try {
                    logger.debug("关闭 {} 子进程，PID: {}", serviceName, child.pid());
                    child.destroy();
                } catch (Exception e) {
                    logger.warn("关闭 {} 子进程 {} 时出错: {}", serviceName, child.pid(), e.getMessage());
                }
            });

            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            process.destroy();
            boolean terminated = process.waitFor(5, TimeUnit.SECONDS);
            if (!terminated) {
                logger.warn("{} 服务未在 5 秒内正常关闭，正在强制关闭进程树...", serviceName);
                processHandle.descendants().forEach(d -> {
                    try { d.destroyForcibly(); } catch (Exception e) { /* 忽略 */ }
                });
                process.destroyForcibly();
                process.waitFor(3, TimeUnit.SECONDS);
            }
            logger.info("{} 服务进程树已关闭", serviceName);
        } catch (InterruptedException e) {
            logger.error("关闭 {} 服务时被中断: {}", serviceName, e.getMessage(), e);
            Thread.currentThread().interrupt();
            if (process.isAlive()) {
                try {
                    process.toHandle().descendants().forEach(d -> {
                        try { d.destroyForcibly(); } catch (Exception ex) { /* 忽略 */ }
                    });
                    process.destroyForcibly();
                } catch (Exception ex) {
                    logger.error("强制关闭 {} 服务时出错: {}", serviceName, ex.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("关闭 {} 服务时发生异常: {}", serviceName, e.getMessage(), e);
            if (process != null && process.isAlive()) {
                try {
                    process.toHandle().descendants().forEach(d -> {
                        try { d.destroyForcibly(); } catch (Exception ex) { /* 忽略 */ }
                    });
                    process.destroyForcibly();
                } catch (Exception ex) {
                    logger.error("强制关闭 {} 服务时出错: {}", serviceName, ex.getMessage());
                }
            }
        }
    }
}
