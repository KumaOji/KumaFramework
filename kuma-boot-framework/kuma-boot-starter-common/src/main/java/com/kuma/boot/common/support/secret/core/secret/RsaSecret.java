//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;

import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.RsaUtil;
import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;

import java.io.UnsupportedEncodingException;

/**
 * RsaSecret
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class RsaSecret extends AbstractSecret {

    protected byte[] doEncrypt( SecretContext context ) {
        try {
            String key = context.keyText();
            return RsaUtil.encrypt(context.source(), key).getBytes(context.charset());
        } catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }

    protected byte[] doDecrypt( SecretContext context ) {
        try {
            String key = context.keyText();
            return RsaUtil.decrypt(context.sourceText(), key).getBytes(context.charset());
        } catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }
}
