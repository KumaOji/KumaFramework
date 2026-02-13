/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.xkzhangsan.time.utils.CollectionUtil
 */
package com.kuma.boot.common.support.hash;

import com.kuma.boot.common.support.hash.ConsistentHashing;
import com.kuma.boot.common.support.hash.DefaultConsistentHashing;
import com.kuma.boot.common.support.hash.api.HashCode;
import com.kuma.boot.common.support.hash.core.HasheCodes;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.xkzhangsan.time.utils.CollectionUtil;
import java.util.Collection;
import java.util.HashSet;

public final class ConsistentHashingBs<T> {
    private int virtualNum = 16;
    private HashCode hashCode = HasheCodes.jdk();
    private Collection<T> nodes = new HashSet<T>();

    public static <T> ConsistentHashingBs<T> newInstance() {
        return new ConsistentHashingBs<T>();
    }

    public ConsistentHashingBs<T> virtualNum(int virtualNum) {
        ArgUtils.gt("virtualNum", virtualNum, 0L);
        this.virtualNum = virtualNum;
        return this;
    }

    public ConsistentHashingBs<T> hashCode(HashCode hashCode) {
        ArgUtils.notNull(hashCode, "hashCode");
        this.hashCode = hashCode;
        return this;
    }

    public ConsistentHashingBs<T> nodes(Collection<T> nodes) {
        ArgUtils.notEmpty(nodes, "nodes");
        this.nodes = nodes;
        return this;
    }

    public ConsistentHashing<T> build() {
        DefaultConsistentHashing<T> hashing = new DefaultConsistentHashing<T>(this.virtualNum, this.hashCode);
        if (CollectionUtil.isNotEmpty(this.nodes)) {
            for (T node : this.nodes) {
                hashing.add(node);
            }
        }
        return hashing;
    }
}

