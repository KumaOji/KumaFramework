/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core.secret;

import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.Base64Util;
import com.kuma.boot.common.support.secret.core.secret.AbstractSecret;
import com.kuma.boot.common.utils.lang.StringUtils;

public class Base64Secret
extends AbstractSecret {
    @Override
    public byte[] doEncrypt(SecretContext context) {
        String text = context.sourceText();
        return StringUtils.getBytes(Base64Util.encodeToString(text), context.charset());
    }

    @Override
    public byte[] doDecrypt(SecretContext context) {
        String text = context.sourceText();
        return Base64Util.decode(text);
    }
}

