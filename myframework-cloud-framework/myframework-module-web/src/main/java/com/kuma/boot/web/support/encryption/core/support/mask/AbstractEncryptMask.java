/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.common.StringUtils
 */
package com.kuma.boot.web.support.encryption.core.support.mask;

import com.kuma.boot.common.utils.common.StringUtils;
import com.kuma.boot.web.support.encryption.api.core.EncryptMask;
import com.kuma.boot.web.support.encryption.api.core.EncryptMaskContext;

public abstract class AbstractEncryptMask
implements EncryptMask {
    protected abstract String doMask(EncryptMaskContext var1);

    @Override
    public String mask(EncryptMaskContext context) {
        String plainText = context.getPlainText();
        if (StringUtils.isEmpty((String)plainText)) {
            return plainText;
        }
        return this.doMask(context);
    }
}

