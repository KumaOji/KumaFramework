/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.core.core;

import com.kuma.boot.web.support.encryption.api.core.Encryption;
import com.kuma.boot.web.support.encryption.api.core.EncryptionFactory;

public class DefaultEncryptionFactory
implements EncryptionFactory {
    private static final Encryption ENCRYPTION = new CommonEncryption();

    @Override
    public Encryption get(String type) {
        return ENCRYPTION;
    }
}

