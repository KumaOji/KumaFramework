//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;


import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.TripleDesUtil;

/**
 * TripleDesSecret
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class TripleDesSecret extends AbstractSecret {

    public byte[] doEncrypt( SecretContext context ) {
        return TripleDesUtil.encrypt(context.source(), context.key());
    }

    public byte[] doDecrypt( SecretContext context ) {
        return TripleDesUtil.decrypt(context.source(), context.key());
    }
}
