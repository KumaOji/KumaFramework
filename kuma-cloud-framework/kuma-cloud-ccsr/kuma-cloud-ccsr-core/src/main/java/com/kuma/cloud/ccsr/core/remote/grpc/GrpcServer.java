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

package com.kuma.cloud.ccsr.core.remote.grpc;

import com.kuma.cloud.ccsr.common.config.CcsrConfig;
import com.kuma.cloud.ccsr.core.remote.AbstractRpcServer;
import com.kuma.cloud.ccsr.spi.Join;
import com.kuma.cloud.ccsr.spi.SpiExtensionFactory;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author kuma
 */
@Join(order = 1, isSingleton = true)
public class GrpcServer extends AbstractRpcServer {

    private Server server;

    private CcsrConfig config;

    public GrpcServer() {
        // 必须要有，用于SPI加载初始化
    }

    public GrpcServer(CcsrConfig config) {
        init(config);
    }

    @Override
    public void init(CcsrConfig config) {
        this.config = config;
    }

    @Override
    public int port() {
        return config.getPort();
    }

    @Override
    public synchronized void startServer() throws IOException {
        CcsrConfig.GrpcConfig grpcConfig = config.getGrpcConfig();
        if (grpcConfig == null) {
            throw new IllegalArgumentException("Ccsr gRPC server config can't be null");
        }

        List<GrpcService> services = SpiExtensionFactory.getExtensions(GrpcService.class);
        NettyServerBuilder builder =
                NettyServerBuilder.forPort(port())
                        .compressorRegistry(CompressorRegistry.getDefaultInstance())
                        .decompressorRegistry(DecompressorRegistry.getDefaultInstance())
                        .maxInboundMessageSize(grpcConfig.getMaxInboundMessageSize())
                        .keepAliveTime(grpcConfig.getKeepAliveTime(), TimeUnit.MILLISECONDS)
                        .keepAliveTimeout(grpcConfig.getKeepAliveTimeout(), TimeUnit.MILLISECONDS)
                        .permitKeepAliveTime(
                                grpcConfig.getPermitKeepAliveTime(), TimeUnit.MILLISECONDS);

        // TODO gRPC允许再服务调用的时候，为每个服务定制责任链拦截器
        services.forEach(builder::addService);

        server = builder.build();
        server.start();
    }

    @Override
    public void stopServer() {
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Override
    public void await() {
        if (server != null) {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
