/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.core.support.mask;

import com.kuma.boot.web.support.encryption.api.core.EncryptMaskContext;
import com.kuma.boot.web.support.encryption.core.util.InnerMaskUtil;

public class EncryptMaskEmail
extends AbstractEncryptMask {
    @Override
    protected String doMask(EncryptMaskContext context) {
        return InnerMaskUtil.email(context.getPlainText());
    }
}

