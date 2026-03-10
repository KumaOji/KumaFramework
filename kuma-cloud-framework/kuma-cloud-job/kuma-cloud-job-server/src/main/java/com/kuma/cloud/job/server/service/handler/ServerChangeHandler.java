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

package com.kuma.cloud.job.server.service.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuma.cloud.job.remote.protos.CommonCausa;
import com.kuma.cloud.job.remote.protos.ServerDiscoverCausa;
import com.kuma.cloud.job.server.persistence.domain.AppInfo;
import com.kuma.cloud.job.server.persistence.mapper.AppInfoMapper;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ServerChangeHandler
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
@Component
@Slf4j
public class ServerChangeHandler implements RpcHandler {

    @Autowired
    AppInfoMapper appInfoMapper;

    @Override
    public void handle( Object req, StreamObserver<CommonCausa.Response> responseObserver ) {
        ServerDiscoverCausa.ServerChangeReq serverChangeReq =
                (ServerDiscoverCausa.ServerChangeReq) req;
        AppInfo appInfo =
                AppInfo.builder().currentServer(serverChangeReq.getTargetServer()).build();
        appInfoMapper.update(
                appInfo,
                new QueryWrapper<AppInfo>()
                        .lambda()
                        .eq(AppInfo::getAppName, serverChangeReq.getAppName()));
        log.info(
                "[KmcJobServerChange] app :{} change to new server :{}",
                serverChangeReq.getAppName(),
                serverChangeReq.getTargetServer());
        CommonCausa.Response build = CommonCausa.Response.newBuilder().setCode(200).build();
        responseObserver.onNext(build);
        responseObserver.onCompleted();
    }
}
