/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core.secret;

import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.Sm4Util;
import com.kuma.boot.common.support.secret.core.secret.AbstractSecret;

public class Sm4Secret
extends AbstractSecret {
    @Override
    public byte[] doEncrypt(SecretContext context) {
        return Sm4Util.encryptEcbPadding(context.source(), context.key());
    }

    @Override
    public byte[] doDecrypt(SecretContext context) {
        return Sm4Util.decryptEcbPadding(context.source(), context.key());
    }
}

