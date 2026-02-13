/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.configuration;

public class SyncWaitingConfiguration {
    private long waitingTime;
    private long queuingTime;

    public long getWaitingTime() {
        return this.waitingTime;
    }

    public long getQueuingTime() {
        return this.queuingTime;
    }

    public static class Builder {
        private long waitingTime = 0L;
        private long queuingTime = 0L;

        public Builder waitingTime(long waitingTime) {
            this.waitingTime = waitingTime;
            return this;
        }

        public Builder queuingTime(long queuingTime) {
            this.queuingTime = queuingTime;
            return this;
        }

        public SyncWaitingConfiguration build() {
            SyncWaitingConfiguration configuration = new SyncWaitingConfiguration();
            configuration.waitingTime = this.waitingTime;
            configuration.queuingTime = this.queuingTime;
            return configuration;
        }
    }
}

