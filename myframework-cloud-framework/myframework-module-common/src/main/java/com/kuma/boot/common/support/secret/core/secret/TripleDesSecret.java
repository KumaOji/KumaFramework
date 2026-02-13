/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core.secret;

import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.TripleDesUtil;
import com.kuma.boot.common.support.secret.core.secret.AbstractSecret;

public class TripleDesSecret
extends AbstractSecret {
    @Override
    public byte[] doEncrypt(SecretContext context) {
        return TripleDesUtil.encrypt(context.source(), context.key());
    }

    @Override
    public byte[] doDecrypt(SecretContext context) {
        return TripleDesUtil.decrypt(context.source(), context.key());
    }
}

