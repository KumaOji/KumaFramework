//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core.secret;


import com.kuma.boot.common.support.instance.Instances;
import com.kuma.boot.common.support.secret.api.Secret;

/**
 * Secrets
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class Secrets {

    public static Secret caesarShift() {
        return (Secret) Instances.singleton(CaesarShiftSecret.class);
    }

    public static Secret simpleSubstitution() {
        return (Secret) Instances.singleton(SimpleSubstitutionSecret.class);
    }

    public static Secret singleAdd() {
        return (Secret) Instances.singleton(SingleAddSecret.class);
    }

    public static Secret des() {
        return (Secret) Instances.singleton(DesSecret.class);
    }

    public static Secret tripleDes() {
        return (Secret) Instances.singleton(TripleDesSecret.class);
    }

    public static Secret sm4() {
        return (Secret) Instances.singleton(Sm4Secret.class);
    }

    public static Secret aes() {
        return (Secret) Instances.singleton(AesSecret.class);
    }

    public static Secret base64() {
        return (Secret) Instances.singleton(Base64Secret.class);
    }

    public static Secret rsa() {
        return (Secret) Instances.singleton(RsaSecret.class);
    }
}
