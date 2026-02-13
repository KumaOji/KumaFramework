/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.context;

public interface Lifecycle {
    public void initialize() throws IllegalStateException;

    public void start() throws IllegalStateException;

    public void destroy() throws IllegalStateException;
}

