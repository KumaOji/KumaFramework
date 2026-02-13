/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.core.HasheCodes;

public final class HashCodeHelper {
    private HashCodeHelper() {
    }

    public static int jdk(String text) {
        return HasheCodes.jdk().hash(text);
    }

    public static int crc(String text) {
        return HasheCodes.crc().hash(text);
    }

    public static int fnv(String text) {
        return HasheCodes.fnv().hash(text);
    }

    public static int ketama(String text) {
        return HasheCodes.ketama().hash(text);
    }

    public static int murmur(String text) {
        return HasheCodes.murmur().hash(text);
    }
}

