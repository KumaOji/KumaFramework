//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;


import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.Base64Util;
import com.kuma.boot.common.utils.lang.StringUtils;

/**
 * Base64Secret
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class Base64Secret extends AbstractSecret {

    public byte[] doEncrypt( SecretContext context ) {
        String text = context.sourceText();
        return StringUtils.getBytes(Base64Util.encodeToString(text), context.charset());
    }

    public byte[] doDecrypt( SecretContext context ) {
        String text = context.sourceText();
        return Base64Util.decode(text);
    }
}
