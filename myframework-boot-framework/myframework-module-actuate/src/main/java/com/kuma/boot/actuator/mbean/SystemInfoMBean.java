/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.actuator.mbean;

public interface SystemInfoMBean {
    public int getCpuCore();

    public long getTotalMemory();

    public void shutdown();
}

