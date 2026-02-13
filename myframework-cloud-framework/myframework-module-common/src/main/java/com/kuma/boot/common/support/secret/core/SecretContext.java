/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.secret.core;

import com.kuma.boot.common.support.secret.exception.SecretRuntimeException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class SecretContext
implements com.kuma.boot.common.support.secret.api.SecretContext {
    private byte[] source;
    private byte[] key;
    private String charset;

    public static SecretContext newInstance() {
        return new SecretContext();
    }

    @Override
    public byte[] source() {
        return this.source;
    }

    @Override
    public String sourceText() {
        try {
            return new String(this.source, this.charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }

    @Override
    public SecretContext source(byte[] source) {
        this.source = source;
        return this;
    }

    @Override
    public byte[] key() {
        return this.key;
    }

    @Override
    public String keyText() {
        try {
            return new String(this.key, this.charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new SecretRuntimeException(e);
        }
    }

    @Override
    public SecretContext key(byte[] key) {
        this.key = key;
        return this;
    }

    @Override
    public String charset() {
        return this.charset;
    }

    @Override
    public SecretContext charset(String charset) {
        this.charset = charset;
        return this;
    }

    public String toString() {
        return "SecretContext{source=" + Arrays.toString(this.source) + ", key=" + Arrays.toString(this.key) + ", charset='" + this.charset + "'}";
    }
}

