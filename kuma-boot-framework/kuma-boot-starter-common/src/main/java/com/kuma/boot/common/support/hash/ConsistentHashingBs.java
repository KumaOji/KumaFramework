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

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.support.hash.api.HashCode;
import com.kuma.boot.common.support.hash.core.HasheCodes;
import com.xkzhangsan.time.utils.CollectionUtil;

import java.util.Collection;
import java.util.HashSet;

/**
 * ConsistentHashingBs
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class ConsistentHashingBs<T> {

    private int virtualNum = 16;
    private HashCode hashCode = HasheCodes.jdk();
    private Collection<T> nodes = new HashSet();

    public static <T> ConsistentHashingBs<T> newInstance() {
        return new ConsistentHashingBs<T>();
    }

    public ConsistentHashingBs<T> virtualNum( int virtualNum ) {
        ArgUtils.gt("virtualNum", (long) virtualNum, 0L);
        this.virtualNum = virtualNum;
        return this;
    }

    public ConsistentHashingBs<T> hashCode( HashCode hashCode ) {
        ArgUtils.notNull(hashCode, "hashCode");
        this.hashCode = hashCode;
        return this;
    }

    public ConsistentHashingBs<T> nodes( Collection<T> nodes ) {
        ArgUtils.notEmpty(nodes, "nodes");
        this.nodes = nodes;
        return this;
    }

    public ConsistentHashing<T> build() {
        ConsistentHashing<T> hashing = new DefaultConsistentHashing(this.virtualNum, this.hashCode);
        if (CollectionUtil.isNotEmpty(this.nodes)) {
            for (T node : this.nodes) {
                hashing.add(node);
            }
        }

        return hashing;
    }
}
