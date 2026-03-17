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

package com.kuma.cloud.rpc.common.common.support.invoke.impl;

import com.kuma.cloud.rpc.common.common.rpc.domain.RpcResponse;
import com.kuma.cloud.rpc.common.common.rpc.domain.impl.RpcResponseFactory;
import com.kuma.cloud.rpc.common.common.support.invoke.InvokeManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调用服务接口
 * @author kuma
 * @since 2024.06
 */
public class DefaultInvokeManager implements InvokeManager {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultInvokeManager.class);

    /**
     * 请求序列号 map
     * （1）这里后期如果要添加超时检测，可以添加对应的超时时间。
     * 可以把这里调整为 map
     *
     * key: seqId 唯一标识一个请求
     * value: 存入该请求最长的有效时间。用于定时删除和超时判断。
     * @since 2024.06
     */
    private final ConcurrentHashMap<String, Long> requestMap;

    /**
     * 响应结果
     * @since 2024.06
     */
    private final ConcurrentHashMap<String, RpcResponse> responseMap;

    public DefaultInvokeManager() {
        requestMap = new ConcurrentHashMap<>();
        responseMap = new ConcurrentHashMap<>();

        final Runnable timeoutThread = new InvokeTimeoutCheckThread(requestMap, responseMap);
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(timeoutThread, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public InvokeManager addRequest(String seqId, long timeoutMills) {
        final long expireTime = System.currentTimeMillis() + timeoutMills;
        requestMap.putIfAbsent(seqId, expireTime);
        return this;
    }

    @Override
    public InvokeManager addResponse(String seqId, RpcResponse rpcResponse) {
        // 1. 判断是否有效，为空说明已超时被清除，直接忽略
        Long expireTime = this.requestMap.get(seqId);
        if (expireTime == null) {
            LOG.warn("[Invoke] seqId: {} has been removed, maybe timeout!", seqId);
            return this;
        }

        // 2. 判断是否超时
        if (System.currentTimeMillis() > expireTime) {
            rpcResponse = RpcResponseFactory.timeout();
        }

        // 3. 存入响应结果，通知所有等待方
        responseMap.putIfAbsent(seqId, rpcResponse);
        synchronized (this) {
            this.notifyAll();
        }

        return this;
    }

    @Override
    public RpcResponse getResponse(String seqId) {
        try {
            RpcResponse rpcResponse = this.responseMap.get(seqId);
            if (rpcResponse == null) {
                // 进入等待，直到 addResponse 通知
                synchronized (this) {
                    while (this.responseMap.get(seqId) == null) {
                        this.wait();
                    }
                }
                rpcResponse = this.responseMap.get(seqId);
            }
            // 移除 request
            this.requestMap.remove(seqId);
            return rpcResponse;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("[Invoke] get response meet InterruptedException ex", e);
            return RpcResponseFactory.interrupted();
        } catch (Exception e) {
            LOG.error("[Invoke] get response meet ex", e);
            return RpcResponseFactory.interrupted();
        }
    }

    @Override
    public boolean remainsRequest() {
        return this.requestMap.size() > 0;
    }

    @Override
    public DefaultInvokeManager removeReqAndResp(String seqId) {
        //        LOG.info("[Invoke] remove the request and response for seqId: {}", seqId);
        // 移除这个 key
        this.requestMap.remove(seqId);
        this.responseMap.remove(seqId);
        return this;
    }
}
