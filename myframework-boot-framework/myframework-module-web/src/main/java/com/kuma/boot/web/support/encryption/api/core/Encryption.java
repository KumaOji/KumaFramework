/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.encryption.api.core;

import com.kuma.boot.web.support.encryption.api.dto.req.CommonDecryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.req.CommonEncryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonDecryptResponse;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonEncryptResponse;

public interface Encryption {
    public CommonEncryptResponse encrypt(CommonEncryptRequest var1, EncryptionContext var2);

    public CommonDecryptResponse decrypt(CommonDecryptRequest var1, EncryptionContext var2);
}

