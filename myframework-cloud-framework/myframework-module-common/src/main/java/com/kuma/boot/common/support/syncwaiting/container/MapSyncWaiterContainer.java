/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.container;

import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;
import com.kuma.boot.common.support.syncwaiting.container.SyncWaiterContainer;
import java.util.HashMap;
import java.util.Map;

public class MapSyncWaiterContainer
implements SyncWaiterContainer {
    protected Map<Object, SyncWaiter> map;

    public Map<Object, SyncWaiter> getMap() {
        return this.map;
    }

    public void setMap(Map<Object, SyncWaiter> map) {
        this.map = map;
    }

    public MapSyncWaiterContainer(Map<Object, SyncWaiter> map) {
        this.map = map;
    }

    public MapSyncWaiterContainer() {
        this(new HashMap<Object, SyncWaiter>());
    }

    @Override
    public boolean contains(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public SyncWaiter find(Object key) {
        return this.map.get(key);
    }

    @Override
    public void add(SyncWaiter waiter) {
        this.map.put(waiter.key(), waiter);
    }

    @Override
    public SyncWaiter remove(Object key) {
        return this.map.remove(key);
    }
}

