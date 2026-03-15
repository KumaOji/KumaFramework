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

package com.kuma.cloud.ccsr.client.client.invoke;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import com.kuma.cloud.ccsr.api.event.EventType;
import com.kuma.cloud.ccsr.api.grpc.auto.Metadata;
import com.kuma.cloud.ccsr.api.grpc.auto.MetadataDeleteRequest;
import com.kuma.cloud.ccsr.api.grpc.auto.MetadataReadRequest;
import com.kuma.cloud.ccsr.api.grpc.auto.MetadataWriteRequest;
import com.kuma.cloud.ccsr.api.grpc.auto.Response;
import com.kuma.cloud.ccsr.client.client.CcsrClient;
import com.kuma.cloud.ccsr.client.context.CcsrContext;
import com.kuma.cloud.ccsr.client.option.GrpcOption;
import com.kuma.cloud.ccsr.client.remote.RpcClient;
import com.kuma.cloud.ccsr.client.remote.grpc.GrpcClient;
import com.kuma.cloud.ccsr.client.request.Payload;
import com.kuma.cloud.ccsr.common.enums.RaftGroup;
import com.kuma.cloud.ccsr.common.exception.CcsrClientException;
import com.kuma.cloud.ccsr.common.exception.InitializationException;
import com.kuma.cloud.ccsr.spi.SpiExtensionFactory;
import org.apache.commons.collections4.CollectionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;

/**
 * GrpcInvoker
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
public class GrpcInvoker extends AbstractInvoker<Message, GrpcOption> {

    private final GrpcClient grpcClient;

    public GrpcInvoker( CcsrClient client ) {
        super(client);
        GrpcOption grpcOption = (GrpcOption) client.getOption();
        if (grpcOption == null) {
            throw new InitializationException("Init Grpc Invoker fail, GrpcOption is empty.");
        }
        if (StringUtils.isBlank(client.getNamespace())) {
            throw new IllegalArgumentException("Init Grpc Invoker fail, Namespace is null.");
        }
        if (CollectionUtils.isEmpty(grpcOption.getServerAddresses())) {
            throw new IllegalArgumentException("Init Grpc Invoker fail, ServerAddresses is empty.");
        }
        grpcClient = (GrpcClient) SpiExtensionFactory.getExtension(protocol(), RpcClient.class);
        grpcClient.init(client.getNamespace(), grpcOption.getServerAddresses());
    }

    public Response innerInvoke( Message request, EventType eventType ) {
        return switch (eventType) {
            case PUT -> grpcClient.put((MetadataWriteRequest) request);
            case DELETE -> grpcClient.delete((MetadataDeleteRequest) request);
            case GET -> grpcClient.get((MetadataReadRequest) request);
            default -> throw new IllegalArgumentException("Unsupported event type: " + eventType);
        };
    }

    @Override
    public Response invoke( CcsrContext context, Payload request ) {
        GrpcOption option = getOption();
        Message message = convert(context, option, request);
        return innerInvoke(message, request.getEventType());
    }

    @Override
    public String protocol() {
        return "grpc";
    }

    @Override
    public Message convert( CcsrContext context, GrpcOption option, Payload request ) {
        return switch (request.getEventType()) {
            case PUT -> MetadataWriteRequest.newBuilder()
                    .setRaftGroup(RaftGroup.CONFIG_CENTER_GROUP.getName())
                    .setNamespace(context.getNamespace())
                    .setGroup(request.getGroup())
                    .setTag(request.getTag())
                    .setDataId(request.getDataId())
                    .setMetadata(buildMetadata(context, option, request))
                    .build();
            case DELETE -> MetadataDeleteRequest.newBuilder()
                    .setRaftGroup(RaftGroup.CONFIG_CENTER_GROUP.getName())
                    .setNamespace(context.getNamespace())
                    .setGroup(request.getGroup())
                    .setTag(request.getTag())
                    .setDataId(request.getDataId())
                    .build();
            case GET -> MetadataReadRequest.newBuilder()
                    .setRaftGroup(RaftGroup.CONFIG_CENTER_GROUP.getName())
                    .setNamespace(context.getNamespace())
                    .setGroup(request.getGroup())
                    .setTag(request.getTag())
                    .setDataId(request.getDataId())
                    .build();
            default -> throw new IllegalArgumentException(
                    "Unsupported event type: " + request.getEventType());
        };
    }

    private Metadata buildMetadata( CcsrContext context, GrpcOption option, Payload request ) {
        return Metadata.newBuilder()
                .setNamespace(context.getNamespace())
                .setGroup(request.getGroup())
                .setTag(request.getTag())
                .setDataId(request.getDataId())
                .setDataKey(request.getConfigData().key())
                .setType(request.getType())
                .setContent(context.getConfigDataString())
                .setMd5(context.getMd5())
                .setGmtCreate(Timestamp.newBuilder().setSeconds(request.getGmtCreate()))
                .setGmtModified(Timestamp.newBuilder().setSeconds(request.getGmtModified()))
                //                .putAllExt(request.getExt())
                .build();
    }

    @Override
    public void shutdown() throws CcsrClientException {
        grpcClient.shutdown();
    }
}
