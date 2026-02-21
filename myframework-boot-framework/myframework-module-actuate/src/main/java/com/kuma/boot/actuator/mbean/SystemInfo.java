/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.actuator.mbean;

public class SystemInfo
implements SystemInfoMBean {
    @Override
    public int getCpuCore() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Override
    public long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    @Override
    public void shutdown() {
    }
}

