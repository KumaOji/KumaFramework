/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSONB
 */
package com.kuma.boot.common.support.hash.core;

import com.alibaba.fastjson2.JSONB;
import com.kuma.boot.common.support.hash.HashBs;
import com.kuma.boot.common.support.hash.api.Hash;
import com.kuma.boot.common.support.hash.api.HashResultHandler;
import com.kuma.boot.common.support.hash.core.HashResultHandlers;
import com.kuma.boot.common.support.hash.core.Hashes;

public final class HashHelper {
    private HashHelper() {
    }

    public static String hash(String text) {
        return HashHelper.hash(Hashes.md5(), text);
    }

    public static String hash(Hash hash, String text) {
        return HashHelper.hash(hash, text, HashResultHandlers.hex());
    }

    public static <T> T hash(Hash hash, String text, HashResultHandler<T> hashResultHandler) {
        return HashHelper.hash(hash, text, null, hashResultHandler);
    }

    public static <T> T hash(Hash hash, String text, String salt, HashResultHandler<T> hashResultHandler) {
        return HashHelper.hash(hash, text, salt, 1, hashResultHandler);
    }

    public static <T> T hash(Hash hash, String text, String salt, int times, HashResultHandler<T> hashResultHandler) {
        return HashBs.newInstance().hash(hash).salt(JSONB.toBytes((String)salt)).times(times).execute(JSONB.toBytes((String)text), hashResultHandler);
    }
}

