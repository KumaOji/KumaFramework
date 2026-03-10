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

package com.kuma.cloud.job.worker;

import com.google.common.collect.Lists;
import com.kuma.cloud.job.worker.common.KmcJobWorkerConfig;
import com.kuma.cloud.job.worker.common.executor.ExecutorManager;
import com.kuma.cloud.job.worker.common.grpc.RpcInitializer;
import com.kuma.cloud.job.worker.core.discover.KmcJobServerDiscoverService;
import com.kuma.cloud.job.worker.core.schedule.WorkerHealthReporter;
import com.kuma.cloud.job.worker.processor.ProcessorLoader;
import com.kuma.cloud.job.worker.processor.KmcJobProcessorLoader;
import com.kuma.cloud.job.worker.processor.factory.BuiltInDefaultProcessorFactory;
import com.kuma.cloud.job.worker.processor.factory.ProcessorFactory;
import com.kuma.cloud.job.worker.subscribe.WorkerSubscribeStarter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端启动类
 */
@Slf4j
public class KmcJobWorker {

    KmcJobWorkerConfig config;

    public KmcJobWorker(KmcJobWorkerConfig config) {
        this.config = config;
    }

    public void init() {
        log.info("[KmcJob] starting ...");

        // init rpc
        RpcInitializer rpcInitializer =
                new RpcInitializer(
                        config.getServerPort(),
                        config.getPort(),
                        config.getServerAddress(),
                        config.getNameServerAddress());
        rpcInitializer.initRpcStrategies();
        rpcInitializer.initRpcServer(config);

        KmcJobServerDiscoverService kmcJobServerDiscoverService =
                new KmcJobServerDiscoverService(config);

        try {
            // subscribe to nameServer
            WorkerSubscribeStarter.start(config.getAppName());

            // get appId
            kmcJobServerDiscoverService.assertApp();

            // init ThreadPool
            ExecutorManager.initExecutorManager();

            // init processorLoader for handler task
            ProcessorLoader processorLoader = buildProcessorLoader();
            KmcJobWorkerConfig.setProcessorLoader(processorLoader);

            // connect server
            kmcJobServerDiscoverService.heartbeatCheck(ExecutorManager.getHeartbeatExecutor());

            // init health reporter
            ExecutorManager.getHealthReportExecutor()
                    .scheduleAtFixedRate(
                            new WorkerHealthReporter(kmcJobServerDiscoverService, config),
                            0,
                            config.getHealthReportInterval(),
                            TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("[KmcJob] start error");
        }
    }

    private ProcessorLoader buildProcessorLoader() {
        List<ProcessorFactory> customPF =
                Optional.ofNullable(config.getProcessorFactoryList())
                        .orElse(Collections.emptyList());
        List<ProcessorFactory> finalPF = Lists.newArrayList(customPF);

        finalPF.add(new BuiltInDefaultProcessorFactory());

        return new KmcJobProcessorLoader(finalPF);
    }

    public void destroy() {
        ExecutorManager.shutdown();
    }
}
