/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.complete;

import com.kuma.boot.common.support.complete.EmptyUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class Write<I, N> {
    private final Function<? super List<I>, ? extends Map<? super I, ? extends N>> nameMapCreator;
    private volatile Set<I> ids;
    private Map<? super I, ? extends N> map;
    private final AtomicBoolean isNew = new AtomicBoolean(false);

    protected Write(Function<? super List<I>, ? extends Map<? super I, ? extends N>> nameMapCreator) {
        this.nameMapCreator = nameMapCreator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void add(I id) {
        if (id == null) {
            return;
        }
        if (this.ids == null) {
            Write write = this;
            synchronized (write) {
                if (this.ids == null) {
                    this.ids = new HashSet<I>();
                }
            }
        }
        this.ids.add(id);
        this.isNew.set(true);
    }

    protected synchronized Map<? super I, ? extends N> get() {
        if (!this.isNew.get()) {
            return this.map == null ? EmptyUtil.emptyMap() : this.map;
        }
        if (EmptyUtil.isEmpty(this.ids)) {
            return EmptyUtil.emptyMap();
        }
        this.map = this.nameMapCreator.apply(new ArrayList<I>(this.ids));
        if (this.map == null) {
            return EmptyUtil.emptyMap();
        }
        return this.map;
    }
}

