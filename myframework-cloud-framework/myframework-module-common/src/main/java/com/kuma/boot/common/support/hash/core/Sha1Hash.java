/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.HashType;
import com.kuma.boot.common.support.hash.core.AbstractMessageDigestHash;

public class Sha1Hash
extends AbstractMessageDigestHash {
    @Override
    protected String algorithmName() {
        return HashType.SHA1.getCode();
    }
}

