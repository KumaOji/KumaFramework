/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.concept;

import com.kuma.boot.common.support.syncwaiting.caller.SyncCaller;
import com.kuma.boot.common.support.syncwaiting.configuration.SyncWaitingConfiguration;

public interface SyncWaitingConcept {
    default public <T> T waitSync(Object key, SyncCaller caller) {
        return this.waitSync(key, caller, 0L);
    }

    default public <T> T waitSync(Object key, SyncCaller caller, long waitingTime) {
        return this.waitSync(key, caller, waitingTime, 0L);
    }

    default public <T> T waitSync(Object key, SyncCaller caller, long waitingTime, long queuingTime) {
        SyncWaitingConfiguration configuration = new SyncWaitingConfiguration.Builder().waitingTime(waitingTime).queuingTime(queuingTime).build();
        return this.waitSync(key, caller, configuration);
    }

    public <T> T waitSync(Object var1, SyncCaller var2, SyncWaitingConfiguration var3);

    public void notifyAsync(Object var1, Object var2);

    public boolean isWaiting(Object var1);
}

