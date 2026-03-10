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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

/**
 * Netty Server Spring 生命周期管理
 *
 * <p>随 Spring 容器启动/关闭，异步绑定端口，不阻塞应用启动。
 * 使用方需向 Spring 容器注册一个 {@link ChannelInitializer} Bean 来定义 Pipeline。</p>
 *
 * @author kuma
 * @since 2025.01
 */
public class NettyServer implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private final NettyServerProperties properties;
    private final ChannelInitializer<SocketChannel> channelInitializer;

    private volatile boolean running = false;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private Channel serverChannel;

    public NettyServer(NettyServerProperties properties,
                       ChannelInitializer<SocketChannel> channelInitializer) {
        this.properties = properties;
        this.channelInitializer = channelInitializer;
    }

    @Override
    public void start() {
        bossGroup = new NioEventLoopGroup(properties.getBossThreads());
        workerGroup = new NioEventLoopGroup(properties.getWorkerThreads());

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, properties.getBacklog())
                .childHandler(channelInitializer);

        // 异步绑定，不阻塞 Spring 启动
        bootstrap.bind(properties.getPort()).addListener(future -> {
            if (future.isSuccess()) {
                serverChannel = ((io.netty.channel.ChannelFuture) future).channel();
                running = true;
                log.info("[NettyServer] started on port {}", properties.getPort());
            } else {
                log.error("[NettyServer] failed to bind port {}", properties.getPort(), future.cause());
                shutdownGroups();
            }
        });
    }

    @Override
    public void stop() {
        log.info("[NettyServer] stopping...");
        running = false;
        if (serverChannel != null) {
            serverChannel.close().syncUninterruptibly();
        }
        shutdownGroups();
        log.info("[NettyServer] stopped");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /** 在所有普通 Bean 初始化完成后再启动，确保 ChannelHandler 已就绪 */
    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    private void shutdownGroups() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().syncUninterruptibly();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().syncUninterruptibly();
        }
    }
}
