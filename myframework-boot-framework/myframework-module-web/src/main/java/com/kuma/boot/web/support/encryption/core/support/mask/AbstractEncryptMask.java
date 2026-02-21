package com.kuma.boot.web.support.encryption.core.support.mask;


import com.kuma.boot.common.utils.common.StringUtils;
import com.kuma.boot.web.support.encryption.api.core.EncryptMaskContext;
import com.kuma.boot.web.support.encryption.api.core.EncryptMask;

/**
 * 基础类
 *
 * @since 1.2.0
 */
public abstract class AbstractEncryptMask implements EncryptMask {

    protected abstract String doMask( EncryptMaskContext context);

    @Override
    public String mask( EncryptMaskContext context) {
        String plainText = context.getPlainText();
        if (StringUtils.isEmpty(plainText)) {
            return plainText;
        }

        return doMask(context);
    }

}
