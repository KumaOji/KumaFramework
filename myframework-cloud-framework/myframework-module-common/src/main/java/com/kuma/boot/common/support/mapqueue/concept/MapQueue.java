/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mapqueue.concept;

import java.util.Map;
import java.util.Queue;

public interface MapQueue<K, V> {
    public Map<K, V> map();

    public Queue<V> queue();

    public void addSynchronizer(Synchronizer<K, V> var1);

    public void removeSynchronizer(Synchronizer<K, V> var1);

    public static interface Synchronizer<K, V> {
        default public void beforeEnqueue(K key, V value, Map<K, V> readOnly) {
        }

        default public void afterEnqueue(K key, V value, Map<K, V> readOnly) {
        }

        default public void beforeDequeue(K key, V value, Map<K, V> readOnly) {
        }

        default public void afterDequeue(K key, V value, Map<K, V> readOnly) {
        }
    }
}

