/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.jspecify.annotations.Nullable;

public class CountMap {
    private final Map<Object, AtomicLong> data;

    public CountMap() {
        this(new HashMap<Object, AtomicLong>(8));
    }

    public CountMap(Map<Object, AtomicLong> data) {
        this.data = data;
    }

    public void add(Object value) {
        this.data.compute(value, (k, v) -> {
            if (v == null) {
                return new AtomicLong(1L);
            }
            v.incrementAndGet();
            return v;
        });
    }

    public void decr(Object value) {
        this.data.computeIfPresent(value, (k, v) -> {
            v.decrementAndGet();
            return v;
        });
    }

    public void remove(Object value) {
        this.data.remove(value);
    }

    public long get(Object value) {
        AtomicLong counter = this.data.get(value);
        if (counter == null) {
            return 0L;
        }
        return counter.get();
    }

    public <T> @Nullable T getMax() {
        return (T) this.data.entrySet().stream().max(Comparator.comparingLong(entry -> ((AtomicLong)entry.getValue()).longValue())).map(Map.Entry::getKey).orElse(null);
    }

    public <T> @Nullable T getMin() {
        return (T) this.data.entrySet().stream().min(Comparator.comparingLong(entry -> ((AtomicLong)entry.getValue()).longValue())).map(Map.Entry::getKey).orElse(null);
    }

    public int size() {
        return this.data.keySet().size();
    }

    public void clear() {
        this.data.clear();
    }

    public String toString() {
        return this.data.toString();
    }
}

