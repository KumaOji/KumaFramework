/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.security.justauth.autoconfigure.properties;

import java.time.Duration;

/**
 * 缓存配置类
 *
 * @author yangkai.shen
 * @since Created in 2019/8/31 10:18
 */
public class CacheProperties {

    /** 缓存类型 */
    private CacheType type = CacheType.DEFAULT;

    /** 缓存前缀，目前只对redis缓存生效，默认 JUSTAUTH::STATE:: */
    private String prefix = "JUSTAUTH::STATE::";

    /** 超时时长，目前只对redis缓存生效，默认3分钟 */
    private Duration timeout = Duration.ofMinutes(3);

    public CacheType getType() {
        return type;
    }

    public void setType(CacheType type) {
        this.type = type;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    /** 缓存类型 */
    public enum CacheType {
        /** 使用JustAuth内置的缓存 */
        DEFAULT,
        /** 使用Redis缓存 */
        REDIS,
        /** 自定义缓存 */
        CUSTOM
    }
}
