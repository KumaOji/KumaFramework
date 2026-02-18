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

package com.kuma.boot.common.support.syncwaiting.container;

import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;

/**
 * 存放等待中的 {@link SyncWaiter} 的容器。
 */
public interface SyncWaiterContainer {

    /**
     * 判断是否存在对应 key 的等待中的 {@link SyncWaiter}。
     * @param key 标识
     * @return 如果存在则返回 true
     */
    boolean contains(Object key);

    /**
     * 根据 key 查找 {@link SyncWaiter}，不存在则返回 null。
     * @param key 标识
     * @return 找到的 {@link SyncWaiter} 或 null
     */
    SyncWaiter find(Object key);

    /**
     * 添加一个等待中的 {@link SyncWaiter}。
     * @param waiter 添加的 {@link SyncWaiter}
     */
    void add(SyncWaiter waiter);

    /**
     * 移除一个等待中的 {@link SyncWaiter}。
     * @param key 标识
     * @return 被移除的 {@link SyncWaiter}
     */
    SyncWaiter remove(Object key);
}
