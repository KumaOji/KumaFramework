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

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.cloud.mq.common.rpc.RpcMessageDto;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 超时检测线程
 *
 * @author kuma
 * @since 2024.05
 */
public class TimeoutCheckThread implements Runnable {

    /**
     * 请求信息
     *
     * @since 2024.05
     */
    private final ConcurrentHashMap<String, Long> requestMap;

    /**
     * 请求信息
     *
     * @since 2024.05
     */
    private final ConcurrentHashMap<String, RpcMessageDto> responseMap;

    /**
     * 锁对象，用于唤醒 getResponse() 中的等待线程
     *
     * @since 2024.05
     */
    private final Object lock;

    /**
     * 新建
     *
     * @param requestMap  请求 Map
     * @param responseMap 结果 map
     * @param lock        与 DefaultInvokeService 共享的锁对象
     * @since 2024.05
     */
    public TimeoutCheckThread(
            ConcurrentHashMap<String, Long> requestMap,
            ConcurrentHashMap<String, RpcMessageDto> responseMap,
            Object lock) {
        ArgUtils.notNull(requestMap, "requestMap");
        this.requestMap = requestMap;
        this.responseMap = responseMap;
        this.lock = lock;
    }

    @Override
    public void run() {
        boolean hasTimeout = false;
        for (Map.Entry<String, Long> entry : requestMap.entrySet()) {
            long expireTime = entry.getValue();
            long currentTime = System.currentTimeMillis();

            if (currentTime > expireTime) {
                final String key = entry.getKey();
                // 结果设置为超时，从请求 map 中移除
                responseMap.putIfAbsent(key, RpcMessageDto.timeout());
                requestMap.remove(key);
                hasTimeout = true;
            }
        }
        // 有超时请求时唤醒所有等待 getResponse() 的线程
        if (hasTimeout) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }
}
