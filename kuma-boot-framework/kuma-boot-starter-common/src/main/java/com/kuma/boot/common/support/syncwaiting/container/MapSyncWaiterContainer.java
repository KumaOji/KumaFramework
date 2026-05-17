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

package com.kuma.boot.common.support.syncwaiting.container;

import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于 {@link Map} 实现的 {@link SyncWaiterContainer}，默认使用 {@link HashMap}。
 */
public class MapSyncWaiterContainer implements SyncWaiterContainer {

    protected Map<Object, SyncWaiter> map;

    public Map<Object, SyncWaiter> getMap() {
        return map;
    }

    public void setMap(Map<Object, SyncWaiter> map) {
        this.map = map;
    }

    public MapSyncWaiterContainer(Map<Object, SyncWaiter> map) {
        this.map = map;
    }

    public MapSyncWaiterContainer() {
        this(new HashMap<>());
    }

    @Override
    public boolean contains(Object key) {
        return map.containsKey(key);
    }

    @Override
    public SyncWaiter find(Object key) {
        return map.get(key);
    }

    @Override
    public void add(SyncWaiter waiter) {
        map.put(waiter.key(), waiter);
    }

    @Override
    public SyncWaiter remove(Object key) {
        return map.remove(key);
    }
}
