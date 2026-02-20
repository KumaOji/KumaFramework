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

package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.HashBs;
import com.kuma.boot.common.support.hash.api.Hash;
import com.kuma.boot.common.support.hash.api.HashResultHandler;

import static com.alibaba.fastjson2.JSONB.toBytes;

/**
 * HashHelper
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public final class HashHelper {

    private HashHelper() {
    }

    public static String hash( String text ) {
        return hash(Hashes.md5(), text);
    }

    public static String hash( Hash hash, String text ) {
        return (String) hash(hash, text, HashResultHandlers.hex());
    }

    public static <T> T hash( Hash hash, String text, HashResultHandler<T> hashResultHandler ) {
        return (T) hash(hash, text, (String) null, hashResultHandler);
    }

    public static <T> T hash(
            Hash hash, String text, String salt, HashResultHandler<T> hashResultHandler ) {
        return (T) hash(hash, text, salt, 1, hashResultHandler);
    }

    public static <T> T hash(
            Hash hash,
            String text,
            String salt,
            int times,
            HashResultHandler<T> hashResultHandler ) {
        return (T)
                HashBs.newInstance()
                        .hash(hash)
                        .salt(toBytes(salt))
                        .times(times)
                        .execute(toBytes(text), hashResultHandler);
    }
}
