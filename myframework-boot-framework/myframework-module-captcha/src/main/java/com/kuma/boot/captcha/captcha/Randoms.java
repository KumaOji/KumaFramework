/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.captcha;

import java.util.Random;

public class Randoms {
    protected static final Random RANDOM = new Random();
    public static final char[] ALPHA = new char[]{'2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'G', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    protected static final int numMaxIndex = 8;
    protected static final int charMinIndex = 8;
    protected static final int charMaxIndex;
    protected static final int upperMinIndex = 8;
    protected static final int upperMaxIndex = 31;
    protected static final int lowerMinIndex = 31;
    protected static final int lowerMaxIndex;

    public static int num(int min, int max) {
        return min + RANDOM.nextInt(max - min);
    }

    public static int num(int num) {
        return RANDOM.nextInt(num);
    }

    public static char alpha() {
        return ALPHA[Randoms.num(ALPHA.length)];
    }

    public static char alpha(int num) {
        return ALPHA[Randoms.num(num)];
    }

    public static char alpha(int min, int max) {
        return ALPHA[Randoms.num(min, max)];
    }

    static {
        lowerMaxIndex = charMaxIndex = ALPHA.length;
    }
}

