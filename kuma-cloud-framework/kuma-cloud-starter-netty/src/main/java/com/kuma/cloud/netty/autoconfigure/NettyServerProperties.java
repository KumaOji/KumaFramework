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

package com.kuma.cloud.netty.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Netty Server 配置属性
 *
 * <pre>
 * kuma:
 *   netty:
 *     port: 8888
 *     boss-threads: 1
 *     worker-threads: 0      # 0 = Netty 默认值（CPU 核心数 × 2）
 *     backlog: 128
 *     reader-idle-seconds: 0 # 0 = 不检测读空闲
 *     logging-enabled: false
 * </pre>
 *
 * @author kuma
 * @since 2025.01
 */
@ConfigurationProperties(prefix = "kuma.netty")
public class NettyServerProperties {

    /** 监听端口 */
    private int port = 8888;

    /** Boss 线程数，负责接收连接，通常设为 1 */
    private int bossThreads = 1;

    /** Worker 线程数，负责读写，0 表示使用 Netty 默认值（CPU 核心数 × 2） */
    private int workerThreads = 0;

    /** TCP accept 队列长度 */
    private int backlog = 128;

    /** 读空闲超时秒数，0 表示不启用心跳检测 */
    private int readerIdleSeconds = 0;

    /** 是否开启 Pipeline 日志（LoggingHandler） */
    private boolean loggingEnabled = false;

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public int getBossThreads() { return bossThreads; }
    public void setBossThreads(int bossThreads) { this.bossThreads = bossThreads; }

    public int getWorkerThreads() { return workerThreads; }
    public void setWorkerThreads(int workerThreads) { this.workerThreads = workerThreads; }

    public int getBacklog() { return backlog; }
    public void setBacklog(int backlog) { this.backlog = backlog; }

    public int getReaderIdleSeconds() { return readerIdleSeconds; }
    public void setReaderIdleSeconds(int readerIdleSeconds) { this.readerIdleSeconds = readerIdleSeconds; }

    public boolean isLoggingEnabled() { return loggingEnabled; }
    public void setLoggingEnabled(boolean loggingEnabled) { this.loggingEnabled = loggingEnabled; }
}
