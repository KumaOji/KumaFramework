package com.kuma.boot.web.support.encryption.core.core;


import com.kuma.boot.common.support.hash.HashBs;
import com.kuma.boot.common.support.secret.core.SecretBs;

/**
 * @author binbin.hou
 * @since 1.0.0
 */
public class EncryptionContext implements com.kuma.boot.web.support.encryption.api.core.EncryptionContext {

    /**
     * 摘要策略
     */
    private HashBs hashBs;

    /**
     * 加密策略
     */
    private SecretBs secretBs;

    public static EncryptionContext newInstance() {
        return new EncryptionContext();
    }

    @Override
    public HashBs hashBs() {
        return hashBs;
    }

    public EncryptionContext hashBs(HashBs hashBs) {
        this.hashBs = hashBs;
        return this;
    }

    @Override
    public SecretBs secretBs() {
        return secretBs;
    }

    public EncryptionContext secretBs(SecretBs secretBs) {
        this.secretBs = secretBs;
        return this;
    }

}
