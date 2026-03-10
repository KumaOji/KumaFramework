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

package com.kuma.cloud.job.worker.subscribe;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kuma.cloud.job.common.utils.net.MyNetUtil;
import com.kuma.cloud.job.remote.protos.RegisterCausa;
import com.kuma.cloud.job.worker.common.constant.TransportTypeEnum;
import com.kuma.cloud.job.worker.common.grpc.strategies.StrategyCaller;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * WorkerSubscribeStarter
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
@Slf4j
public class WorkerSubscribeStarter {

    public static void start( String appName ) {
        ThreadFactory registerThreadFactory =
                new ThreadFactoryBuilder().setNameFormat("KmcJob-server-register-%d").build();
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
                new ScheduledThreadPoolExecutor(3, registerThreadFactory);
        RegisterCausa.WorkerSubscribeReq build =
                RegisterCausa.WorkerSubscribeReq.newBuilder()
                        .setAppName(appName)
                        .setScheduleTime(0)
                        .setWorkerIpAddress(MyNetUtil.address)
                        .setServerIpAddress(WorkerSubscribeManager.getCurrentServerIp())
                        .setSubscribeTimestamp(System.currentTimeMillis())
                        .build();
        StrategyCaller.call(TransportTypeEnum.REGISTER_TO_NAMESERVER, build);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            StrategyCaller.call(TransportTypeEnum.REGISTER_TO_NAMESERVER, build);

                        } catch (Exception e) {
                            log.error("worker register to nameServer error");
                        }
                    }
                },
                10,
                20,
                TimeUnit.SECONDS);
    }
}
