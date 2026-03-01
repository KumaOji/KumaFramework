package com.kuma.boot.web.support.encryption.core.bs;


import com.kuma.boot.common.support.hash.HashBs;
import com.kuma.boot.common.support.hash.api.Hash;
import com.kuma.boot.common.support.hash.core.Hashes;
import com.kuma.boot.common.support.secret.api.Secret;
import com.kuma.boot.common.support.secret.core.SecretBs;
import com.kuma.boot.common.support.secret.core.secret.Secrets;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.web.support.encryption.api.core.*;
import com.kuma.boot.web.support.encryption.api.dto.req.CommonDecryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.req.CommonEncryptRequest;
import com.kuma.boot.web.support.encryption.api.dto.resp.CommonEncryptResponse;
import com.kuma.boot.web.support.encryption.core.core.DefaultEncryptionFactory;
import com.kuma.boot.web.support.encryption.core.support.mask.DefaultEncryptMaskFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author binbin.hou
 * @since 1.0.0
 */
public final class EncryptionLocalBs {

    private EncryptionLocalBs() {
    }

    public static EncryptionLocalBs newInstance() {
        return new EncryptionLocalBs();
    }

    /**
     * 编码
     */
    private Charset charset = StandardCharsets.UTF_8;

    /**
     * 秘钥
     * <p>
     * 建议用户自己指定
     */
    private String salt = "99886622";

    /**
     * 哈希策略
     */
    private Hash hash = Hashes.md5();

    /**
     * 加密策略
     */
    private Secret secret = Secrets.aes();

    /**
     * 掩码工厂策略
     *
     * @since 1.2.0
     */
    private com.kuma.boot.web.support.encryption.api.core.EncryptMaskFactory maskFactory = new DefaultEncryptMaskFactory();

    /**
     * 加密工厂策略
     *
     * @since 1.2.0
     */
    private com.kuma.boot.web.support.encryption.api.core.EncryptionFactory encryptionFactory = new DefaultEncryptionFactory();

    /**
     * 上下文
     */
    private com.kuma.boot.web.support.encryption.api.core.EncryptionContext encryptionContext;


    public EncryptionLocalBs charset(Charset charset) {
        ArgUtils.notNull(charset, "charset");

        this.charset = charset;
        return this;
    }

    public EncryptionLocalBs salt(String salt) {
        ArgUtils.notEmpty(salt, "salt");

        this.salt = salt;
        return this;
    }

    public EncryptionLocalBs hash( Hash hash) {
        ArgUtils.notNull(hash, "hash");

        this.hash = hash;
        return this;
    }

    public EncryptionLocalBs secret(Secret secret) {
        ArgUtils.notNull(secret, "secret");

        this.secret = secret;
        return this;
    }

    public EncryptionLocalBs maskFactory( com.kuma.boot.web.support.encryption.api.core.EncryptMaskFactory maskFactory) {
        ArgUtils.notNull(maskFactory, "maskFactory");

        this.maskFactory = maskFactory;
        return this;
    }

    public EncryptionLocalBs encryptionFactory( com.kuma.boot.web.support.encryption.api.core.EncryptionFactory encryptionFactory) {
        ArgUtils.notNull(encryptionFactory, "encryptionFactory");

        this.encryptionFactory = encryptionFactory;
        return this;
    }

    /**
     * 初始化
     *
     * @return this
     */
    public synchronized EncryptionLocalBs init() {
        ArgUtils.notEmpty(salt, "salt");

        final byte[] salts = salt.getBytes(charset);
        HashBs hashBs = HashBs.newInstance()
                .charset(charset)
                .hash(hash)
                .salt(salts);

        SecretBs secretBs = SecretBs.newInstance()
                .charset(charset.name())
                .secret(secret)
                .key(salts);

        this.encryptionContext = com.kuma.boot.web.support.encryption.core.core.EncryptionContext.newInstance()
                .hashBs(hashBs)
                .secretBs(secretBs);

        return this;
    }


    /**
     * 加密
     *
     * @param plainText 文本
     * @param type      类别
     * @return 结果
     * @since 1.2.0
     */
    public CommonEncryptResponse encrypt(String plainText, String type) {
        checkStatus();

        final com.kuma.boot.web.support.encryption.api.core.EncryptMask encryptMask = maskFactory.get(type);
        final com.kuma.boot.web.support.encryption.api.core.Encryption encryption = encryptionFactory.get(type);

        CommonEncryptRequest request = new CommonEncryptRequest();
        request.setText(plainText);
        request.setEncryptMask(encryptMask);

        return encryption.encrypt(request, encryptionContext);
    }

    /**
     * 解密
     *
     * @param cipher 文本
     * @param type   类别
     * @return 结果
     */
    public String decrypt(String cipher, String type) {
        checkStatus();

        final com.kuma.boot.web.support.encryption.api.core.Encryption encryption = encryptionFactory.get(type);

        CommonDecryptRequest request = new CommonDecryptRequest();
        request.setCipher(cipher);
        return encryption.decrypt(request, encryptionContext).getText();
    }

    /**
     * 生成摘要
     *
     * @param plainText 文本
     * @return 结果
     */
    public String hash(String plainText) {
        checkStatus();

        //2. 摘要
        return this.encryptionContext.hashBs().execute(plainText);
    }

    private void checkStatus() {
        if (this.encryptionContext == null) {
            this.init();
        }
    }


}
