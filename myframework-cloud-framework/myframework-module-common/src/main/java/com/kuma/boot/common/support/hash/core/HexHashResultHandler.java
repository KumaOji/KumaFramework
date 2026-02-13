/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.HashResult;
import com.kuma.boot.common.support.hash.api.HashResultHandler;
import com.kuma.boot.common.utils.secure.HexUtils;

public class HexHashResultHandler
implements HashResultHandler<String> {
    @Override
    public String handle(HashResult hashResult) {
        return HexUtils.encodeToString(hashResult.hashed());
    }
}

