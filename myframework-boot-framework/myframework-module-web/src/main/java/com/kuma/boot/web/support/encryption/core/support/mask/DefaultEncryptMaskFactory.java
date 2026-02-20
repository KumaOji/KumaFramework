/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.core.support.mask;

import com.kuma.boot.web.support.encryption.api.core.EncryptMask;
import com.kuma.boot.web.support.encryption.api.core.EncryptMaskFactory;
import com.kuma.boot.web.support.encryption.api.enums.EncryptTypeEnum;

import java.util.HashMap;
import java.util.Map;

public class DefaultEncryptMaskFactory
implements EncryptMaskFactory {
    private final Map<String, EncryptMask> map = new HashMap<String, EncryptMask>();

    public DefaultEncryptMaskFactory() {
        this.map.put(EncryptTypeEnum.ADDRESS.getCode(), EncryptMasks.address());
        this.map.put(EncryptTypeEnum.BANK_CARD_NUM.getCode(), EncryptMasks.bankCardNum());
        this.map.put(EncryptTypeEnum.EMAIL.getCode(), EncryptMasks.email());
        this.map.put(EncryptTypeEnum.ID_CARD.getCode(), EncryptMasks.idCard());
        this.map.put(EncryptTypeEnum.NAME.getCode(), EncryptMasks.chineseName());
        this.map.put(EncryptTypeEnum.PASSWORD.getCode(), EncryptMasks.password());
        this.map.put(EncryptTypeEnum.PHONE.getCode(), EncryptMasks.phone());
    }

    @Override
    public EncryptMask get(String type) {
        return this.map.get(type);
    }
}

