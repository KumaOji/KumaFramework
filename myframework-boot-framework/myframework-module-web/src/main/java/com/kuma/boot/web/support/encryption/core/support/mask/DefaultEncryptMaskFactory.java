package com.kuma.boot.web.support.encryption.core.support.mask;


import com.kuma.boot.web.support.encryption.api.core.EncryptMask;
import com.kuma.boot.web.support.encryption.api.core.EncryptMaskFactory;
import com.kuma.boot.web.support.encryption.api.enums.EncryptTypeEnum;

import java.util.HashMap;
import java.util.Map;

public class DefaultEncryptMaskFactory implements EncryptMaskFactory {

    private final Map<String, EncryptMask> map = new HashMap<>();

    public DefaultEncryptMaskFactory() {
        map.put(EncryptTypeEnum.ADDRESS.getCode(), EncryptMasks.address());
        map.put(EncryptTypeEnum.BANK_CARD_NUM.getCode(), EncryptMasks.bankCardNum());
        map.put(EncryptTypeEnum.EMAIL.getCode(), EncryptMasks.email());
        map.put(EncryptTypeEnum.ID_CARD.getCode(), EncryptMasks.idCard());
        map.put(EncryptTypeEnum.NAME.getCode(), EncryptMasks.chineseName());
        map.put(EncryptTypeEnum.PASSWORD.getCode(), EncryptMasks.password());
        map.put(EncryptTypeEnum.PHONE.getCode(), EncryptMasks.phone());
    }

    @Override
    public EncryptMask get(String type) {
        return map.get(type);
    }

}
