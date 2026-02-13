/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

import com.kuma.boot.common.utils.async.ThrowingRunnable;

@FunctionalInterface
public interface RunnableWithException
extends ThrowingRunnable<Exception> {
    @Override
    public void run() throws Exception;
}

