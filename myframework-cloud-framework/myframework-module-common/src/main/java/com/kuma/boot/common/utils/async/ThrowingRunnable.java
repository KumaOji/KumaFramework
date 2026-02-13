/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {
    public void run() throws E;

    public static Runnable unchecked(ThrowingRunnable<?> throwingRunnable) {
        return () -> {
            try {
                throwingRunnable.run();
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        };
    }
}

