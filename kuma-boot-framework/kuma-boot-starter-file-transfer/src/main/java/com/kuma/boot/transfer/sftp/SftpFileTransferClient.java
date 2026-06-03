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

package com.kuma.boot.transfer.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.transfer.autoconfigure.properties.SftpProperties;
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
import java.util.List;
import java.util.Vector;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.StringUtils;

/**
 * 基于 JSch（mwiede fork）的 SFTP 文件传输客户端.
 *
 * <p>支持密码鉴权和私钥鉴权，私钥优先。
 * 线程安全：所有方法加 {@code synchronized}，ChannelSftp 本身非线程安全。
 *
 * @author kuma
 */
public class SftpFileTransferClient implements FileTransferClient, DisposableBean {

    private final SftpProperties props;
    private Session session;
    private ChannelSftp channel;

    public SftpFileTransferClient(SftpProperties props) {
        this.props = props;
        connect();
    }

    // ── FileTransferClient ────────────────────────────────────────────────────

    @Override
    public synchronized void upload(String remotePath, Path localFile) {
        try (InputStream in = Files.newInputStream(localFile)) {
            upload(remotePath, in);
        } catch (IOException e) {
            throw new FileTransferException("SFTP 读取本地文件失败: " + localFile, e);
        }
    }

    @Override
    public synchronized void upload(String remotePath, InputStream inputStream) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        mkdirParents(fullPath);
        try {
            channel.put(inputStream, fullPath);
            LogUtils.info("[kuma-sftp] uploaded: {}", fullPath);
        } catch (SftpException e) {
            disconnect();
            throw new FileTransferException("SFTP 上传失败: " + fullPath, e);
        }
    }

    @Override
    public synchronized InputStream download(String remotePath) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        try {
            return channel.get(fullPath);
        } catch (SftpException e) {
            disconnect();
            throw new FileTransferException("SFTP 下载失败: " + fullPath, e);
        }
    }

    @Override
    public synchronized void download(String remotePath, Path localFile) {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(localFile.toFile()))) {
            download(remotePath, out);
        } catch (IOException e) {
            throw new FileTransferException("SFTP 写入本地文件失败: " + localFile, e);
        }
    }

    @Override
    public synchronized void download(String remotePath, OutputStream outputStream) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        try {
            channel.get(fullPath, outputStream);
        } catch (SftpException e) {
            disconnect();
            throw new FileTransferException("SFTP 下载失败: " + fullPath, e);
        }
    }

    @Override
    public synchronized void delete(String remotePath) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        try {
            channel.rm(fullPath);
        } catch (SftpException e) {
            disconnect();
            throw new FileTransferException("SFTP 删除失败: " + fullPath, e);
        }
    }

    @Override
    public synchronized void rename(String remotePath, String newPath) {
        ensureConnected();
        try {
            channel.rename(resolvePath(remotePath), resolvePath(newPath));
        } catch (SftpException e) {
            disconnect();
            throw new FileTransferException("SFTP 重命名失败", e);
        }
    }

    @Override
    public synchronized void mkdir(String remotePath) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        String[] parts = fullPath.split("/");
        StringBuilder current = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) continue;
            current.append("/").append(part);
            try {
                channel.mkdir(current.toString());
            } catch (SftpException ignored) {
                // 目录已存在时会抛异常，忽略
            }
        }
    }

    @Override
    public synchronized boolean exists(String remotePath) {
        ensureConnected();
        try {
            SftpATTRS attrs = channel.stat(resolvePath(remotePath));
            return attrs != null;
        } catch (SftpException e) {
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized List<FileInfo> list(String remotePath) {
        ensureConnected();
        String fullPath = resolvePath(remotePath);
        try {
            Vector<ChannelSftp.LsEntry> entries = channel.ls(fullPath);
            return entries.stream()
                    .filter(e -> !".".equals(e.getFilename()) && !"..".equals(e.getFilename()))
                    .map(e -> {
                        SftpATTRS a = e.getAttrs();
                        return FileInfo.builder()
                                .name(e.getFilename())
                                .path(fullPath + "/" + e.getFilename())
                                .directory(a.isDir())
                                .size(a.getSize())
                                .lastModified(Instant.ofEpochSecond(a.getMTime()))
                                .build();
                    })
                    .toList();
        } catch (SftpException e) {
            disconnect();
            throw new FileTransferException("SFTP 列目录失败: " + fullPath, e);
        }
    }

    @Override
    public String protocol() {
        return "sftp";
    }

    @Override
    public void destroy() {
        disconnect();
    }

    // ── 连接管理 ──────────────────────────────────────────────────────────────

    private void connect() {
        try {
            JSch jsch = new JSch();

            if (StringUtils.hasText(props.getKnownHostsFile())) {
                jsch.setKnownHosts(props.getKnownHostsFile());
            }
            if (StringUtils.hasText(props.getPrivateKey())) {
                byte[] passphrase = StringUtils.hasText(props.getPrivateKeyPassphrase())
                        ? props.getPrivateKeyPassphrase().getBytes()
                        : null;
                jsch.addIdentity(props.getPrivateKey(), passphrase);
            }

            session = jsch.getSession(props.getUsername(), props.getHost(), props.getPort());

            if (StringUtils.hasText(props.getPassword()) && !StringUtils.hasText(props.getPrivateKey())) {
                session.setPassword(props.getPassword());
            }
            session.setConfig("StrictHostKeyChecking", props.isStrictHostKeyChecking() ? "yes" : "no");
            session.connect(props.getConnectTimeoutMs());

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(props.getConnectTimeoutMs());

            LogUtils.info("[kuma-sftp] connected to {}:{}", props.getHost(), props.getPort());
        } catch (JSchException e) {
            throw new FileTransferException("SFTP 连接失败: " + props.getHost(), e);
        }
    }

    private void disconnect() {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        channel = null;
        session = null;
    }

    private void ensureConnected() {
        if (channel == null || !channel.isConnected()) {
            LogUtils.info("[kuma-sftp] reconnecting...");
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
