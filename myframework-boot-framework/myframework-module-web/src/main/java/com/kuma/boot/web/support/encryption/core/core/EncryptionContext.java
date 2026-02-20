/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.hash.HashBs
 *  com.kuma.boot.common.support.secret.core.SecretBs
 */
package com.kuma.boot.web.support.encryption.core.core;

import com.kuma.boot.common.support.hash.HashBs;
import com.kuma.boot.common.support.secret.core.SecretBs;

public class EncryptionContext
implements com.kuma.boot.web.support.encryption.api.core.EncryptionContext {
    private HashBs hashBs;
    private SecretBs secretBs;

    public static EncryptionContext newInstance() {
        return new EncryptionContext();
    }

    @Override
    public HashBs hashBs() {
        return this.hashBs;
    }

    public EncryptionContext hashBs(HashBs hashBs) {
        this.hashBs = hashBs;
        return this;
    }

    @Override
    public SecretBs secretBs() {
        return this.secretBs;
    }

    public EncryptionContext secretBs(SecretBs secretBs) {
        this.secretBs = secretBs;
        return this;
    }
}

