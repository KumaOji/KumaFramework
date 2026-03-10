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

package com.kuma.cloud.job.server.service;

import com.kuma.cloud.job.remote.protos.CommonCausa;
import com.kuma.cloud.job.remote.protos.ServerDiscoverCausa;
import com.kuma.cloud.job.server.service.handler.AppInfoHandler;
import com.kuma.cloud.job.server.service.handler.HeartbeatHandler;
import com.kuma.cloud.job.server.service.handler.PongHandler;
import com.kuma.cloud.job.server.service.handler.ServerChangeHandler;
import com.kuma.cloud.remote.api.ServerDiscoverGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ServerDiscoverGrpcService
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
@GrpcService
@Slf4j
public class ServerDiscoverGrpcService extends ServerDiscoverGrpc.ServerDiscoverImplBase {

    @Autowired
    HeartbeatHandler heartbeatHandler;
    @Autowired
    AppInfoHandler appInfoHandler;
    @Autowired
    PongHandler pongHandler;
    @Autowired
    ServerChangeHandler serverChangeHandler;

    public void heartbeatCheck(
            ServerDiscoverCausa.HeartbeatCheck request,
            StreamObserver<CommonCausa.Response> responseObserver ) {
        heartbeatHandler.handle(request, responseObserver);
    }

    public void assertApp(
            ServerDiscoverCausa.AppName request,
            StreamObserver<CommonCausa.Response> responseObserver ) {
        appInfoHandler.handle(request, responseObserver);
    }

    public void pingServer(
            ServerDiscoverCausa.Ping request,
            StreamObserver<CommonCausa.Response> responseObserver ) {
        pongHandler.handle(request, responseObserver);
    }

    public void serverChange(
            ServerDiscoverCausa.ServerChangeReq request,
            StreamObserver<CommonCausa.Response> responseObserver ) {
        serverChangeHandler.handle(request, responseObserver);
    }
}
