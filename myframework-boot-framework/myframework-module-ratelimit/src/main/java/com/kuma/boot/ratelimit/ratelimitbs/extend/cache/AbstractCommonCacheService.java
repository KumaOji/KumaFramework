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

package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 抽象实现
 */
public abstract class AbstractCommonCacheService implements ICommonCacheService {

    @Override
    public void set(String key, String value) {
        this.set(key, value, 0);
    }

    @Override
    public void expire(String key, long expireTime, TimeUnit timeUnit) {
        long currentMills = System.currentTimeMillis();
        long actualMills = currentMills + timeUnit.toMillis(expireTime);

        this.expireAt(key, actualMills);
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> params) {
        return eval(script, keys.size(), getParams(keys, params));
    }

    @Override
    public Object eval(String script) {
        return this.eval(script, 0);
    }

    protected static String[] getParams(List<String> keys, List<String> args) {
        int keyCount = keys.size();
        int argCount = args.size();
        String[] params = new String[keyCount + args.size()];

        int i;
        for (i = 0; i < keyCount; ++i) {
            params[i] = keys.get(i);
        }

        for (i = 0; i < argCount; ++i) {
            params[keyCount + i] = args.get(i);
        }

        return params;
    }
}
