/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.number;

import java.util.List;

public final class MathUtils {
    public static final double GOLD_SECTION = 0.6180339887498949;

    private MathUtils() {
    }

    public static int gcd(int x, int y) {
        return y == 0 ? x : MathUtils.gcd(y, x % y);
    }

    public static int ngcd(List<Integer> list) {
        return MathUtils.ngcd(list, list.size());
    }

    private static int ngcd(List<Integer> target, int z) {
        if (z == 1) {
            return target.get(0);
        }
        return MathUtils.gcd(target.get(z - 1), MathUtils.ngcd(target, z - 1));
    }

    public static int lcm(int x, int y) {
        return x * y / MathUtils.gcd(x, y);
    }

    public static int nlcm(List<Integer> list) {
        return MathUtils.nlcm(list, list.size());
    }

    private static int nlcm(List<Integer> target, int z) {
        if (z == 1) {
            return target.get(z - 1);
        }
        return MathUtils.lcm(target.get(z - 1), MathUtils.nlcm(target, z - 1));
    }
}

