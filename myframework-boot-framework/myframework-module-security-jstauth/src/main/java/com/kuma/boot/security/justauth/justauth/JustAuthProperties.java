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

package com.kuma.boot.security.justauth.justauth;

import com.kuma.boot.security.justauth.justauth.enums.StateCacheType;
import java.time.Duration;
import me.zhyd.oauth.cache.AuthCacheConfig;
import me.zhyd.oauth.model.AuthCallback;

/**
 * JustAuth 配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/6 19:58
 */
public class JustAuthProperties {

    /**
     * 忽略校验 {@code state} 参数，默认不开启。当 {@code ignoreCheckState} 为 {@code true} 时，
     * {@link me.zhyd.oauth.request.AuthDefaultRequest#login(AuthCallback)} 将不会校验 {@code state} 的合法性。
     * <p>
     * 使用场景：当且仅当使用自实现 {@code state} 校验逻辑时开启
     * <p>
     * 以下场景使用方案仅作参考：
     * 1. 授权、登录为同端，并且全部使用 JustAuth 实现时，该值建议设为 {@code false};
     * 2. 授权和登录为不同端实现时，比如前端页面拼装 {@code authorizeUrl}，并且前端自行对{@code state}进行校验，
     * 后端只负责使用{@code code}获取用户信息时，该值建议设为 {@code true};
     *
     * <strong>如非特殊需要，不建议开启这个配置</strong>
     * <p>
     * 该方案主要为了解决以下类似场景的问题：
     *
     * @see <a href="https://github.com/justauth/JustAuth/issues/83">https://github.com/justauth/JustAuth/issues/83</a>
     * @since 1.15.6
     */
    private Boolean ignoreCheckState = false;

    /**
     * 默认 state 缓存过期时间：3分钟(PT180S)
     * 鉴于授权过程中，根据个人的操作习惯，或者授权平台的不同（google等），每个授权流程的耗时也有差异，不过单个授权流程一般不会太长
     * 本缓存工具默认的过期时间设置为3分钟，即程序默认认为3分钟内的授权有效，超过3分钟则默认失效，失效后删除
     */
    private Duration timeout = Duration.ofMillis(AuthCacheConfig.timeout);

    /**
     * JustAuth state 缓存类型, 默认 session
     */
    private StateCacheType cacheType = StateCacheType.SESSION;

    /**
     * JustAuth state 缓存 key 前缀
     */
    private String cacheKeyPrefix = "JUST_AUTH:";

    public Boolean getIgnoreCheckState() {
        return ignoreCheckState;
    }

    public void setIgnoreCheckState(Boolean ignoreCheckState) {
        this.ignoreCheckState = ignoreCheckState;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public StateCacheType getCacheType() {
        return cacheType;
    }

    public void setCacheType(StateCacheType cacheType) {
        this.cacheType = cacheType;
    }

    public String getCacheKeyPrefix() {
        return cacheKeyPrefix;
    }

    public void setCacheKeyPrefix(String cacheKeyPrefix) {
        this.cacheKeyPrefix = cacheKeyPrefix;
    }
}
