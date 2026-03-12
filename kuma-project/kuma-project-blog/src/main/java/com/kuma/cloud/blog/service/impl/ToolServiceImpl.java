package com.kuma.cloud.blog.service.impl;

import com.kuma.cloud.blog.domain.vo.CommandRequest;
import com.kuma.cloud.blog.domain.vo.CommandResponse;
import com.kuma.cloud.blog.domain.vo.FileUploadResponse;
import com.kuma.cloud.blog.domain.vo.RepkgRequest;
import com.kuma.cloud.blog.service.ToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 工具服务实现类
 *
 * @author Kuma
 */
@Service
@SuppressWarnings("all")
public class ToolServiceImpl implements ToolService {

    private static final Logger logger = LoggerFactory.getLogger(ToolServiceImpl.class);

    private static final String REPKG_TOOL_CLASSPATH = "RePKG";

    private final Executor asyncExecutor;

    @Value("${blog.tool.repkg.output-dir:${user.home}/blog-resource/tool/output}")
    private String repkgOutputDir;

    @Value("${blog.tool.repkg.tool-dir:${user.home}/blog-resource/tool}")
    private String repkgToolDir;

    public ToolServiceImpl(@Qualifier("asyncThreadPoolTaskExecutor") Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public Resource processRepkgFile(MultipartFile pkgFile, RepkgRequest request) {
        String tempPkgPath = null;
        String outputDir = null;
        String zipFilePath = null;
        try {
            FileUploadResponse uploadResponse = saveUploadedFile(repkgOutputDir, pkgFile);
            tempPkgPath = uploadResponse.getFilePath();

            File tempPkgFile = new File(tempPkgPath);
            if (!tempPkgFile.isAbsolute()) {
                tempPkgPath = tempPkgFile.getAbsolutePath();
            }
            if (!tempPkgFile.exists()) {
                throw new RuntimeException("上传的文件不存在: " + tempPkgPath);
            }

            String outputDirName = UUID.randomUUID().toString();
            outputDir = Paths.get(repkgOutputDir, outputDirName).toString();
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }
            if (!outputDirFile.isAbsolute()) {
                outputDir = outputDirFile.getAbsolutePath();
            }

            CommandRequest commandRequest = new CommandRequest();
            String repkgPath = request.getRepkgPath();
            if (repkgPath == null || repkgPath.trim().isEmpty()) {
                repkgPath = getDefaultRepkgPath();
            }

            File repkgExeFile = new File(repkgPath);
            if (!repkgExeFile.isAbsolute()) {
                repkgPath = repkgExeFile.getAbsolutePath();
                repkgExeFile = new File(repkgPath);
            }

            // 安全校验：仅允许使用工具目录内的可执行文件
            try {
                String safeBaseDir = new File(repkgToolDir).getCanonicalPath();
                String repkgCanonical = repkgExeFile.getCanonicalPath();
                if (!repkgCanonical.startsWith(safeBaseDir)) {
                    throw new RuntimeException("RePKG 路径必须在工具目录内: " + repkgToolDir);
                }
                if (!repkgExeFile.getName().toLowerCase().endsWith(".exe")) {
                    throw new RuntimeException("仅允许执行 .exe 文件");
                }
            } catch (IOException e) {
                throw new RuntimeException("路径校验失败", e);
            }

            if (!repkgExeFile.exists()) {
                throw new RuntimeException("RePKG.exe 不存在: " + repkgPath);
            }
            commandRequest.setCommand(repkgPath);

            File repkgDir = repkgExeFile.getParentFile();
            if (repkgDir != null && repkgDir.exists()) {
                commandRequest.setWorkingDirectory(repkgDir.getAbsolutePath());
            }

            boolean isMpkg = tempPkgPath.toLowerCase().endsWith(".mpkg");

            List<String> args = new ArrayList<>();
            args.add(request.getOperation() != null ? request.getOperation() : "extract");

            if (request.getFindSubfolders() != null && request.getFindSubfolders()) {
                args.add("-c");
            }
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

            if (request.getWorkingDirectory() != null && !request.getWorkingDirectory().trim().isEmpty()) {
                commandRequest.setWorkingDirectory(request.getWorkingDirectory());
            }

            logger.info("执行 RePKG 命令: {} {}", repkgPath, String.join(" ", args));

            CommandResponse commandResponse = executeCommand(commandRequest);
            logger.debug("RePKG 执行结果 - 成功: {}, 退出码: {}, 输出: {}, 错误: {}",
                    commandResponse.getSuccess(), commandResponse.getExitCode(),
                    commandResponse.getOutput(), commandResponse.getError());

            if (!commandResponse.getSuccess()) {
                String errorMsg = String.format("RePKG 执行失败 (退出码: %d): %s",
                        commandResponse.getExitCode(),
                        commandResponse.getError() != null && !commandResponse.getError().isEmpty()
                                ? commandResponse.getError()
                                : commandResponse.getOutput());
                logger.error("RePKG 执行失败: {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }

            zipFilePath = Paths.get(repkgOutputDir, outputDirName + ".zip").toString();
            zipDirectory(outputDir, zipFilePath);
            File zipFile = new File(zipFilePath);
            if (!zipFile.exists()) {
                throw new RuntimeException("打包文件失败");
            }

            cleanupTempFiles(tempPkgPath, outputDir);
            return createAutoCleanupResource(zipFile);

        } catch (Exception e) {
            logger.error("处理 RePKG 文件时发生异常: {}", e.getMessage(), e);
            cleanupTempFiles(tempPkgPath, outputDir, zipFilePath);
            throw new RuntimeException("处理 RePKG 文件失败: " + e.getMessage(), e);
        }
    }

    // ── 私有工具方法 ──────────────────────────────────────────────────────────

    /**
     * 执行外部命令，同步等待结果
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
            }, asyncExecutor);

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
            }, asyncExecutor);

            if (request.getWaitForCompletion() != null && !request.getWaitForCompletion()) {
                response.setExitCode(0);
                response.setSuccess(true);
            } else {
                int timeout = request.getTimeout() != null ? request.getTimeout() : 30;
                boolean finished = process.waitFor(timeout, TimeUnit.SECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    response.setSuccess(false);
                    response.setExitCode(-1);
                    response.setError("命令执行超时（超过 " + timeout + " 秒）");
                } else {
                    try {
                        CompletableFuture.allOf(outputFuture, errorFuture).get(2, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        // 读取超时，继续处理已有内容
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
            response.setError("执行命令时发生 IO 异常: " + e.getMessage());
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
        response.setExecutionTime(System.currentTimeMillis() - startTime);
        return response;
    }

    /**
     * 保存上传的文件到指定目录
     */
    private FileUploadResponse saveUploadedFile(String dir, MultipartFile file) throws IOException {
        File uploadDir = new File(dir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.')) : "";
        String savedFilename = UUID.randomUUID() + ext;
        File dest = new File(uploadDir, savedFilename);
        Files.copy(file.getInputStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

        FileUploadResponse resp = new FileUploadResponse();
        resp.setOriginalFilename(originalFilename);
        resp.setSavedFilename(savedFilename);
        resp.setFilePath(dest.getAbsolutePath());
        resp.setFileSize(file.getSize());
        resp.setContentType(file.getContentType());
        return resp;
    }

    /**
     * 将目录压缩为 ZIP 文件
     */
    private void zipDirectory(String sourceDir, String zipFilePath) throws IOException {
        File sourceDirFile = new File(sourceDir);
        try (ZipOutputStream zos = new ZipOutputStream(
                new BufferedOutputStream(new FileOutputStream(zipFilePath)))) {
            zipDirectoryRecursive(sourceDirFile, sourceDirFile, zos);
        }
    }

    private void zipDirectoryRecursive(File rootDir, File currentFile, ZipOutputStream zos) throws IOException {
        if (currentFile.isDirectory()) {
            File[] files = currentFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    zipDirectoryRecursive(rootDir, file, zos);
                }
            }
        } else {
            String relativePath = rootDir.toURI().relativize(currentFile.toURI()).getPath();
            zos.putNextEntry(new ZipEntry(relativePath));
            Files.copy(currentFile.toPath(), zos);
            zos.closeEntry();
        }
    }

    /**
     * 清理临时文件和目录
     */
    private void cleanupTempFiles(String... paths) {
        for (String path : paths) {
            if (path == null) continue;
            try {
                File file = new File(path);
                if (file.isDirectory()) {
                    deleteDirectoryRecursive(file);
                } else if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                logger.warn("清理临时文件失败: {}, 原因: {}", path, e.getMessage());
            }
        }
    }

    private void deleteDirectoryRecursive(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursive(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

    /**
     * 创建下载后自动删除的 Resource
     */
    private Resource createAutoCleanupResource(File file) {
        return new AbstractResource() {
            @Override
            public String getDescription() {
                return "AutoCleanupResource[" + file.getAbsolutePath() + "]";
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new BufferedInputStream(new FileInputStream(file)) {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                };
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public String getFilename() {
                return file.getName();
            }
        };
    }

    /**
     * 获取默认 RePKG 可执行文件路径。
     * 优先返回工具目录下已有文件；不存在时尝试从 classpath 的 RePKG/ 目录释放。
     */
    private String getDefaultRepkgPath() {
        try {
            File toolDir = new File(repkgToolDir);
            if (!toolDir.exists()) {
                toolDir.mkdirs();
            }
            File repkgExe = new File(toolDir, "RePKG.exe");
            if (repkgExe.exists()) {
                return repkgExe.getAbsolutePath();
            }

            ClassLoader classLoader = getClass().getClassLoader();
            String[] toolFiles = {
                "RePKG.exe", "RePKG.exe.config",
                "RePKG.Application.dll", "RePKG.Core.dll",
                "CommandLine.dll", "K4os.Compression.LZ4.dll",
                "Newtonsoft.Json.dll", "SixLabors.ImageSharp.dll",
                "System.Buffers.dll", "System.Drawing.Common.dll",
                "System.Memory.dll", "System.Numerics.Vectors.dll",
                "System.Runtime.CompilerServices.Unsafe.dll",
                "System.Text.Encoding.CodePages.dll"
            };
            for (String fileName : toolFiles) {
                try (InputStream inputStream = classLoader.getResourceAsStream(
                        REPKG_TOOL_CLASSPATH + "/" + fileName)) {
                    if (inputStream != null) {
                        Files.copy(inputStream, new File(toolDir, fileName).toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            if (repkgExe.exists()) {
                return repkgExe.getAbsolutePath();
            }
        } catch (Exception e) {
            logger.warn("无法从 classpath 释放 RePKG 工具: {}", e.getMessage());
        }
        return new File(repkgToolDir, "RePKG.exe").getAbsolutePath();
    }
}
