//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;


import com.kuma.boot.common.support.secret.api.SecretContext;
import com.kuma.boot.common.support.secret.core.DesUtil;

/**
 * DesSecret
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class DesSecret extends AbstractSecret {

    public byte[] doEncrypt( SecretContext context ) {
        return DesUtil.encrypt(context.source(), context.key());
    }

    public byte[] doDecrypt( SecretContext context ) {
        return DesUtil.decrypt(context.source(), context.key());
    }
}
