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

package com.kuma.boot.transfer.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.transfer.autoconfigure.properties.FileTransferProperties;
import com.kuma.boot.transfer.ftp.FtpFileTransferClient;
import com.kuma.boot.transfer.sftp.SftpFileTransferClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 文件传输自动配置.
 *
 * <p>FTP 和 SFTP 客户端各自独立，通过 {@code enabled} 开关控制，可同时存在。
 * 若两者都启用，注入时请使用 {@code @Qualifier("ftpFileTransferClient")} /
 * {@code @Qualifier("sftpFileTransferClient")} 区分。
 *
 * @author kuma
 */
@AutoConfiguration
@EnableConfigurationProperties(FileTransferProperties.class)
public class FileTransferAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(FileTransferAutoConfiguration.class, StarterNameConstants.FILE_TRANSFER_STARTER);
    }

    @Bean("ftpFileTransferClient")
    @ConditionalOnMissingBean(name = "ftpFileTransferClient")
    @ConditionalOnProperty(prefix = FileTransferProperties.PREFIX + ".ftp", name = "enabled", havingValue = "true")
    public FtpFileTransferClient ftpFileTransferClient(FileTransferProperties properties) {
        return new FtpFileTransferClient(properties.getFtp());
    }

    @Bean("sftpFileTransferClient")
    @ConditionalOnMissingBean(name = "sftpFileTransferClient")
    @ConditionalOnProperty(prefix = FileTransferProperties.PREFIX + ".sftp", name = "enabled", havingValue = "true")
    public SftpFileTransferClient sftpFileTransferClient(FileTransferProperties properties) {
        return new SftpFileTransferClient(properties.getSftp());
    }
}
