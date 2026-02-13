/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core.secret;

import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.RsaUtil;
import com.kuma.boot.common.support.secret.core.secret.AbstractSecret;
import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;
import java.io.UnsupportedEncodingException;

public class RsaSecret
extends AbstractSecret {
    @Override
    protected byte[] doEncrypt(SecretContext context) {
        try {
            String key = context.keyText();
            return RsaUtil.encrypt(context.source(), key).getBytes(context.charset());
        }
        catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }

    @Override
    protected byte[] doDecrypt(SecretContext context) {
        try {
            String key = context.keyText();
            return RsaUtil.decrypt(context.sourceText(), key).getBytes(context.charset());
        }
        catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }
}

