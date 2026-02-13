/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.xkzhangsan.time.utils.StringUtil
 */
package com.kuma.boot.common.support.secret.core.secret;

import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.secret.AbstractSecret;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.xkzhangsan.time.utils.StringUtil;

public class SingleAddSecret
extends AbstractSecret {
    @Override
    public byte[] doEncrypt(SecretContext context) {
        String source = context.sourceText();
        int offset = Integer.parseInt(context.keyText());
        return StringUtils.getBytes(this.shiftString(source, offset), context.charset());
    }

    @Override
    public byte[] doDecrypt(SecretContext context) {
        String source = context.sourceText();
        int offset = Integer.parseInt(context.keyText());
        return StringUtils.getBytes(this.shiftString(source, -offset), context.charset());
    }

    private String shiftString(String source, int offset) {
        if (StringUtil.isEmpty((String)source)) {
            return source;
        }
        Object[] characters = StringUtils.toCharacterArray(source);
        Object[] newChars = ArrayUtils.shift(characters, offset);
        return StringUtils.join(newChars, "", 0, newChars.length - 1);
    }
}

