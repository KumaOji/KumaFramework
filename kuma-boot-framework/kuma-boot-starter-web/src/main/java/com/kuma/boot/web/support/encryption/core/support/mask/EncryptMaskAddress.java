package com.kuma.boot.web.support.encryption.core.support.mask;


import com.kuma.boot.web.support.encryption.api.core.EncryptMaskContext;
import com.kuma.boot.web.support.encryption.core.util.InnerMaskUtil;

/**
 * @since 1.2.0
 */
public class EncryptMaskAddress extends com.kuma.boot.web.support.encryption.core.support.mask.AbstractEncryptMask {

    @Override
    protected String doMask( EncryptMaskContext context) {
        return InnerMaskUtil.address(context.getPlainText());
    }

}
