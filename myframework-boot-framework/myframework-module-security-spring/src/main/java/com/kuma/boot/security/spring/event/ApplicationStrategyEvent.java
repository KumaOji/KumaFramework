/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.event;

public interface ApplicationStrategyEvent<T>
extends StrategyEvent<T> {
    @Override
    default public boolean isLocal(String destinationService) {
        return true;
    }

    default public void postProcess(String destinationService, T data) {
        String originService = "http://127.0.0.1:3337";
        this.postProcess(originService, destinationService, data);
    }
}

