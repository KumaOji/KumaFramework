/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.hash.HashBs
 *  com.kuma.boot.common.support.secret.core.SecretBs
 */
package com.kuma.boot.web.support.encryption.api.core;

import com.kuma.boot.common.support.hash.HashBs;
import com.kuma.boot.common.support.secret.core.SecretBs;

public interface EncryptionContext {
    public SecretBs secretBs();

    public HashBs hashBs();
}

