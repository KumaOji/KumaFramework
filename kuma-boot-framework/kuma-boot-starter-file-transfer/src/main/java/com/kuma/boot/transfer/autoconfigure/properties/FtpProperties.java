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
 * FTP 连接配置.
 *
 * @author kuma
 */
@Data
public class FtpProperties {

    /** 是否启用 FTP 客户端. */
    private boolean enabled = false;

    private String host = "localhost";

    private int port = 21;

    private String username = "anonymous";

    private String password = "";

    /** 控制连接字符集（GBK / UTF-8）. */
    private String encoding = "UTF-8";

    /** 是否使用被动模式（NAT / 防火墙环境推荐 true）. */
    private boolean passiveMode = true;

    /** 连接超时毫秒数. */
    private int connectTimeoutMs = 5_000;

    /** 数据传输超时毫秒数. */
    private int dataTimeoutMs = 30_000;

    /** 远程根路径，所有操作路径均以此为基准. */
    private String basePath = "/";
}
