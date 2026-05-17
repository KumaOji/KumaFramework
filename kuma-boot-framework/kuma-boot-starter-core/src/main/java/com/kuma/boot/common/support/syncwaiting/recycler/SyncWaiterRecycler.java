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

package com.kuma.boot.common.support.syncwaiting.recycler;

import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;

/**
 * {@link SyncWaiter} 回收器。
 */
public interface SyncWaiterRecycler {

    /**
     * 回收一个 {@link SyncWaiter}。
     * @param waiter 被回收的 {@link SyncWaiter}
     */
    void recycle(SyncWaiter waiter);

    /**
     * 重新使用一个 {@link SyncWaiter}， 如果不存在可以重新使用的则返回 null。
     * @return 重新使用的 {@link SyncWaiter} 或 null
     */
    SyncWaiter reuse();
}
