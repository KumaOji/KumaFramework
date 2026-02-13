/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.lang;

public final class BoolUtils {
    public static final String Y = "Y";
    public static final String N = "N";

    private BoolUtils() {
    }

    public static boolean getBool(String boolStr) {
        if ("YES".equals(boolStr)) {
            return true;
        }
        if (Y.equals(boolStr)) {
            return true;
        }
        if ("1".equals(boolStr)) {
            return true;
        }
        if ("true".equals(boolStr)) {
            return true;
        }
        return "\u662f".equals(boolStr);
    }

    public static String getYesOrNo(boolean value) {
        if (value) {
            return Y;
        }
        return N;
    }
}

