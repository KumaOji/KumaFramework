/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.HashResult;
import com.kuma.boot.common.support.hash.api.HashResultHandler;
import com.kuma.boot.common.support.hash.core.Base64HashResultHandler;
import com.kuma.boot.common.support.hash.core.BytesHashResultHandler;
import com.kuma.boot.common.support.hash.core.DefaultHashResultHandler;
import com.kuma.boot.common.support.hash.core.HexHashResultHandler;
import com.kuma.boot.common.support.instance.Instances;

public final class HashResultHandlers {
    private HashResultHandlers() {
    }

    public static HashResultHandler<String> hex() {
        return Instances.singleton(HexHashResultHandler.class);
    }

    public static HashResultHandler<String> base64() {
        return Instances.singleton(Base64HashResultHandler.class);
    }

    public static HashResultHandler<HashResult> defaults() {
        return Instances.singleton(DefaultHashResultHandler.class);
    }

    public static HashResultHandler<byte[]> bytes() {
        return Instances.singleton(BytesHashResultHandler.class);
    }
}

