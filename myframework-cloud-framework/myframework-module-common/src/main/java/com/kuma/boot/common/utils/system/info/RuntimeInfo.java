/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.system.info;

import java.io.Serializable;

public class RuntimeInfo
implements Serializable {
    private final transient Runtime currentRuntime = Runtime.getRuntime();

    public final Runtime getRuntime() {
        return this.currentRuntime;
    }

    public final long getMaxMemory() {
        return this.currentRuntime.maxMemory();
    }

    public final long getTotalMemory() {
        return this.currentRuntime.totalMemory();
    }

    public final long getFreeMemory() {
        return this.currentRuntime.freeMemory();
    }

    public final long getUsableMemory() {
        return this.currentRuntime.maxMemory() - this.currentRuntime.totalMemory() + this.currentRuntime.freeMemory();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Runtime:         ").append(this.getRuntime()).append("\nMax Memory:      ").append(this.getMaxMemory()).append("\nTotal Memory:    ").append(this.getTotalMemory()).append("\nFree Memory:     ").append(this.getFreeMemory()).append("\nUsable Memory:   ").append(this.getUsableMemory());
        return builder.toString();
    }
}

