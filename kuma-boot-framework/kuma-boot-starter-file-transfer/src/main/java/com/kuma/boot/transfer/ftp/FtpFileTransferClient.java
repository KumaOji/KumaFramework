/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.transfer.ftp;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.transfer.autoconfigure.properties.FtpProperties;
import com.kuma.boot.transfer.core.FileInfo;
import com.kuma.boot.transfer.core.FileTransferClient;
import com.kuma.boot.transfer.core.FileTransferException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.DisposableBean;

/**
 * 基于 Apache Commons Net 的 FTP 文件传输客户端.
 *
 * <p>线程安全：所有方法加 {@code synchronized}，FTPClient 本身非线程安全。
 * 高并发场景建议自定义 Bean 引入连接池。
 *
 * @author kuma
 */
public class FtpFileTransferClient implements FileTransferClient, DisposableBean {

    private final FtpProperties props;
    private FTPClient ftpClient;

    public FtpFileTransferClient(FtpProperties props) {
        this.props = props;
        connect();
    }

    // ── FileTransferClient ────────────────────────────────────────────────────

    @Override
    public synchronized void upload(String remotePath, Path localFile) {
        try (InputStream in = Files.newInputStream(localFile)) {
            upload(remotePath, in);
        } catch (IOException e) {
            throw new FileTransferException("FTP 读取本地文件失败: " + localFile, e);
        }
    }

    @Override
    public synchronized void upload(String remotePath, InputStream inputStream) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        mkdirParents(fullPath);
        try {
            if (props.isPassiveMode()) ftpClient.enterLocalPassiveMode();
            boolean ok = ftpClient.storeFile(fullPath, inputStream);
            if (!ok) {
                throw new FileTransferException("FTP 上传失败: " + fullPath
                        + " reply=" + ftpClient.getReplyString());
            }
            LogUtils.info("[kuma-ftp] uploaded: {}", fullPath);
        } catch (IOException e) {
            disconnect();
            throw new FileTransferException("FTP 上传异常: " + fullPath, e);
        }
    }

    @Override
    public synchronized InputStream download(String remotePath) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        try {
            if (props.isPassiveMode()) ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            InputStream in = ftpClient.retrieveFileStream(fullPath);
            if (in == null) {
                throw new FileTransferException("FTP 文件不存在或无权限: " + fullPath);
            }
            return in;
        } catch (IOException e) {
            disconnect();
            throw new FileTransferException("FTP 下载异常: " + fullPath, e);
        }
    }

    @Override
    public synchronized void download(String remotePath, Path localFile) {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(localFile.toFile()))) {
            download(remotePath, out);
        } catch (IOException e) {
            throw new FileTransferException("FTP 写入本地文件失败: " + localFile, e);
        }
    }

    @Override
    public synchronized void download(String remotePath, OutputStream outputStream) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        try {
            if (props.isPassiveMode()) ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            boolean ok = ftpClient.retrieveFile(fullPath, outputStream);
            if (!ok) {
                throw new FileTransferException("FTP 下载失败: " + fullPath
                        + " reply=" + ftpClient.getReplyString());
            }
        } catch (IOException e) {
            disconnect();
            throw new FileTransferException("FTP 下载异常: " + fullPath, e);
        }
    }

    @Override
    public synchronized void delete(String remotePath) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        try {
            ftpClient.deleteFile(fullPath);
        } catch (IOException e) {
            disconnect();
            throw new FileTransferException("FTP 删除异常: " + fullPath, e);
        }
    }

    @Override
    public synchronized void rename(String remotePath, String newPath) {
        ensureConnected();
        try {
            ftpClient.rename(resolvePath(remotePath), resolvePath(newPath));
        } catch (IOException e) {
            disconnect();
            throw new FileTransferException("FTP 重命名异常", e);
        }
    }

    @Override
    public synchronized void mkdir(String remotePath) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        try {
            String[] parts = fullPath.split("/");
            StringBuilder current = new StringBuilder();
            for (String part : parts) {
                if (part.isBlank()) continue;
                current.append("/").append(part);
                ftpClient.makeDirectory(current.toString());
            }
        } catch (IOException e) {
            disconnect();
            throw new FileTransferException("FTP 创建目录异常: " + fullPath, e);
        }
    }

    @Override
    public synchronized boolean exists(String remotePath) {
        ensureConnected();
        try {
            FTPFile[] files = ftpClient.listFiles(resolvePath(remotePath));
            return files != null && files.length > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public synchronized List<FileInfo> list(String remotePath) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        try {
            FTPFile[] files = ftpClient.listFiles(fullPath);
            if (files == null) return List.of();
            return Arrays.stream(files)
                    .filter(f -> !".".equals(f.getName()) && !"..".equals(f.getName()))
                    .map(f -> FileInfo.builder()
                            .name(f.getName())
                            .path(fullPath + "/" + f.getName())
                            .directory(f.isDirectory())
                            .size(f.getSize())
                            .lastModified(f.getTimestampInstant())
                            .build())
                    .toList();
        } catch (IOException e) {
            disconnect();
            throw new FileTransferException("FTP 列目录异常: " + fullPath, e);
        }
    }

    @Override
    public String protocol() {
        return "ftp";
    }

    @Override
    public void destroy() {
        disconnect();
    }

    // ── 连接管理 ──────────────────────────────────────────────────────────────

    private void connect() {
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding(props.getEncoding());
            ftpClient.setConnectTimeout(props.getConnectTimeoutMs());
            ftpClient.setDataTimeout(java.time.Duration.ofMillis(props.getDataTimeoutMs()));
            ftpClient.connect(props.getHost(), props.getPort());

            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                throw new FileTransferException("FTP 服务器拒绝连接: " + ftpClient.getReplyString());
            }
            if (!ftpClient.login(props.getUsername(), props.getPassword())) {
                throw new FileTransferException("FTP 登录失败，检查账号密码");
            }
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            LogUtils.info("[kuma-ftp] connected to {}:{}", props.getHost(), props.getPort());
        } catch (IOException e) {
            throw new FileTransferException("FTP 连接失败: " + props.getHost(), e);
        }
    }

    private void disconnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ignored) {}
        }
        ftpClient = null;
    }

    private void ensureConnected() {
        if (ftpClient == null || !ftpClient.isConnected()) {
            LogUtils.info("[kuma-ftp] reconnecting...");
            connect();
        }
    }

    private void mkdirParents(String fullPath) {
        int idx = fullPath.lastIndexOf('/');
        if (idx > 0) {
            mkdir(fullPath.substring(0, idx));
        }
    }

    private String resolvePath(String remotePath) {
        String base = props.getBasePath().endsWith("/")
                ? props.getBasePath()
                : props.getBasePath() + "/";
        if (remotePath.startsWith("/")) {
            return remotePath;
        }
        return base + remotePath;
    }
}
