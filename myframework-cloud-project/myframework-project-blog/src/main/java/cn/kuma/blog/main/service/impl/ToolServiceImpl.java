package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.main.domain.VO.CommandRequest;
import cn.kuma.blog.main.domain.VO.CommandResponse;
import cn.kuma.blog.main.domain.VO.FileUploadResponse;
import cn.kuma.blog.main.domain.VO.RepkgRequest;
import cn.kuma.blog.main.service.FileService;
import cn.kuma.blog.main.service.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * 工具服务实现类
 *
 * @author Kuma
 * @version 1.0
 */
@Service
@SuppressWarnings("all")
public class ToolServiceImpl implements ToolService {

    private static final Logger logger = LoggerFactory.getLogger(ToolServiceImpl.class);

    private final Executor commandExecutor;
    private final FileService fileService;

    private static final String REPKG_OUTPUT_DIR = "E:\\blog-resource\\tool\\output";
    private static final String REPKG_TOOL_DIR = "E:\\blog-resource\\tool";
    private static final String REPKG_TOOL_CLASSPATH = "RePKG";

    @Autowired
    public ToolServiceImpl(@Qualifier("commandExecutor") Executor commandExecutor, FileService fileService) {
        this.commandExecutor = commandExecutor;
        this.fileService = fileService;
    }

    /**
     * 执行命令（私有方法，供内部使用）
     */
    private CommandResponse executeCommand(CommandRequest request) {
        long startTime = System.currentTimeMillis();
        CommandResponse response = new CommandResponse();
        try {
            List<String> commandList = new ArrayList<>();
            commandList.add(request.getCommand());
            if (request.getArgs() != null && !request.getArgs().isEmpty()) {
                commandList.addAll(request.getArgs());
            }
            response.setFullCommand(String.join(" ", commandList));
            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            if (request.getWorkingDirectory() != null && !request.getWorkingDirectory().trim().isEmpty()) {
                processBuilder.directory(new File(request.getWorkingDirectory()));
            }
            processBuilder.redirectErrorStream(false);
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();
            CompletableFuture<Void> outputFuture = CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                } catch (IOException e) {
                    synchronized (error) {
                        error.append("读取标准输出时出错: ").append(e.getMessage());
                    }
                }
            }, commandExecutor);
            CompletableFuture<Void> errorFuture = CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream(), Charset.defaultCharset()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        error.append(line).append("\n");
                    }
                } catch (IOException e) {
                    synchronized (error) {
                        error.append("读取错误输出时出错: ").append(e.getMessage());
                    }
                }
            }, commandExecutor);
            boolean finished;
            if (request.getWaitForCompletion() != null && !request.getWaitForCompletion()) {
                finished = true;
                response.setExitCode(0);
                response.setSuccess(true);
            } else {
                int timeout = request.getTimeout() != null ? request.getTimeout() : 30;
                finished = process.waitFor(timeout, TimeUnit.SECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    response.setSuccess(false);
                    response.setExitCode(-1);
                    response.setError("命令执行超时（超过 " + timeout + " 秒）");
                } else {
                    try {
                        CompletableFuture.allOf(outputFuture, errorFuture).get(2, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        // 如果读取超时，继续处理已读取的内容
                    }
                    int exitCode = process.exitValue();
                    response.setExitCode(exitCode);
                    response.setSuccess(exitCode == 0);
                }
            }
            response.setOutput(output.toString().trim());
            if (error.length() > 0) {
                response.setError(error.toString().trim());
            }
        } catch (IOException e) {
            response.setSuccess(false);
            response.setExitCode(-1);
            response.setError("执行命令时发生IO异常: " + e.getMessage());
        } catch (InterruptedException e) {
            response.setSuccess(false);
            response.setExitCode(-1);
            response.setError("命令执行被中断: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            response.setSuccess(false);
            response.setExitCode(-1);
            response.setError("执行命令时发生异常: " + e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        response.setExecutionTime(endTime - startTime);

        return response;
    }


    @Override
    public Resource processRepkgFile(MultipartFile pkgFile, RepkgRequest request) {
        String tempPkgPath = null;
        String outputDir = null;
        String zipFilePath = null;
        try {
            FileUploadResponse uploadResponse = fileService.uploadFile(REPKG_OUTPUT_DIR, pkgFile);
            tempPkgPath = uploadResponse.getFilePath();

            // 确保文件路径是绝对路径
            File tempPkgFile = new File(tempPkgPath);
            if (!tempPkgFile.isAbsolute()) {
                tempPkgPath = tempPkgFile.getAbsolutePath();
            }

            // 验证文件是否存在
            if (!tempPkgFile.exists()) {
                throw new RuntimeException("上传的文件不存在: " + tempPkgPath);
            }

            String outputDirName = UUID.randomUUID().toString();
            outputDir = Paths.get(REPKG_OUTPUT_DIR, outputDirName).toString();
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }

            // 确保输出目录是绝对路径
            if (!outputDirFile.isAbsolute()) {
                outputDir = outputDirFile.getAbsolutePath();
            }

            CommandRequest commandRequest = new CommandRequest();
            String repkgPath = request.getRepkgPath();
            if (repkgPath == null || repkgPath.trim().isEmpty()) {
                repkgPath = getDefaultRepkgPath();
            }

            // 确保 RePKG.exe 路径是绝对路径
            File repkgExeFile = new File(repkgPath);
            if (!repkgExeFile.isAbsolute()) {
                repkgPath = repkgExeFile.getAbsolutePath();
                repkgExeFile = new File(repkgPath);
            }
            // 安全：仅允许使用工具目录内的可执行文件，防止传入任意 exe 路径
            String safeBaseDir;
            try {
                safeBaseDir = new File(REPKG_TOOL_DIR).getCanonicalPath();
                String repkgCanonical = repkgExeFile.getCanonicalPath();
                if (!repkgCanonical.startsWith(safeBaseDir)) {
                    throw new RuntimeException("RePKG 路径必须在工具目录内: " + REPKG_TOOL_DIR);
                }
                if (!repkgExeFile.getName().toLowerCase().endsWith(".exe")) {
                    throw new RuntimeException("仅允许执行 .exe 文件");
                }
            } catch (IOException e) {
                throw new RuntimeException("路径校验失败", e);
            }
            // 验证 RePKG.exe 是否存在
            if (!repkgExeFile.exists()) {
                throw new RuntimeException("RePKG.exe 不存在: " + repkgPath);
            }
            commandRequest.setCommand(repkgPath);

            // 设置工作目录为 RePKG.exe 所在目录，确保能找到依赖的 DLL 文件
            File repkgDir = repkgExeFile.getParentFile();
            if (repkgDir != null && repkgDir.exists()) {
                commandRequest.setWorkingDirectory(repkgDir.getAbsolutePath());
                logger.debug("设置工作目录为: {}", repkgDir.getAbsolutePath());
            }

            // 检测文件类型
            boolean isMpkg = tempPkgPath.toLowerCase().endsWith(".mpkg");

            List<String> args = new ArrayList<>();
            args.add(request.getOperation() != null ? request.getOperation() : "extract");

            // 添加参数
            if (request.getFindSubfolders() != null && request.getFindSubfolders()) {
                args.add("-c");
            }

            // .mpkg 文件不支持 -e tex 参数，需要跳过
            if (request.getEntryType() != null && !request.getEntryType().trim().isEmpty()) {
                if (isMpkg) {
                    logger.warn("检测到 .mpkg 文件，跳过 -e {} 参数（.mpkg 文件不支持此参数）", request.getEntryType());
                } else {
                    args.add("-e");
                    args.add(request.getEntryType());
                }
            }

            if (request.getSimplifyPaths() != null && request.getSimplifyPaths()) {
                args.add("-s");
            }

            if (request.getTexOnly() != null && request.getTexOnly()) {
                args.add("-t");
            }
            args.add("-o");
            args.add(outputDir);
            args.add(tempPkgPath);
            commandRequest.setArgs(args);
            commandRequest.setTimeout(request.getTimeout() != null ? request.getTimeout() : 300);

            // 如果请求中指定了工作目录，则使用指定的，否则使用 RePKG.exe 所在目录
            if (request.getWorkingDirectory() != null && !request.getWorkingDirectory().trim().isEmpty()) {
                commandRequest.setWorkingDirectory(request.getWorkingDirectory());
            }

            // 记录执行的命令和参数
            logger.info("执行 RePKG 命令: {} {}", repkgPath, String.join(" ", args));
            logger.debug("输入文件路径: {}", tempPkgPath);
            logger.debug("输出目录: {}", outputDir);

            CommandResponse commandResponse = executeCommand(commandRequest);

            // 记录命令执行结果
            logger.debug("RePKG 执行结果 - 成功: {}, 退出码: {}, 输出: {}, 错误: {}",
                    commandResponse.getSuccess(),
                    commandResponse.getExitCode(),
                    commandResponse.getOutput(),
                    commandResponse.getError());

            if (!commandResponse.getSuccess()) {
                String errorMsg = String.format("repkg 执行失败 (退出码: %d): %s",
                        commandResponse.getExitCode(),
                        commandResponse.getError() != null && !commandResponse.getError().isEmpty()
                                ? commandResponse.getError()
                                : commandResponse.getOutput());
                logger.error("RePKG 执行失败: {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            zipFilePath = Paths.get(REPKG_OUTPUT_DIR, outputDirName + ".zip").toString();
            cn.kuma.blog.common.util.FileUtils.zipDirectory(outputDir, zipFilePath);
            File zipFile = new File(zipFilePath);
            if (!zipFile.exists()) {
                throw new RuntimeException("打包文件失败");
            }

            // 打包成功后，清理临时文件
            cn.kuma.blog.common.util.FileUtils.cleanupTempFiles(tempPkgPath, outputDir, null);

            // 返回可自动清理的 Resource
            return fileService.createAutoCleanupResource(zipFile);

        } catch (Exception e) {
            // 异常时清理所有临时文件
            logger.error("处理 repkg 文件时发生异常: {}", e.getMessage(), e);
            cn.kuma.blog.common.util.FileUtils.cleanupTempFiles(tempPkgPath, outputDir, zipFilePath);
            throw new RuntimeException("处理 repkg 文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取默认的 repkg 工具路径
     * 从 classpath 读取工具文件，复制到临时目录后返回可执行路径
     */
    private String getDefaultRepkgPath() {
        try {
            File toolDir = new File(REPKG_TOOL_DIR);
            if (!toolDir.exists()) {
                toolDir.mkdirs();
            }
            File repkgExe = new File(toolDir, "RePKG.exe");
            if (repkgExe.exists()) {
                return repkgExe.getAbsolutePath();
            }
            ClassLoader classLoader = getClass().getClassLoader();
            String[] toolFiles = {
                "RePKG.exe",
                "RePKG.exe.config",
                "RePKG.Application.dll",
                "RePKG.Core.dll",
                "CommandLine.dll",
                "K4os.Compression.LZ4.dll",
                "Newtonsoft.Json.dll",
                "SixLabors.ImageSharp.dll",
                "System.Buffers.dll",
                "System.Drawing.Common.dll",
                "System.Memory.dll",
                "System.Numerics.Vectors.dll",
                "System.Runtime.CompilerServices.Unsafe.dll",
                "System.Text.Encoding.CodePages.dll"
            };
            for (String fileName : toolFiles) {
                String resourcePath = REPKG_TOOL_CLASSPATH + "/" + fileName;
                InputStream inputStream = classLoader.getResourceAsStream(resourcePath);

                if (inputStream != null) {
                    File targetFile = new File(toolDir, fileName);
                    Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    inputStream.close();
                }
            }
            if (repkgExe.exists()) {
                return repkgExe.getAbsolutePath();
            }
        } catch (Exception e) {
            System.err.println("无法从 classpath 读取 repkg 工具: " + e.getMessage());
        }
        return "tool.RePKG-Tool-linux";
    }
}

