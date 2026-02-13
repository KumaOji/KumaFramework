/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.HashContext;
import com.kuma.boot.common.support.hash.core.AbstractHash;
import com.kuma.boot.common.support.hash.core.HashRuntimeException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class AbstractMessageDigestHash
extends AbstractHash {
    protected abstract String algorithmName();

    @Override
    public byte[] doHash(byte[] source, HashContext context) {
        String algorithmName = this.algorithmName();
        return this.innerHash(source, context.salt(), context.times(), algorithmName);
    }

    protected MessageDigest getDigest(String algorithmName) {
        try {
            return MessageDigest.getInstance(algorithmName);
        }
        catch (NoSuchAlgorithmException e) {
            String msg = "No native '" + algorithmName + "' MessageDigest instance available on the current JVM.";
            throw new HashRuntimeException(msg, e);
        }
    }

    protected byte[] innerHash(byte[] bytes, byte[] salt, int hashIterations, String algorithmName) {
        MessageDigest digest = this.getDigest(algorithmName);
        if (salt != null) {
            digest.reset();
            digest.update(salt);
        }
        byte[] hashed = digest.digest(bytes);
        int times = hashIterations - 1;
        for (int i = 0; i < times; ++i) {
            digest.reset();
            hashed = digest.digest(hashed);
        }
        return hashed;
    }
}

