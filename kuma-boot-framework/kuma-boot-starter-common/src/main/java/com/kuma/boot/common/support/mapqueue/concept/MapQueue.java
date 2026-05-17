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

package com.kuma.boot.common.support.mapqueue.concept;

import java.util.Map;
import java.util.Queue;

public interface MapQueue<K, V> {

    Map<K, V> map();

    Queue<V> queue();

    void addSynchronizer(Synchronizer<K, V> synchronizer);

    void removeSynchronizer(Synchronizer<K, V> synchronizer);

    interface Synchronizer<K, V> {

        default void beforeEnqueue(K key, V value, Map<K, V> readOnly) {}

        default void afterEnqueue(K key, V value, Map<K, V> readOnly) {}

        default void beforeDequeue(K key, V value, Map<K, V> readOnly) {}

        default void afterDequeue(K key, V value, Map<K, V> readOnly) {}
    }
}
