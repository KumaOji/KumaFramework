package com.kuma.boot.web.support.encryption.core.support.mask;


import com.kuma.boot.web.support.encryption.api.core.EncryptMask;

/**
 * @since 1.2.0
 */
public class EncryptMasks {

    public static EncryptMask address() {
        return new com.kuma.boot.web.support.encryption.core.support.mask.EncryptMaskAddress();
    }

    public static EncryptMask chineseName() {
        return new com.kuma.boot.web.support.encryption.core.support.mask.EncryptMaskChineseName();
    }

    public static EncryptMask bankCardNum() {
        return new com.kuma.boot.web.support.encryption.core.support.mask.EncryptMaskBankCardNum();
    }

    public static EncryptMask email() {
        return new com.kuma.boot.web.support.encryption.core.support.mask.EncryptMaskEmail();
    }

    public static EncryptMask phone() {
        return new com.kuma.boot.web.support.encryption.core.support.mask.EncryptMaskPhone();
    }

    public static EncryptMask idCard() {
        return new com.kuma.boot.web.support.encryption.core.support.mask.EncryptMaskIdCard();
    }

    public static EncryptMask password() {
        return new com.kuma.boot.web.support.encryption.core.support.mask.EncryptMaskPassword();
    }

}
