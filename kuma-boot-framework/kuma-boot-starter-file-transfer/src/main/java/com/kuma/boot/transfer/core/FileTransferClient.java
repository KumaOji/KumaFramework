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

package com.kuma.boot.transfer.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * 文件传输客户端统一抽象，屏蔽 FTP / SFTP 协议差异.
 *
 * <p>所有路径均为远程路径（相对于 {@code basePath} 配置的根目录）。
 *
 * <p>典型用法：
 * <pre>
 * // 上传
 * fileTransferClient.upload("data/report.xlsx", localPath);
 *
 * // 下载到流
 * try (InputStream in = fileTransferClient.download("data/report.xlsx")) {
 *     Files.copy(in, Paths.get("/tmp/report.xlsx"));
 * }
 * </pre>
 *
 * @author kuma
 */
public interface FileTransferClient {

    /**
     * 将本地文件上传到远程路径.
     *
     * @param remotePath 远程目标路径（含文件名）
     * @param localFile  本地文件
     */
    void upload(String remotePath, Path localFile);

    /**
     * 将输入流内容上传到远程路径.
     *
     * @param remotePath  远程目标路径（含文件名）
     * @param inputStream 数据流，调用方负责关闭
     */
    void upload(String remotePath, InputStream inputStream);

    /**
     * 下载远程文件为输入流，调用方负责关闭.
     *
     * @param remotePath 远程文件路径
     * @return 文件内容流
     */
    InputStream download(String remotePath);

    /**
     * 将远程文件下载到本地路径.
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地目标路径
     */
    void download(String remotePath, Path localFile);

    /**
     * 将远程文件内容写入输出流.
     *
     * @param remotePath   远程文件路径
     * @param outputStream 目标流，调用方负责关闭
     */
    void download(String remotePath, OutputStream outputStream);

    /**
     * 删除远程文件.
     *
     * @param remotePath 远程文件路径
     */
    void delete(String remotePath);

    /**
     * 重命名 / 移动远程文件.
     *
     * @param remotePath 原路径
     * @param newPath    新路径
     */
    void rename(String remotePath, String newPath);

    /**
     * 在远程创建目录（含父级目录）.
     *
     * @param remotePath 目录路径
     */
    void mkdir(String remotePath);

    /**
     * 判断远程路径是否存在.
     *
     * @param remotePath 远程路径
     */
    boolean exists(String remotePath);

    /**
     * 列出远程目录下的文件和子目录.
     *
     * @param remotePath 远程目录路径
     * @return 文件信息列表
     */
    List<FileInfo> list(String remotePath);

    /** 返回协议名称（{@code "ftp"} / {@code "sftp"}）. */
    String protocol();
}
