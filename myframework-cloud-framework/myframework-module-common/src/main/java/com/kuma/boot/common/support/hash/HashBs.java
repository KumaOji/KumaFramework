/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.xkzhangsan.time.utils.StringUtil
 */
package com.kuma.boot.common.support.hash;

import com.kuma.boot.common.support.hash.api.Hash;
import com.kuma.boot.common.support.hash.api.HashContext;
import com.kuma.boot.common.support.hash.api.HashResult;
import com.kuma.boot.common.support.hash.api.HashResultHandler;
import com.kuma.boot.common.support.hash.core.HashResultHandlers;
import com.kuma.boot.common.support.hash.core.Hashes;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.xkzhangsan.time.utils.StringUtil;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class HashBs {
    private Hash hash = Hashes.md5();
    private int times = 1;
    private byte[] salt;
    private HashContext hashContext;
    private Charset charset = StandardCharsets.UTF_8;

    private HashBs() {
    }

    public static HashBs newInstance() {
        return new HashBs();
    }

    public HashBs hash(Hash hash) {
        ArgUtils.notNull(hash, "hash");
        this.hash = hash;
        return this;
    }

    public HashBs times(int times) {
        this.times = times;
        return this;
    }

    public HashBs salt(byte[] salt) {
        this.salt = salt;
        return this;
    }

    public HashBs charset(Charset charset) {
        ArgUtils.notNull(charset, "charset");
        this.charset = charset;
        return this;
    }

    public synchronized HashBs init() {
        this.hashContext = com.kuma.boot.common.support.hash.core.HashContext.newInstance().salt(this.salt).times(this.times).charset(this.charset);
        return this;
    }

    public <T> T execute(byte[] source, HashResultHandler<T> handler) {
        if (this.hashContext == null) {
            this.init();
        }
        HashResult result = this.hash.hash(source, this.hashContext);
        return handler.handle(result);
    }

    public <T> T execute(String source, HashResultHandler<T> handler) {
        byte[] bytes = null;
        if (StringUtil.isNotEmpty((String)source)) {
            bytes = source.getBytes(this.charset);
        }
        return this.execute(bytes, handler);
    }

    public String execute(String source) {
        return this.execute(source, HashResultHandlers.hex());
    }
}

