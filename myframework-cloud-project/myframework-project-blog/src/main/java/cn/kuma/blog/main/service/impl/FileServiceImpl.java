package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.common.util.FileUtils;
import cn.kuma.blog.main.domain.VO.FileUploadResponse;
import cn.kuma.blog.main.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现类
 *
 * @author Kuma
 * @version 1.0
 */
@Service
@SuppressWarnings("all")
public class FileServiceImpl implements FileService {

    @Override
    public FileUploadResponse uploadFile(String baseDir, MultipartFile file) {

        try {
            // 确保上传目录存在
            File uploadDir = new File(baseDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String savedFilename = UUID.randomUUID().toString() + extension;

            // 按日期创建子目录
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            File dateDirFile = new File(baseDir, dateDir);
            if (!dateDirFile.exists()) {
                dateDirFile.mkdirs();
            }

            // 保存文件
            Path targetPath = Paths.get(dateDirFile.getAbsolutePath(), savedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 构建响应
            FileUploadResponse response = new FileUploadResponse();
            response.setOriginalFilename(originalFilename);
            response.setSavedFilename(savedFilename);
            response.setFilePath(targetPath.toString());
            response.setFileSize(file.getSize());
            response.setContentType(file.getContentType());
            response.setFileUrl("/file/download/" + dateDir + "/" + savedFilename);

            return response;

        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FileUploadResponse> uploadFiles(String baseDir, MultipartFile[] files) {
        List<FileUploadResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                FileUploadResponse response = uploadFile(baseDir, file);
                responses.add(response);
            } catch (Exception e) {
                // 单个文件上传失败，继续处理其他文件
                // 可以记录日志
            }
        }

        return responses;
    }

    @Override
    public Resource downloadFile(String baseDir, String dateDir, String filename) {
        Path filePath = Paths.get(baseDir, dateDir, filename);
        File file = filePath.toFile();

        if (!file.exists()) {
            return null;
        }

        return new FileSystemResource(file);
    }

    @Override
    public boolean deleteFile(String baseDir, String dateDir, String filename) {
        return FileUtils.deleteFile(baseDir, dateDir, filename);
    }

    @Override
    public boolean fileExists(String baseDir, String dateDir, String filename) {
        return FileUtils.fileExists(baseDir, dateDir, filename);
    }

    @Override
    public void zipDirectory(String sourceDir, String zipFilePath) throws IOException {
        FileUtils.zipDirectory(sourceDir, zipFilePath);
    }

    @Override
    public void cleanupTempFiles(String tempPath, String outputDir, String zipFilePath) {
        FileUtils.cleanupTempFiles(tempPath, outputDir, zipFilePath);
    }

    @Override
    public void deleteDirectory(File directory) {
        FileUtils.deleteDirectory(directory);
    }

    @Override
    public Resource createAutoCleanupResource(File file) {
        return new AutoCleanupFileSystemResource(file);
    }

    /**
     * 自动清理的 FileSystemResource
     * 在资源被读取后自动删除文件
     */
    private static class AutoCleanupFileSystemResource extends FileSystemResource {
        private final File file;

        public AutoCleanupFileSystemResource(File file) {
            super(file);
            this.file = file;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new AutoCleanupInputStream(super.getInputStream(), file);
        }

        /**
         * 自动清理的输入流
         */
        private static class AutoCleanupInputStream extends InputStream {
            private final InputStream delegate;
            private final File file;
            private boolean closed = false;

            public AutoCleanupInputStream(InputStream delegate, File file) {
                this.delegate = delegate;
                this.file = file;
            }

            @Override
            public int read() throws IOException {
                return delegate.read();
            }

            @Override
            public int read(byte[] b) throws IOException {
                return delegate.read(b);
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                return delegate.read(b, off, len);
            }

            @Override
            public void close() throws IOException {
                if (!closed) {
                    closed = true;
                    delegate.close();
                    // 流关闭后删除文件
                    try {
                        Files.deleteIfExists(file.toPath());
                    } catch (IOException e) {
                        // 忽略删除失败的情况
                    }
                }
            }

            @Override
            public long skip(long n) throws IOException {
                return delegate.skip(n);
            }

            @Override
            public int available() throws IOException {
                return delegate.available();
            }

            @Override
            public synchronized void mark(int readlimit) {
                delegate.mark(readlimit);
            }

            @Override
            public synchronized void reset() throws IOException {
                delegate.reset();
            }

            @Override
            public boolean markSupported() {
                return delegate.markSupported();
            }
        }
    }
}

