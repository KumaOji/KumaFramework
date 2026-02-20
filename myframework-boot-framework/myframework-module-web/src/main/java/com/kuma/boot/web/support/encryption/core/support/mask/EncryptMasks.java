/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.core.support.mask;

import com.kuma.boot.web.support.encryption.api.core.EncryptMask;

public class EncryptMasks {
    public static EncryptMask address() {
        return new EncryptMaskAddress();
    }

    public static EncryptMask chineseName() {
        return new EncryptMaskChineseName();
    }

    public static EncryptMask bankCardNum() {
        return new EncryptMaskBankCardNum();
    }

    public static EncryptMask email() {
        return new EncryptMaskEmail();
    }

    public static EncryptMask phone() {
        return new EncryptMaskPhone();
    }

    public static EncryptMask idCard() {
        return new EncryptMaskIdCard();
    }

    public static EncryptMask password() {
        return new EncryptMaskPassword();
    }
}

