/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.HashResult;
import com.kuma.boot.common.support.hash.api.HashResultHandler;
import com.kuma.boot.common.utils.secure.Base64Utils;

public class Base64HashResultHandler
implements HashResultHandler<String> {
    @Override
    public String handle(HashResult hashResult) {
        return Base64Utils.encodeToString(hashResult.hashed());
    }
}

