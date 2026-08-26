package com.kuma.cloud.lab.javacore.support;

import com.kuma.cloud.lab.javacore.domain.vo.FileEntryVO;
import com.kuma.cloud.lab.javacore.domain.vo.FileOperationResultVO;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Java 文件 I/O 与 NIO 演示。
 */
public final class FileLabSupport {

    private final Path workspace;

    public FileLabSupport(String workspacePath) {
        this.workspace = Path.of(workspacePath).toAbsolutePath().normalize();
    }

    public Path workspace() {
        return workspace;
    }

    public FileOperationResultVO writeText(String relativePath, String content, boolean append) throws IOException {
        Path target = resolve(relativePath);
        Files.createDirectories(target.getParent());
        if (append) {
            Files.writeString(
                    target,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } else {
            Files.writeString(
                    target,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        }
        return result("WRITE_TEXT", target, "使用 Files.writeString 写入 UTF-8 文本");
    }

    public FileOperationResultVO readText(String relativePath) throws IOException {
        Path target = resolve(relativePath);
        String content = Files.readString(target, StandardCharsets.UTF_8);
        return new FileOperationResultVO(
                "READ_TEXT",
                target.toString(),
                Files.size(target),
                content,
                "使用 Files.readString 读取");
    }

    public FileOperationResultVO copy(String sourceRelativePath, String targetRelativePath) throws IOException {
        Path source = resolve(sourceRelativePath);
        Path target = resolve(targetRelativePath);
        Files.createDirectories(target.getParent());
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        return result("COPY", target, "自 " + source.getFileName() + " 复制");
    }

    public List<FileEntryVO> list(String relativeDir) throws IOException {
        Path dir = resolve(relativeDir);
        Files.createDirectories(dir);
        List<FileEntryVO> entries = new ArrayList<>();
        try (Stream<Path> paths = Files.list(dir)) {
            paths.sorted().forEach(path -> {
                try {
                    entries.add(new FileEntryVO(
                            workspace.relativize(path).toString().replace('\\', '/'),
                            Files.isDirectory(path),
                            Files.isDirectory(path) ? -1L : Files.size(path)));
                } catch (IOException ex) {
                    entries.add(new FileEntryVO(path.toString(), false, -1L));
                }
            });
        }
        return entries;
    }

    public List<String> ioNotes() {
        return List.of(
                "java.io：面向流的经典 API，如 FileInputStream / BufferedReader",
                "java.nio.file（NIO.2）：Path + Files 工具类，支持 walk、copy、watch 等",
                "字符集应显式指定 StandardCharsets.UTF_8，避免平台默认编码差异",
                "大文件优先使用流式读写或 FileChannel，避免一次性 readAllBytes");
    }

    private FileOperationResultVO result(String operation, Path target, String note) throws IOException {
        return new FileOperationResultVO(
                operation,
                target.toString(),
                Files.exists(target) ? Files.size(target) : 0L,
                null,
                note);
    }

    private Path resolve(String relativePath) {
        Path resolved = workspace.resolve(relativePath).normalize();
        if (!resolved.startsWith(workspace)) {
            throw new IllegalArgumentException("路径越界，仅允许访问工作目录内文件: " + relativePath);
        }
        return resolved;
    }

}
