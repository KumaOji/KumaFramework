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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 文件传输模块配置（{@code kuma.boot.transfer.*}）.
 *
 * <pre>
 * kuma:
 *   boot:
 *     transfer:
 *       ftp:
 *         enabled: true
 *         host: 192.168.1.100
 *         username: ftpuser
 *         password: secret
 *       sftp:
 *         enabled: true
 *         host: 192.168.1.200
 *         username: sftpuser
 *         private-key: /home/app/.ssh/id_rsa
 * </pre>
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = FileTransferProperties.PREFIX)
public class FileTransferProperties {

    public static final String PREFIX = "kuma.boot.transfer";

    @NestedConfigurationProperty
    private FtpProperties ftp = new FtpProperties();

    @NestedConfigurationProperty
    private SftpProperties sftp = new SftpProperties();
}
