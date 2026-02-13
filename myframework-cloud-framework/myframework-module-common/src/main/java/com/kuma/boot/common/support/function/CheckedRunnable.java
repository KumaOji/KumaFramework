/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.function;

import java.io.Serializable;

@FunctionalInterface
public interface CheckedRunnable
extends Serializable {
    public void run() throws Throwable;
}

