/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.security.justauth.justauth.cache;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION;

import com.kuma.boot.security.justauth.justauth.JustAuthProperties;
import com.kuma.boot.security.justauth.justauth.enums.CacheKeyStrategy;
import java.time.Instant;
import me.zhyd.oauth.cache.AuthCacheConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * auth state session cache, 根据 session 的缓存模式是否适用分布式来决定是否适用单机与分布式<br>
 *     1. 传入的 key 必须为 {@link AuthDefaultSource} 的 <code>name()</code>. 这样相同 session
 *     与相同的第三方 {@link AuthDefaultSource} 的 <code>name()</code> 的 cache key 永远相同 <br>
 *     2. 默认缓存时间为 {@link AuthCacheConfig#timeout}. <br>
 *     3. 清除缓存时间点: 获取缓存时(<code>get(key)</code>)会判断是否过期, 过期则删除, 调用 <code>containsKey(key)</code> 时会走 <code>get(key)
 *     </code> 流程. <br>
 *     4. 相同 session 与 相同的第三方 {@link AuthDefaultSource} 的 <code>name()</code>, <code>cache(key, value)</code> 会覆盖上一次的 value; <br>
 *
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/6 15:59
 */
public class AuthStateSessionCache implements com.kuma.boot.security.justauth.justauth.cache.Auth2StateCache {

    /**
     * value 与 timeout 的分隔符
     */
    private static final String DELIMITER = "_";

    private final JustAuthProperties justAuthProperties;

    public AuthStateSessionCache(JustAuthProperties justAuthProperties) {
        this.justAuthProperties = justAuthProperties;
    }

    @Override
    public void cache(String key, String value) {
        this.cache(key, value, justAuthProperties.getTimeout().toMillis());
    }

    @Override
    public void cache(String key, String value, long timeout) {
        long epochMilli = Instant.now().plusMillis(timeout).toEpochMilli();
        RequestContextHolder.currentRequestAttributes()
                .setAttribute(
                        justAuthProperties.getCacheKeyPrefix() + key,
                        value + DELIMITER + epochMilli,
                        SCOPE_SESSION);
    }

    @Override
    public String get(String key) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        String sessionKey = justAuthProperties.getCacheKeyPrefix() + key;

        String result = (String) requestAttributes.getAttribute(sessionKey, SCOPE_SESSION);

        if (!StringUtils.hasText(result)) {
            return null;
        }

        int index = result.lastIndexOf(DELIMITER);

        long timeout = Long.parseLong(result.substring(index + 1));
        if (Instant.now().toEpochMilli() > timeout) {
            requestAttributes.removeAttribute(sessionKey, SCOPE_SESSION);
            return null;
        }
        return result.substring(0, index);
    }

    /**
     * 移除缓存
     * @param key   state cache key
     */
    public void remove(String key) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        String sessionKey = justAuthProperties.getCacheKeyPrefix() + key;
        requestAttributes.removeAttribute(sessionKey, SCOPE_SESSION);
    }

    @Override
    public boolean containsKey(String key) {
        return StringUtils.hasText(this.get(key));
    }

    @Override
    public CacheKeyStrategy getCacheKeyStrategy() {
        return CacheKeyStrategy.PROVIDER_ID;
    }
}
