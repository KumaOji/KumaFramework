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

package com.kuma.cloud.job.worker.common.grpc.strategies.strategy;

import com.kuma.cloud.job.common.constant.RemoteConstant;
import com.kuma.cloud.job.common.exception.KmcJobException;
import com.kuma.cloud.job.common.utils.CommonUtils;
import com.kuma.cloud.job.remote.protos.CommonCausa;
import com.kuma.cloud.job.remote.protos.ServerDiscoverCausa;
import com.kuma.cloud.job.worker.common.constant.TransportTypeEnum;
import com.kuma.cloud.job.worker.common.grpc.RpcInitializer;
import com.kuma.cloud.job.worker.common.grpc.strategies.GrpcStrategy;
import com.kuma.cloud.job.worker.subscribe.WorkerSubscribeManager;
import com.kuma.cloud.remote.api.ServerDiscoverGrpc;
import io.grpc.ManagedChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * AssertAppRpcService
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
@Slf4j
public class AssertAppRpcService implements GrpcStrategy<TransportTypeEnum> {

    List<ServerDiscoverGrpc.ServerDiscoverBlockingStub> serverDiscoverStubs = new ArrayList<>();

    @Override
    public void init() {
        HashMap<String, ManagedChannel> ip2ChannelsMap = RpcInitializer.getIp2ChannelsMap();
        for (ManagedChannel channel : ip2ChannelsMap.values()) {
            serverDiscoverStubs.add(ServerDiscoverGrpc.newBlockingStub(channel));
        }
    }

    @Override
    public Object execute( Object params ) {
        ServerDiscoverCausa.AppName appNameInfo = (ServerDiscoverCausa.AppName) params;
        for (ServerDiscoverGrpc.ServerDiscoverBlockingStub serverDiscoverStub :
                serverDiscoverStubs) {
            try {
                if (WorkerSubscribeManager.isSplit()) {
                    // 需要分组，依附于新的server，优先选择最小连接的server
                    appNameInfo =
                            ServerDiscoverCausa.AppName.newBuilder()
                                    .setAppName(appNameInfo.getAppName())
                                    .setSubAppName(WorkerSubscribeManager.getSubAppName())
                                    .setTargetServer(
                                            WorkerSubscribeManager.getServerIpList().get(0))
                                    .build();
                    log.info("change server to ip:{}", appNameInfo.getTargetServer());
                }

                // 重置状态，防止多次分组
                WorkerSubscribeManager.setSplitStatus(false);

                ServerDiscoverCausa.AppName finalAppNameInfo = appNameInfo;
                CommonCausa.Response response =
                        CommonUtils.executeWithRetry0(
                                () -> serverDiscoverStub.assertApp(finalAppNameInfo));
                if (response.getCode() == RemoteConstant.SUCCESS) {
                    return response.getWorkInfo();
                } else {
                    log.error(
                            "[KmcJobWorker] assert appName failed, this appName is invalid, please register the appName  first.");
                    throw new KmcJobException(response.getMessage());
                }
            } catch (Exception e) {
                log.error("[KmcJobWorker] grpc error");
            }
        }
        log.error("[KmcJobWorker] no available server");
        throw new KmcJobException("no server available");
    }

    @Override
    public TransportTypeEnum getTypeEnumFromStrategyClass() {
        return TransportTypeEnum.ASSERT_APP;
    }
}
