//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;


import com.kuma.boot.common.support.secret.api.Secret;
import com.kuma.boot.common.support.secret.api.SecretContext;

/**
 * AbstractSecret
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public abstract class AbstractSecret implements Secret {

    protected abstract byte[] doEncrypt( SecretContext var1 );

    protected abstract byte[] doDecrypt( SecretContext var1 );

    public byte[] encrypt( SecretContext context ) {
        byte[] source = context.source();
        return source == null ? null : this.doEncrypt(context);
    }

    public byte[] decrypt( SecretContext context ) {
        byte[] source = context.source();
        return source == null ? null : this.doDecrypt(context);
    }
}
