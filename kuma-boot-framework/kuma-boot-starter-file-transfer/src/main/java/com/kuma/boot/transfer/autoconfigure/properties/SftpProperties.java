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

package com.kuma.boot.transfer.autoconfigure.properties;

import lombok.Data;

/**
 * SFTP（SSH File Transfer Protocol）连接配置.
 *
 * @author kuma
 */
@Data
public class SftpProperties {

    /** 是否启用 SFTP 客户端. */
    private boolean enabled = false;

    private String host = "localhost";

    private int port = 22;

    private String username;

    /** 密码鉴权（与 privateKey 二选一）. */
    private String password;

    /** 私钥文件路径（如 ~/.ssh/id_rsa），优先于密码. */
    private String privateKey;

    /** 私钥口令（privateKey 有加密时必填）. */
    private String privateKeyPassphrase;

    /** known_hosts 文件路径；为空时由 strictHostKeyChecking 决定行为. */
    private String knownHostsFile;

    /**
     * 是否严格检查 Host Key.
     * <ul>
     *   <li>{@code true}  — 未知主机拒绝连接（生产推荐）</li>
     *   <li>{@code false} — 自动接受（开发/内网环境）</li>
     * </ul>
     */
    private boolean strictHostKeyChecking = false;

    /** SSH 会话建立超时毫秒数. */
    private int connectTimeoutMs = 5_000;

    /** 远程根路径，所有操作路径均以此为基准. */
    private String basePath = "/";
}
