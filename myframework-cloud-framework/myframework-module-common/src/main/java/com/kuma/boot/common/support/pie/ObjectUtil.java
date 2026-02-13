/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pie;

public final class ObjectUtil {
    private ObjectUtil() {
    }

    public static Object checkNotNull(Object arg, String text) {
        if (arg == null) {
            throw new NullPointerException(text);
        }
        return arg;
    }

    public static int checkPositive(int i, String name) {
        if (i <= 0) {
            throw new IllegalArgumentException(name + ": " + i + " (expected: > 0)");
        }
        return i;
    }
}

