/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.secret.core.HexUtil
 *  com.kuma.boot.common.support.secret.core.SecretBs
 */
package com.kuma.boot.web.support.encryption.core.core;

import com.kuma.boot.common.support.secret.core.HexUtil;
import com.kuma.boot.common.support.secret.core.SecretBs;
import com.kuma.boot.web.support.encryption.api.core.EncryptMask;
import com.kuma.boot.web.support.encryption.api.core.EncryptMaskContext;
import com.kuma.boot.web.support.encryption.api.core.Encryption;
import com.kuma.boot.web.support.encryption.api.core.EncryptionContext;
import com.kuma.boot.web.support.encryption.api.dto.req.CommonDecryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.req.CommonEncryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonDecryptResponse;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonEncryptResponse;

public class CommonEncryption
implements Encryption {
    protected String getMask(CommonEncryptRequest request) {
        EncryptMaskContext context = new EncryptMaskContext();
        context.setPlainText(request.getText());
        EncryptMask encryptMask = request.getEncryptMask();
        return encryptMask.mask(context);
    }

    @Override
    public CommonEncryptResponse encrypt(CommonEncryptRequest request, EncryptionContext context) {
        String plain = request.getText();
        String cipher = context.secretBs().encryptToHexString(plain);
        String hash = context.hashBs().execute(plain);
        String mask = this.getMask(request);
        CommonEncryptResponse response = new CommonEncryptResponse();
        response.setMask(mask);
        response.setCipher(cipher);
        response.setHash(hash);
        return response;
    }

    @Override
    public CommonDecryptResponse decrypt(CommonDecryptRequest request, EncryptionContext context) {
        SecretBs secretBs = context.secretBs();
        byte[] bytes = HexUtil.hexStringToByte((String)request.getCipher());
        String plain = secretBs.decryptToString(bytes);
        CommonDecryptResponse response = new CommonDecryptResponse();
        response.setText(plain);
        return response;
    }
}

