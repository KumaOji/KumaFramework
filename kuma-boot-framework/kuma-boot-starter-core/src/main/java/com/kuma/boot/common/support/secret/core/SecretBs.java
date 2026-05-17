//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core;


import com.kuma.boot.common.support.secret.api.Secret;
import com.kuma.boot.common.support.secret.core.secret.Secrets;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.StringUtils;

/**
 * SecretBs
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class SecretBs {

    private Secret secret = Secrets.base64();
    private String charset = "UTF-8";
    private byte[] key = new byte[0];

    private SecretBs() {
    }

    public static SecretBs newInstance() {
        return new SecretBs();
    }

    public SecretBs secret( Secret secret ) {
        ArgUtils.notNull(secret, "secret");
        this.secret = secret;
        return this;
    }

    public SecretBs key( byte[] key ) {
        this.key = key;
        return this;
    }

    public SecretBs charset( String charset ) {
        ArgUtils.notEmpty(charset, "charset");
        this.charset = charset;
        return this;
    }

    public byte[] encrypt( byte[] source ) {
        SecretContext context = new SecretContext();
        context.key(this.key).source(source).charset(this.charset);
        return this.secret.encrypt(context);
    }

    public byte[] encrypt( String source ) {
        byte[] sourceBytes = StringUtils.getBytes(source, this.charset);
        return this.encrypt(sourceBytes);
    }

    public String encryptToString( byte[] source ) {
        byte[] encrypt = this.encrypt(source);
        return StringUtils.toString(encrypt, this.charset);
    }

    public String encryptToString( String source ) {
        byte[] encrypt = this.encrypt(source);
        return StringUtils.toString(encrypt, this.charset);
    }

    public String encryptToHexString( byte[] source ) {
        byte[] encrypt = this.encrypt(source);
        return HexUtil.byteToHexString(encrypt);
    }

    public String encryptToHexString( String source ) {
        byte[] encrypt = this.encrypt(source);
        return HexUtil.byteToHexString(encrypt);
    }

    public byte[] decrypt( byte[] source ) {
        SecretContext context = new SecretContext();
        context.key(this.key).source(source).charset(this.charset);
        return this.secret.decrypt(context);
    }

    public byte[] decrypt( String source ) {
        byte[] sourceBytes = StringUtils.getBytes(source, this.charset);
        return this.decrypt(sourceBytes);
    }

    public String decryptToString( byte[] source ) {
        byte[] decrypt = this.decrypt(source);
        return StringUtils.toString(decrypt, this.charset);
    }

    public String decryptToString( String source ) {
        byte[] decrypt = this.decrypt(source);
        return StringUtils.toString(decrypt, this.charset);
    }

    public String decryptToHexString( byte[] source ) {
        byte[] decrypt = this.decrypt(source);
        return HexUtil.byteToHexString(decrypt);
    }

    public String decryptToHexString( String source ) {
        byte[] decrypt = this.decrypt(source);
        return HexUtil.byteToHexString(decrypt);
    }
}
