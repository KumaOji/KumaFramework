/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.pair;

import com.kuma.boot.common.utils.secure.RSAUtils;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyPair {
    private final java.security.KeyPair keyPair;

    public KeyPair(java.security.KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public PublicKey getPublic() {
        return this.keyPair.getPublic();
    }

    public PrivateKey getPrivate() {
        return this.keyPair.getPrivate();
    }

    public byte[] getPublicBytes() {
        return this.getPublic().getEncoded();
    }

    public byte[] getPrivateBytes() {
        return this.getPrivate().getEncoded();
    }

    public String getPublicBase64() {
        return RSAUtils.getKeyString(this.getPublic());
    }

    public String getPrivateBase64() {
        return RSAUtils.getKeyString(this.getPrivate());
    }

    public String toString() {
        return "PublicKey=" + this.getPublicBase64() + "\nPrivateKey=" + this.getPrivateBase64();
    }
}

