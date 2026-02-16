package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.common.core.compose.ContextComponent;
import cn.kuma.blog.main.service.ServiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
 * @version 1.0
 */
@Service
@SuppressWarnings("all")
public class ServiceServiceImpl implements ServiceService, ApplicationListener<ApplicationReadyEvent> ,ContextComponent {

    /**
     * 标记是否已经执行过清理（避免重复清理）
     */
    private volatile boolean destroyed = false;

    private static final Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);

    /**
     * 命令执行线程池，用于在后台线程启动服务
     */
    private final Executor asyncExecutor;

    /**
     * LibreTranslate 进程引用
     */
    private Process libreTranslateProcess;

    /**
     * MediaCrawler 进程引用
     */
    private Process mediaCrawlerProcess;

    /**
     * LibreTranslate 是否启用（启动时自动启动）
     */
    @Value("${python.libreTranslate.enabled:false}")
    private boolean libreTranslateEnabled;

    /**
     * LibreTranslate 可执行文件路径
     */
    @Value("${python.libreTranslate.path:E:\\python\\Python39\\Scripts\\libretranslate.exe}")
    private String libreTranslatePath;

    /**
     * LibreTranslate 服务监听地址
     */
    @Value("${python.libreTranslate.host:0.0.0.0}")
    private String libreTranslateHost;

    /**
     * LibreTranslate 服务端口
     */
    @Value("${python.libreTranslate.port:5000}")
    private int libreTranslatePort;

    /**
     * LibreTranslate 仅加载的语言模型
     */
    @Value("${python.libreTranslate.load-only:en,zh}")
    private String libreTranslateLoadOnly;

    /**
     * LibreTranslate 线程数
     */
    @Value("${python.libreTranslate.threads:2}")
    private int libreTranslateThreads;

    /**
     * LibreTranslate 是否自动更新/下载模型
     */
    @Value("${python.libreTranslate.update-models:false}")
    private boolean libreTranslateUpdateModels;

    /**
     * MediaCrawler 是否启用（启动时自动启动）
     */
    @Value("${python.mediaCrawler.enabled:false}")
    private boolean mediaCrawlerEnabled;

    /**
     * MediaCrawler 项目路径
     */
    @Value("${python.mediaCrawler.path:blog-module-Python/MediaCrawler}")
    private String mediaCrawlerPath;

    /**
     * MediaCrawler 启动命令（默认使用 uv）
     */
    @Value("${python.mediaCrawler.command:uv}")
    private String mediaCrawlerCommand;

    /**
     * MediaCrawler WebUI 服务端口
     */
    @Value("${python.mediaCrawler.port}")
    private int mediaCrawlerPort;

    @Autowired
    public ServiceServiceImpl(@Qualifier("normalExecutor") Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (libreTranslateEnabled) {
            logger.info("配置了自动启动 LibreTranslate 服务，正在启动...");
            startLibreTranslate();
        }
        if (mediaCrawlerEnabled) {
            logger.info("配置了自动启动 MediaCrawler 服务，正在启动...");
            startMediaCrawler();
        }
    }
    /**
     * ContextComponent 接口方法
     * Bean 初始化后调用（此时配置已经注入，但应用可能还未完全启动）
     */
    @Override
    public void onApplicationStart() {
        // 不做任何事情，等待 ApplicationReadyEvent 触发后再启动服务
        // 这样可以确保应用完全启动后再启动外部服务
    }

    /**
     * ContextComponent 接口方法
     * Spring 上下文关闭时调用
     */
    @Override
    public void onApplicationStop() {
        logger.info("Spring Boot 正在关闭，开始清理所有服务进程...");

        if (libreTranslateProcess != null) {
            destroyProcessTree(libreTranslateProcess, "LibreTranslate");
            libreTranslateProcess = null;
        }

        if (mediaCrawlerProcess != null) {
            destroyProcessTree(mediaCrawlerProcess, "MediaCrawler");
            mediaCrawlerProcess = null;
        }

        logger.info("所有服务进程清理完成");
    }

    @Override
    public Map<String, Object> startLibreTranslate() {
        // 检查服务是否已在运行
        if (isLibreTranslateRunning()) {
            Map<String, Object> status = new HashMap<>();
            status.put("running", true);
            status.put("host", libreTranslateHost);
            status.put("port", libreTranslatePort);
            status.put("pid", getProcessId(libreTranslateProcess));
            status.put("message", "LibreTranslate服务已在运行");
            return status;
        }


        // 立即提交到异步线程，让主线程快速返回
        CompletableFuture.runAsync(() -> {
            try {
                logger.info("正在异步启动 LibreTranslate 服务...");

                File libreTranslateExe = new File(libreTranslatePath);
                if (!libreTranslateExe.exists() || !libreTranslateExe.canExecute()) {
                    logger.error("LibreTranslate 可执行文件不存在或不可执行: {}", libreTranslatePath);
                    return;
                }

                logger.info("找到 LibreTranslate 路径: {}", libreTranslatePath);
                List<String> command = new ArrayList<>();
                command.add(libreTranslateExe.getAbsolutePath());
                command.add("--host");
                command.add(libreTranslateHost);
                command.add("--port");
                command.add(String.valueOf(libreTranslatePort));
                command.add("--load-only");
                command.add(libreTranslateLoadOnly);
                command.add("--threads");
                command.add(String.valueOf(libreTranslateThreads));
                command.add("--disable-web-ui");
                
                // 自动下载/更新语言模型
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
                logger.error("启动 LibreTranslate 服务时发生未知异常: {}", e.getMessage(), e);
                libreTranslateProcess = null;
            }
        }, asyncExecutor).exceptionally(throwable -> {
            logger.error("异步启动 LibreTranslate 服务时发生异常: {}", throwable.getMessage(), throwable);
            return null;
        });

        Map<String, Object> result = new HashMap<>();
        result.put("message", "LibreTranslate服务启动中，请稍后查询状态");
        result.put("host", libreTranslateHost);
        result.put("port", libreTranslatePort);
        return result;
    }

    @Override
    public String stopLibreTranslate() {
        if (libreTranslateProcess == null || !libreTranslateProcess.isAlive()) {
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
                    ProcessHandle processHandle = libreTranslateProcess.toHandle();
                    processHandle.descendants().forEach(descendant -> {
                        try {
                            descendant.destroyForcibly();
                        } catch (Exception ex) {
                            // 忽略异常
                        }
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

    /**
     * 检查 LibreTranslate 服务是否正在运行
     *
     * @return 是否正在运行
     */
    private boolean isLibreTranslateRunning() {
        return libreTranslateProcess != null && libreTranslateProcess.isAlive();
    }

    /**
     * 获取进程 ID（跨平台兼容）
     *
     * @param process 进程对象
     * @return 进程 ID 字符串
     */
    private String getProcessId(Process process) {
        try {
            ProcessHandle handle = process.toHandle();
            if (handle.isAlive()) {
                return String.valueOf(handle.pid());
            }
            return "unknown";
        } catch (Exception e) {
            try {
                java.lang.reflect.Field pidField = process.getClass().getDeclaredField("pid");
                pidField.setAccessible(true);
                long pid = pidField.getLong(process);
                return String.valueOf(pid);
            } catch (Exception ex) {
                return process.toString();
            }
        }
    }


    @Override
    public Map<String, Object> startMediaCrawler() {
        // 检查服务是否已在运行
        if (isMediaCrawlerRunning()) {
            Map<String, Object> status = new HashMap<>();
            status.put("running", true);
            status.put("path", mediaCrawlerPath);
            status.put("pid", getProcessId(mediaCrawlerProcess));
            status.put("message", "MediaCrawler服务已在运行");
            return status;
        }

        // 立即提交到异步线程，让主线程快速返回
        CompletableFuture.runAsync(() -> {
            try {
                logger.info("正在异步启动 MediaCrawler 服务...");

                // 处理相对路径，转换为绝对路径
                File mediaCrawlerDir = new File(mediaCrawlerPath);
                if (!mediaCrawlerDir.isAbsolute()) {
                    // 如果是相对路径，基于项目根目录
                    String projectRoot = System.getProperty("user.dir");
                    mediaCrawlerDir = new File(projectRoot, mediaCrawlerPath);
                }

                try {
                    mediaCrawlerDir = mediaCrawlerDir.getCanonicalFile();
                } catch (IOException e) {
                    logger.warn("无法规范化路径: {}", e.getMessage());
                }

                if (!mediaCrawlerDir.exists() || !mediaCrawlerDir.isDirectory()) {
                    logger.error("MediaCrawler 目录不存在: {} (绝对路径: {})", mediaCrawlerPath, mediaCrawlerDir.getAbsolutePath());
                    return;
                }

                logger.info("MediaCrawler 工作目录: {}", mediaCrawlerDir.getAbsolutePath());

                // 查找启动命令（uv 或 python）
                String commandPath = findCommandPath(mediaCrawlerCommand);
                if (commandPath == null || commandPath.trim().isEmpty()) {
                    logger.error("无法找到启动命令: {}，请确保已安装并添加到 PATH 环境变量中", mediaCrawlerCommand);
                    return;
                }

                logger.info("找到启动命令路径: {}", commandPath);
                List<String> command = new ArrayList<>();

                // 如果使用 uv，需要以 uv run 方式启动
                // 但 "uv run uvicorn" 可能需要 uvicorn 作为脚本运行，改用 "uv run python -m uvicorn"
                if (mediaCrawlerCommand.equals("uv") || mediaCrawlerCommand.contains("uv")) {
                    command.add(commandPath);
                    command.add("run");
                    command.add("python");
                    command.add("-m");
                    command.add("uvicorn");
                } else {
                    // 使用 python 直接启动
                    command.add(commandPath);
                    command.add("-m");
                    command.add("uvicorn");
                }

                command.add("api.main:app");
                command.add("--port");
                command.add(String.valueOf(mediaCrawlerPort));
                command.add("--host");
                command.add("0.0.0.0");

                logger.info("执行命令: {}，工作目录: {}", String.join(" ", command), mediaCrawlerDir.getAbsolutePath());

                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.directory(mediaCrawlerDir);
                processBuilder.redirectErrorStream(false);
                mediaCrawlerProcess = processBuilder.start();

                // 异步读取错误输出，以便在进程失败时能看到错误信息
                StringBuilder errorOutput = new StringBuilder();
                CompletableFuture<Void> errorReader = CompletableFuture.runAsync(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(mediaCrawlerProcess.getErrorStream(), Charset.defaultCharset()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            errorOutput.append(line).append("\n");
                            logger.debug("MediaCrawler 错误输出: {}", line);
                        }
                    } catch (IOException e) {
                        logger.warn("读取 MediaCrawler 错误输出时出错: {}", e.getMessage());
                    }
                }, asyncExecutor);

                // 等待进程启动
                Thread.sleep(2000);
                if (mediaCrawlerProcess.isAlive()) {
                    logger.info("MediaCrawler 服务启动成功，PID: {}", getProcessId(mediaCrawlerProcess));
                } else {
                    int exitCode = mediaCrawlerProcess.exitValue();
                    // 等待错误输出读取完成
                    try {
                        errorReader.get(1, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        // 忽略超时
                    }
                    String errorMsg = errorOutput.toString().trim();
                    if (errorMsg.isEmpty()) {
                        logger.error("MediaCrawler 服务启动失败，退出码: {}，未获取到错误输出", exitCode);
                    } else {
                        logger.error("MediaCrawler 服务启动失败，退出码: {}，错误信息:\n{}", exitCode, errorMsg);
                    }
                    mediaCrawlerProcess = null;
                }
            } catch (IOException e) {
                logger.error("启动 MediaCrawler 服务时发生 IO 异常: {}", e.getMessage(), e);
                mediaCrawlerProcess = null;
            } catch (InterruptedException e) {
                logger.error("启动 MediaCrawler 服务时被中断: {}", e.getMessage(), e);
                Thread.currentThread().interrupt();
                mediaCrawlerProcess = null;
            } catch (Exception e) {
                logger.error("启动 MediaCrawler 服务时发生未知异常: {}", e.getMessage(), e);
                mediaCrawlerProcess = null;
            }
        }, asyncExecutor).exceptionally(throwable -> {
            logger.error("异步启动 MediaCrawler 服务时发生异常: {}", throwable.getMessage(), throwable);
            return null;
        });

        Map<String, Object> result = new HashMap<>();
        result.put("message", "MediaCrawler服务启动中，请稍后查询状态");
        result.put("path", mediaCrawlerPath);
        return result;
    }

    @Override
    public String stopMediaCrawler() {
        if (mediaCrawlerProcess == null || !mediaCrawlerProcess.isAlive()) {
            return "服务未运行";
        }

        try {
            logger.info("停止 MediaCrawler 服务，PID: {}", getProcessId(mediaCrawlerProcess));
            destroyProcessTree(mediaCrawlerProcess, "MediaCrawler");
            mediaCrawlerProcess = null;
            return "服务已关闭";
        } catch (Exception e) {
            logger.error("关闭 MediaCrawler 服务时发生异常: {}", e.getMessage(), e);
            if (mediaCrawlerProcess != null && mediaCrawlerProcess.isAlive()) {
                try {
                    ProcessHandle processHandle = mediaCrawlerProcess.toHandle();
                    processHandle.descendants().forEach(descendant -> {
                        try {
                            descendant.destroyForcibly();
                        } catch (Exception ex) {
                            // 忽略异常
                        }
                    });
                    mediaCrawlerProcess.destroyForcibly();
                } catch (Exception ex) {
                    logger.error("强制关闭 MediaCrawler 服务时出错: {}", ex.getMessage());
                }
            }
            mediaCrawlerProcess = null;
            return "关闭服务失败: " + e.getMessage();
        }
    }

    @Override
    public Map<String, Object> getMediaCrawlerStatus() {
        Map<String, Object> status = new HashMap<>();
        boolean running = isMediaCrawlerRunning();
        status.put("running", running);
        status.put("enabled", mediaCrawlerEnabled);
        status.put("path", mediaCrawlerPath);
        status.put("command", mediaCrawlerCommand);
        status.put("port", mediaCrawlerPort);

        if (running && mediaCrawlerProcess != null) {
            status.put("pid", getProcessId(mediaCrawlerProcess));
        }

        return status;
    }

    /**
     * 检查 MediaCrawler 服务是否正在运行
     *
     * @return 是否正在运行
     */
    private boolean isMediaCrawlerRunning() {
        return mediaCrawlerProcess != null && mediaCrawlerProcess.isAlive();
    }

    /**
     * 查找命令的完整路径（如 uv、python 等）
     *
     * @param command 命令名称（如 uv、python、python3 或完整路径）
     * @return 命令的完整路径，如果找不到则返回 null
     */
    private String findCommandPath(String command) {
        String os = System.getProperty("os.name").toLowerCase();

        if (command == null || command.trim().isEmpty()) {
            return null;
        }

        String cmd = command.trim();
        // 如果是完整路径
        File cmdFile = new File(cmd);
        if (cmdFile.exists() && cmdFile.canExecute()) {
            return cmdFile.getAbsolutePath();
        }

        // 尝试在 PATH 中查找
        try {
            if (os.contains("win")) {
                ProcessBuilder pb = new ProcessBuilder("where", cmd);
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        String path = line.trim();
                        if (new File(path).exists() && new File(path).canExecute()) {
                            return path;
                        }
                    }
                }
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    logger.debug("where 命令未找到 {}，退出码: {}", cmd, exitCode);
                }
            } else {
                ProcessBuilder pb = new ProcessBuilder("which", cmd);
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
                    String line = reader.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        String path = line.trim();
                        if (new File(path).exists() && new File(path).canExecute()) {
                            return path;
                        }
                    }
                }
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    logger.debug("which 命令未找到 {}，退出码: {}", cmd, exitCode);
                }
            }
        } catch (Exception e) {
            logger.warn("查找命令 {} 时出错: {}", cmd, e.getMessage());
        }

        logger.warn("无法找到命令: {}", cmd);
        return null;
    }

    /**
     * 关闭进程及其所有子进程
     */
    private void destroyProcessTree(Process process, String serviceName) {
        if (process == null || !process.isAlive()) {
            return;
        }

        try {
            String pid = getProcessId(process);
            logger.info("正在关闭 {} 服务进程树，根进程 PID: {}", serviceName, pid);

            // 获取进程句柄并关闭所有子进程
            ProcessHandle processHandle = process.toHandle();

            // 先关闭所有子进程
            processHandle.children().forEach(child -> {
                try {
                    logger.debug("关闭 {} 子进程，PID: {}", serviceName, child.pid());
                    child.destroy();
                } catch (Exception e) {
                    logger.warn("关闭 {} 子进程 {} 时出错: {}", serviceName, child.pid(), e.getMessage());
                }
            });

            // 等待子进程关闭
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 关闭主进程
            process.destroy();
            boolean terminated = process.waitFor(5, TimeUnit.SECONDS);
            if (!terminated) {
                logger.warn("{} 服务未在 5 秒内正常关闭，正在强制关闭进程树...", serviceName);
                // 强制关闭所有后代进程
                processHandle.descendants().forEach(descendant -> {
                    try {
                        logger.debug("强制关闭 {} 后代进程，PID: {}", serviceName, descendant.pid());
                        descendant.destroyForcibly();
                    } catch (Exception e) {
                        logger.warn("强制关闭 {} 后代进程 {} 时出错: {}", serviceName, descendant.pid(), e.getMessage());
                    }
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
                    ProcessHandle processHandle = process.toHandle();
                    processHandle.descendants().forEach(descendant -> {
                        try {
                            descendant.destroyForcibly();
                        } catch (Exception ex) {
                            // 忽略异常
                        }
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
                    ProcessHandle processHandle = process.toHandle();
                    processHandle.descendants().forEach(descendant -> {
                        try {
                            descendant.destroyForcibly();
                        } catch (Exception ex) {
                            // 忽略异常
                        }
                    });
                    process.destroyForcibly();
                } catch (Exception ex) {
                    logger.error("强制关闭 {} 服务时出错: {}", serviceName, ex.getMessage());
                }
            }
        }
    }

}
