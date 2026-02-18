/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.support.syncwaiting.concept;

import com.kuma.boot.common.support.syncwaiting.caller.SyncCaller;
import com.kuma.boot.common.support.syncwaiting.configuration.SyncWaitingConfiguration;

/**
 * 管理类，提供统一接口。
 */
public interface SyncWaitingConcept {

    /**
     * 阻塞等待，无等待时间限制。
     * @param key 标识
     * @param caller 业务调用回调
     * @param <T> 值类型
     * @return 值
     */
    default <T> T waitSync(Object key, SyncCaller caller) {
        return waitSync(key, caller, 0);
    }

    /**
     * 阻塞等待，可限制等待时间。
     * @param key 标识
     * @param caller 业务调用回调
     * @param waitingTime 等待时间，ms
     * @param <T> 值类型
     * @return 值
     */
    default <T> T waitSync(Object key, SyncCaller caller, long waitingTime) {
        return waitSync(key, caller, waitingTime, 0);
    }

    /**
     * 阻塞等待，可限制等待超时时间和队列时间。
     * @param key 标识
     * @param caller 业务调用回调
     * @param waitingTime 等待时间，ms
     * @param queuingTime 队列时间，ms
     * @param <T> 值类型
     * @return 值
     */
    default <T> T waitSync(Object key, SyncCaller caller, long waitingTime, long queuingTime) {
        SyncWaitingConfiguration configuration =
                new SyncWaitingConfiguration.Builder()
                        .waitingTime(waitingTime)
                        .queuingTime(queuingTime)
                        .build();
        return waitSync(key, caller, configuration);
    }

    /**
     * 阻塞等待，基于等待配置 {@link SyncWaitingConfiguration}。
     * @param key 标识
     * @param caller 业务调用回调
     * @param configuration 等待配置 {@link SyncWaitingConfiguration}
     * @param <T> 值类型
     * @return 值
     */
    <T> T waitSync(Object key, SyncCaller caller, SyncWaitingConfiguration configuration);

    /**
     * 异步唤醒。
     * @param key 标识
     * @param value 值
     */
    void notifyAsync(Object key, Object value);

    /**
     * 某个 key 是否在等待中
     * @param key 标识
     * @return 如果在等待中则返回 true
     */
    boolean isWaiting(Object key);
}
