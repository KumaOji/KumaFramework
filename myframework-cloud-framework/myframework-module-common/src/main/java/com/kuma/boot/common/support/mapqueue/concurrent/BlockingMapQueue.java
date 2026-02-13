/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mapqueue.concurrent;

import com.kuma.boot.common.support.mapqueue.concept.MapQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public interface BlockingMapQueue<K, V>
extends MapQueue<K, V> {
    @Override
    public ConcurrentMap<K, V> map();

    @Override
    public BlockingQueue<V> queue();
}

