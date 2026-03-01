/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.trigger.delay;

public interface DelayQueueMachine {
    public boolean addJob(String var1, Long var2);

    public String getDelayQueueName();
}

