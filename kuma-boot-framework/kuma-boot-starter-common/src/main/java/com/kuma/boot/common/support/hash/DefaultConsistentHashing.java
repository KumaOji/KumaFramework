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

package com.kuma.boot.common.support.hash;

import com.kuma.boot.common.support.hash.api.HashCode;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * ConsistentHashing
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class DefaultConsistentHashing<T> implements ConsistentHashing<T> {

    private final int virtualNum;
    private final HashCode hashCode;
    private final TreeMap<Integer, T> nodeMap = new TreeMap();

    public DefaultConsistentHashing( int virtualNum, HashCode hashCode ) {
        this.virtualNum = virtualNum;
        this.hashCode = hashCode;
    }

    public T get( String key ) {
        int hashCode = this.hashCode.hash(key);
        Integer target = hashCode;
        if (!this.nodeMap.containsKey(hashCode)) {
            target = (Integer) this.nodeMap.ceilingKey(hashCode);
            if (target == null && !this.nodeMap.isEmpty()) {
                target = (Integer) this.nodeMap.firstKey();
            }
        }

        return (T) this.nodeMap.get(target);
    }

    public ConsistentHashing add( T node ) {
        for (int i = 0; i < this.virtualNum; ++i) {
            int nodeKey = this.hashCode.hash(node.toString() + "-" + i);
            this.nodeMap.put(nodeKey, node);
        }

        return this;
    }

    public ConsistentHashing remove( T node ) {
        for (int i = 0; i < this.virtualNum; ++i) {
            int nodeKey = this.hashCode.hash(node.toString() + "-" + i);
            this.nodeMap.remove(nodeKey);
        }

        return this;
    }

    public Map<Integer, T> nodeMap() {
        return Collections.unmodifiableMap(this.nodeMap);
    }
}
