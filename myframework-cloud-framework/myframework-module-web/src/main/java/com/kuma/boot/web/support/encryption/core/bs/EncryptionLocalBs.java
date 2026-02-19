/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.hash.HashBs
 *  com.kuma.boot.common.support.hash.api.Hash
 *  com.kuma.boot.common.support.hash.core.Hashes
 *  com.kuma.boot.common.support.secret.api.Secret
 *  com.kuma.boot.common.support.secret.core.SecretBs
 *  com.kuma.boot.common.support.secret.core.secret.Secrets
 *  com.kuma.boot.common.utils.common.ArgUtils
 */
package com.kuma.boot.web.support.encryption.core.bs;

import com.kuma.boot.web.support.encryption.core.core.EncryptionContext;
import com.kuma.boot.common.support.hash.HashBs;
import com.kuma.boot.common.support.hash.api.Hash;
import com.kuma.boot.common.support.hash.core.Hashes;
import com.kuma.boot.common.support.secret.api.Secret;
import com.kuma.boot.common.support.secret.core.SecretBs;
import com.kuma.boot.common.support.secret.core.secret.Secrets;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.web.support.encryption.api.core.EncryptMask;
import com.kuma.boot.web.support.encryption.api.core.EncryptMaskFactory;
import com.kuma.boot.web.support.encryption.api.core.Encryption;
import com.kuma.boot.web.support.encryption.api.core.EncryptionFactory;
import com.kuma.boot.web.support.encryption.api.dto.req.CommonDecryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.req.CommonEncryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonEncryptResponse;
import com.kuma.boot.web.support.encryption.core.core.DefaultEncryptionFactory;
import com.kuma.boot.web.support.encryption.core.support.mask.DefaultEncryptMaskFactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class EncryptionLocalBs {
    private Charset charset = StandardCharsets.UTF_8;
    private String salt = "99886622";
    private Hash hash = Hashes.md5();
    private Secret secret = Secrets.aes();
    private EncryptMaskFactory maskFactory = new DefaultEncryptMaskFactory();
    private EncryptionFactory encryptionFactory = new DefaultEncryptionFactory();
    private com.kuma.boot.web.support.encryption.api.core.EncryptionContext encryptionContext;

    private EncryptionLocalBs() {
    }

    public static EncryptionLocalBs newInstance() {
        return new EncryptionLocalBs();
    }

    public EncryptionLocalBs charset(Charset charset) {
        ArgUtils.notNull((Object)charset, (String)"charset");
        this.charset = charset;
        return this;
    }

    public EncryptionLocalBs salt(String salt) {
        ArgUtils.notEmpty((String)salt, (String)"salt");
        this.salt = salt;
        return this;
    }

    public EncryptionLocalBs hash(Hash hash) {
        ArgUtils.notNull((Object)hash, (String)"hash");
        this.hash = hash;
        return this;
    }

    public EncryptionLocalBs secret(Secret secret) {
        ArgUtils.notNull((Object)secret, (String)"secret");
        this.secret = secret;
        return this;
    }

    public EncryptionLocalBs maskFactory(EncryptMaskFactory maskFactory) {
        ArgUtils.notNull((Object)maskFactory, (String)"maskFactory");
        this.maskFactory = maskFactory;
        return this;
    }

    public EncryptionLocalBs encryptionFactory(EncryptionFactory encryptionFactory) {
        ArgUtils.notNull((Object)encryptionFactory, (String)"encryptionFactory");
        this.encryptionFactory = encryptionFactory;
        return this;
    }

    public synchronized EncryptionLocalBs init() {
        ArgUtils.notEmpty((String)this.salt, (String)"salt");
        byte[] salts = this.salt.getBytes(this.charset);
        HashBs hashBs = HashBs.newInstance().charset(this.charset).hash(this.hash).salt(salts);
        SecretBs secretBs = SecretBs.newInstance().charset(this.charset.name()).secret(this.secret).key(salts);
        this.encryptionContext = EncryptionContext.newInstance().hashBs(hashBs).secretBs(secretBs);
        return this;
    }

    public CommonEncryptResponse encrypt(String plainText, String type) {
        this.checkStatus();
        EncryptMask encryptMask = this.maskFactory.get(type);
        Encryption encryption = this.encryptionFactory.get(type);
        CommonEncryptRequest request = new CommonEncryptRequest();
        request.setText(plainText);
        request.setEncryptMask(encryptMask);
        return encryption.encrypt(request, this.encryptionContext);
    }

    public String decrypt(String cipher, String type) {
        this.checkStatus();
        Encryption encryption = this.encryptionFactory.get(type);
        CommonDecryptRequest request = new CommonDecryptRequest();
        request.setCipher(cipher);
        return encryption.decrypt(request, this.encryptionContext).getText();
    }

    public String hash(String plainText) {
        this.checkStatus();
        return this.encryptionContext.hashBs().execute(plainText);
    }

    private void checkStatus() {
        if (this.encryptionContext == null) {
            this.init();
        }
    }
}

