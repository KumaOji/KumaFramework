/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.generator.util;

public class LuhnUtils {
    public static int getLuhnSum(char[] chs) {
        int luhnSum = 0;
        int i = chs.length - 1;
        int j = 0;
        while (i >= 0) {
            int k = chs[i] - 48;
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhnSum += k;
            --i;
            ++j;
        }
        return luhnSum;
    }
}

