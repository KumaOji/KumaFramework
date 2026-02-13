/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core.secret;

import com.kuma.boot.common.support.instance.Instances;
import com.kuma.boot.common.support.secret.api.Secret;
import com.kuma.boot.common.support.secret.core.secret.AesSecret;
import com.kuma.boot.common.support.secret.core.secret.Base64Secret;
import com.kuma.boot.common.support.secret.core.secret.CaesarShiftSecret;
import com.kuma.boot.common.support.secret.core.secret.DesSecret;
import com.kuma.boot.common.support.secret.core.secret.RsaSecret;
import com.kuma.boot.common.support.secret.core.secret.SimpleSubstitutionSecret;
import com.kuma.boot.common.support.secret.core.secret.SingleAddSecret;
import com.kuma.boot.common.support.secret.core.secret.Sm4Secret;
import com.kuma.boot.common.support.secret.core.secret.TripleDesSecret;

public final class Secrets {
    public static Secret caesarShift() {
        return Instances.singleton(CaesarShiftSecret.class);
    }

    public static Secret simpleSubstitution() {
        return Instances.singleton(SimpleSubstitutionSecret.class);
    }

    public static Secret singleAdd() {
        return Instances.singleton(SingleAddSecret.class);
    }

    public static Secret des() {
        return Instances.singleton(DesSecret.class);
    }

    public static Secret tripleDes() {
        return Instances.singleton(TripleDesSecret.class);
    }

    public static Secret sm4() {
        return Instances.singleton(Sm4Secret.class);
    }

    public static Secret aes() {
        return Instances.singleton(AesSecret.class);
    }

    public static Secret base64() {
        return Instances.singleton(Base64Secret.class);
    }

    public static Secret rsa() {
        return Instances.singleton(RsaSecret.class);
    }
}

