//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * SecretContext
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class SecretContext implements com.kuma.boot.common.support.secret.api.SecretContext {

    private byte[] source;
    private byte[] key;
    private String charset;

    public static SecretContext newInstance() {
        return new SecretContext();
    }

    public byte[] source() {
        return this.source;
    }

    public String sourceText() {
        try {
            return new String(this.source, this.charset);
        } catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }

    public SecretContext source( byte[] source ) {
        this.source = source;
        return this;
    }

    public byte[] key() {
        return this.key;
    }

    public String keyText() {
        try {
            return new String(this.key, this.charset);
        } catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }

    public SecretContext key( byte[] key ) {
        this.key = key;
        return this;
    }

    public String charset() {
        return this.charset;
    }

    public SecretContext charset( String charset ) {
        this.charset = charset;
        return this;
    }

    public String toString() {
        return "SecretContext{source=" + Arrays.toString(this.source) + ", key=" + Arrays.toString(this.key)
                + ", charset='" + this.charset + '\'' + '}';
    }
}
