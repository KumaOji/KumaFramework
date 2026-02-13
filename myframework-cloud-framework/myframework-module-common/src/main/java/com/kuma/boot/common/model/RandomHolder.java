/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.model;

import java.security.SecureRandom;
import java.util.Random;

public class RandomHolder {
    public static final Random RANDOM = new Random();
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();
}

