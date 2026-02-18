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

package com.kuma.boot.common.support.mapqueue.concurrent;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 在Queue的基础上结合了Map的特性
 *
 * 在入队的时候，如果已经存在对应的key则会更新value，否则添加的队列最后
 *
 * 支持阻塞队列，同时对Map的操作也会阻塞
 *
 * BlockingMapQueue<String, String> bmq = new LinkedBlockingMapQueue<>();
 *
 * //通过 Map 操作 Map<String, String> map = bmq.map();
 *
 * //通过 Queue 操作 BlockingQueue<String> queue = bmq.queue();
 * 通过map()和queue()可以获得对应的实例调用对应的方法
 *
 * @param <K>
 * @param <V>
 */
public class LinkedBlockingMapQueue<K, V> extends AbstractBlockingMapQueue<K, V>
        implements Serializable {

    private static final long serialVersionUID = 2226674414226776978L;

    public LinkedBlockingMapQueue() {}

    public LinkedBlockingMapQueue(int capacity) {
        super(capacity);
    }

    public LinkedBlockingMapQueue(boolean fair) {
        super(fair);
    }

    public LinkedBlockingMapQueue(Map<K, V> map) {
        super(map);
    }

    public LinkedBlockingMapQueue(int capacity, Map<K, V> map) {
        super(capacity, map);
    }

    public LinkedBlockingMapQueue(boolean fair, Map<K, V> map) {
        super(fair, map);
    }

    public LinkedBlockingMapQueue(int capacity, boolean fair) {
        super(capacity, fair);
    }

    public LinkedBlockingMapQueue(int capacity, boolean fair, Map<? extends K, ? extends V> map) {
        super(capacity, fair, map);
    }

    @Override
    protected Map<K, V> createMap() {
        return new LinkedHashMap<>();
    }
}
