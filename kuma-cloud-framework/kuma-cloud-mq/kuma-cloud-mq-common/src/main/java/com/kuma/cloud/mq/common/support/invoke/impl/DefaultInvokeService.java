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

package com.kuma.cloud.mq.common.support.invoke.impl;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.cloud.mq.common.resp.MqCommonRespCode;
import com.kuma.cloud.mq.common.resp.MqException;
import com.kuma.cloud.mq.common.rpc.RpcMessageDto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.kuma.cloud.mq.common.support.invoke.InvokeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调用服务接口
 *
 * @author kuma
 * @since 2024.05
 */
public class DefaultInvokeService implements InvokeService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultInvokeService.class);

    /**
     * 请求序列号 map （1）这里后期如果要添加超时检测，可以添加对应的超时时间。 可以把这里调整为 map
     * <p>
     * key: seqId 唯一标识一个请求 value: 存入该请求最长的有效时间。用于定时删除和超时判断。
     *
     * @since 2024.05
     */
    private final ConcurrentHashMap<String, Long> requestMap;

    /**
     * 响应结果
     *
     * @since 2024.05
     */
    private final ConcurrentHashMap<String, RpcMessageDto> responseMap;

    public DefaultInvokeService() {
        requestMap = new ConcurrentHashMap<>();
        responseMap = new ConcurrentHashMap<>();

        final Runnable timeoutThread = new TimeoutCheckThread(requestMap, responseMap, this);
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(timeoutThread, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public com.kuma.cloud.mq.common.support.invoke.InvokeService addRequest(String seqId, long timeoutMills) {
        logger.info(
                "[Invoke] start add request for seqId: {}, timeoutMills: {}", seqId, timeoutMills);

        final long expireTime = System.currentTimeMillis() + timeoutMills;
        requestMap.putIfAbsent(seqId, expireTime);

        return this;
    }

    @Override
    public com.kuma.cloud.mq.common.support.invoke.InvokeService addResponse(String seqId, RpcMessageDto rpcResponse) {
        // 1. 判断是否有效
        Long expireTime = this.requestMap.get(seqId);
        // 如果为空，可能是这个结果已经超时了，被定时 job 移除之后，响应结果才过来。直接忽略
        if (ObjectUtils.isNull(expireTime)) {
            return this;
        }

        // 2. 判断是否超时
        if (System.currentTimeMillis() > expireTime) {
            logger.info("[Invoke] seqId:{} 信息已超时，直接返回超时结果。", seqId);
            rpcResponse = RpcMessageDto.timeout();
        }

        // 这里放入之前，可以添加判断。
        // 如果 seqId 必须处理请求集合中，才允许放入。或者直接忽略丢弃。
        responseMap.putIfAbsent(seqId, rpcResponse);
        logger.info("[Invoke] 获取结果信息，seqId: {}, rpcResponse: {}", seqId, JSON.toJSON(rpcResponse));
        logger.info("[Invoke] seqId:{} 信息已经放入，通知所有等待方", seqId);

        // 移除对应的 requestMap
        requestMap.remove(seqId);
        logger.info("[Invoke] seqId:{} remove from request map", seqId);

        // 唤醒所有等待方
        synchronized (this) {
            this.notifyAll();
        }

        return this;
    }

    @Override
    public RpcMessageDto getResponse(String seqId) {
        try {
            while (true) {
                synchronized (this) {
                    RpcMessageDto rpcResponse = this.responseMap.get(seqId);
                    if (ObjectUtils.isNotNull(rpcResponse)) {
                        logger.info("[Invoke] seq {} 对应结果已经获取: {}", seqId, rpcResponse);
                        return rpcResponse;
                    }
                    // 计算剩余超时时间，避免无限等待
                    Long expireTime = this.requestMap.get(seqId);
                    if (expireTime == null) {
                        // requestMap 已被 TimeoutCheckThread 清除，但 responseMap 尚未写入，短暂自旋
                        logger.info("[Invoke] seq {} requestMap 已移除，等待 responseMap 写入", seqId);
                        this.wait(50);
                        continue;
                    }
                    long remainingMs = expireTime - System.currentTimeMillis();
                    if (remainingMs <= 0) {
                        logger.info("[Invoke] seq {} 已超时，返回超时结果", seqId);
                        this.responseMap.putIfAbsent(seqId, RpcMessageDto.timeout());
                        this.requestMap.remove(seqId);
                        return RpcMessageDto.timeout();
                    }
                    logger.info("[Invoke] seq {} 对应结果为空，进入等待 {}ms", seqId, remainingMs);
                    this.wait(remainingMs);
                    logger.info("[Invoke] {} wait has notified!", seqId);
                }
            }
        } catch (InterruptedException e) {
            logger.error("获取响应异常", e);
            throw new MqException(MqCommonRespCode.RPC_GET_RESP_FAILED);
        }
    }

    @Override
    public boolean remainsRequest() {
        return this.requestMap.size() > 0;
    }
}
