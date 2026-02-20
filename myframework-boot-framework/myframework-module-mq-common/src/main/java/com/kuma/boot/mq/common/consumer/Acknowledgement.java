/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.mq.common.consumer;

@FunctionalInterface
public interface Acknowledgement {
    public void acknowledge();
}

