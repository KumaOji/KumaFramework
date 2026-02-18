//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;


import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.Sm4Util;

/**
 * Sm4Secret
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class Sm4Secret extends AbstractSecret {

    public byte[] doEncrypt( SecretContext context ) {
        return Sm4Util.encryptEcbPadding(context.source(), context.key());
    }

    public byte[] doDecrypt( SecretContext context ) {
        return Sm4Util.decryptEcbPadding(context.source(), context.key());
    }
}
