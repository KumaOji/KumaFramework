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

package com.kuma.cloud.job.server.common.grpc;

import com.kuma.cloud.job.common.constant.RemoteConstant;
import com.kuma.cloud.job.remote.protos.CommonCausa;
import com.kuma.cloud.job.remote.protos.ScheduleCausa;
import com.kuma.cloud.job.server.extension.singletonpool.GrpcStubSingletonPool;
import com.kuma.cloud.remote.api.ScheduleGrpc;
import org.springframework.stereotype.Component;

/**
 * ServerScheduleJobRpcClient
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
@Component
public class ServerScheduleJobRpcClient implements RpcServiceCaller {

    @Override
    public Object call( Object params ) {
        ScheduleCausa.ServerScheduleJobReq req = (ScheduleCausa.ServerScheduleJobReq) params;
        ScheduleGrpc.ScheduleBlockingStub stubSingleton =
                GrpcStubSingletonPool.getStubSingleton(
                        req.getWorkerAddress(),
                        ScheduleGrpc.class,
                        ScheduleGrpc.ScheduleBlockingStub.class,
                        RemoteConstant.WORKER);
        CommonCausa.Response response = stubSingleton.serverScheduleJob(req);
        return response;
    }
}
