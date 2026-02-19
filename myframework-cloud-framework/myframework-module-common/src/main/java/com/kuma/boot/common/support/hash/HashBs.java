/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.hash;

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.support.hash.api.Hash;
import com.kuma.boot.common.support.hash.api.HashContext;
import com.kuma.boot.common.support.hash.api.HashResult;
import com.kuma.boot.common.support.hash.api.HashResultHandler;
import com.kuma.boot.common.support.hash.core.HashResultHandlers;
import com.kuma.boot.common.support.hash.core.Hashes;
import com.xkzhangsan.time.utils.StringUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * HashBs
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class HashBs {

    private Hash hash = Hashes.md5();
    private int times = 1;
    private byte[] salt;
    private HashContext hashContext;
    private Charset charset;

    private HashBs() {
        this.charset = StandardCharsets.UTF_8;
    }

    public static HashBs newInstance() {
        return new HashBs();
    }

    public HashBs hash( Hash hash ) {
        ArgUtils.notNull(hash, "hash");
        this.hash = hash;
        return this;
    }

    public HashBs times( int times ) {
        this.times = times;
        return this;
    }

    public HashBs salt( byte[] salt ) {
        this.salt = salt;
        return this;
    }

    public HashBs charset( Charset charset ) {
        ArgUtils.notNull(charset, "charset");
        this.charset = charset;
        return this;
    }

    public synchronized HashBs init() {
        this.hashContext =
                com.kuma.boot.common.support.hash.core.HashContext.newInstance().salt(this.salt).times(this.times).charset(this.charset);
        return this;
    }

    public <T> T execute( byte[] source, HashResultHandler<T> handler ) {
        if (this.hashContext == null) {
            this.init();
        }

        HashResult result = this.hash.hash(source, this.hashContext);
        return (T) handler.handle(result);
    }

    public <T> T execute( String source, HashResultHandler<T> handler ) {
        byte[] bytes = null;
        if (StringUtil.isNotEmpty(source)) {
            bytes = source.getBytes(this.charset);
        }

        return (T) this.execute(bytes, handler);
    }

    public String execute( String source ) {
        return (String) this.execute(source, HashResultHandlers.hex());
    }
}
