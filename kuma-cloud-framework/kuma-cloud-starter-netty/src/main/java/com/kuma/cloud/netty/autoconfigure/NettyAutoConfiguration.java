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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Netty Server Spring Boot 自动配置
 *
 * <p>激活条件：classpath 中存在 {@link ServerBootstrap}（即引入了 netty-all），
 * 且 Spring 容器中存在用户自定义的 {@link ChannelInitializer} Bean。</p>
 *
 * <p>使用方只需注册一个 {@code ChannelInitializer<SocketChannel>} Bean，
 * 框架即自动启动 Netty Server：</p>
 * <pre>{@code
 * @Bean
 * public ChannelInitializer<SocketChannel> myInitializer() {
 *     return new ChannelInitializer<>() {
 *         @Override
 *         protected void initChannel(SocketChannel ch) {
 *             ch.pipeline().addLast(new MyHandler());
 *         }
 *     };
 * }
 * }</pre>
 *
 * @author kuma
 * @since 2025.01
 */
@AutoConfiguration
@ConditionalOnClass(ServerBootstrap.class)
@EnableConfigurationProperties(NettyServerProperties.class)
public class NettyAutoConfiguration {

    @Bean
    @ConditionalOnBean(ChannelInitializer.class)
    @ConditionalOnMissingBean(NettyServer.class)
    public NettyServer nettyServer(NettyServerProperties properties,
                                   ChannelInitializer<SocketChannel> channelInitializer) {
        return new NettyServer(properties, channelInitializer);
    }
}
