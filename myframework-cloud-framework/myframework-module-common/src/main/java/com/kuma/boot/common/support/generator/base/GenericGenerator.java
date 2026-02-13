/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.generator.base;

import java.util.Random;

public abstract class GenericGenerator {
    private static Random random = null;

    public abstract String generate();

    protected Random getRandomInstance() {
        if (random == null) {
            random = new Random(System.currentTimeMillis());
        }
        return random;
    }
}

