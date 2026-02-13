/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash;

import com.kuma.boot.common.support.hash.ConsistentHashing;
import com.kuma.boot.common.support.hash.api.HashCode;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class DefaultConsistentHashing<T>
implements ConsistentHashing<T> {
    private final int virtualNum;
    private final HashCode hashCode;
    private final TreeMap<Integer, T> nodeMap = new TreeMap();

    public DefaultConsistentHashing(int virtualNum, HashCode hashCode) {
        this.virtualNum = virtualNum;
        this.hashCode = hashCode;
    }

    @Override
    public T get(String key) {
        int hashCode = this.hashCode.hash(key);
        Integer target = hashCode;
        if (!this.nodeMap.containsKey(hashCode) && (target = this.nodeMap.ceilingKey(hashCode)) == null && !this.nodeMap.isEmpty()) {
            target = this.nodeMap.firstKey();
        }
        return this.nodeMap.get(target);
    }

    @Override
    public ConsistentHashing add(T node) {
        for (int i = 0; i < this.virtualNum; ++i) {
            int nodeKey = this.hashCode.hash(node.toString() + "-" + i);
            this.nodeMap.put(nodeKey, node);
        }
        return this;
    }

    @Override
    public ConsistentHashing remove(T node) {
        for (int i = 0; i < this.virtualNum; ++i) {
            int nodeKey = this.hashCode.hash(node.toString() + "-" + i);
            this.nodeMap.remove(nodeKey);
        }
        return this;
    }

    @Override
    public Map<Integer, T> nodeMap() {
        return Collections.unmodifiableMap(this.nodeMap);
    }
}

