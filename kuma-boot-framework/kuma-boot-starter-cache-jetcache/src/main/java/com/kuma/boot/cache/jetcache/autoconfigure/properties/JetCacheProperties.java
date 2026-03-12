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

package com.kuma.boot.cache.jetcache.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.HashMap;
import java.util.Map;

/**
 * JetCache 配置属性。
 *
 * @author kuma
 * @since 2022-07-03
 */
@RefreshScope
@ConfigurationProperties(JetCacheProperties.PREFIX)
public class JetCacheProperties {

    public static final String PREFIX = "kuma.boot.cache.jetcache";

    /** 是否启用（默认开启） */
    private boolean enabled = true;

    /** 敏感信息脱敏（默认开启） */
    private Boolean desensitization = true;

    /** 应用退出时清理远程缓存（默认关闭） */
    private Boolean clearRemoteOnExit = false;

    /** 是否允许缓存 null 值（默认允许） */
    private Boolean allowNullValues = true;

    /** cache name 中的分隔符，用于匹配 expires 配置 key（默认 {@code -}） */
    private String separator = "-";

    /** 按 cache name 配置的独立过期时间 */
    private Map<String, Expire> expires = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getDesensitization() {
        return desensitization;
    }

    public void setDesensitization(Boolean desensitization) {
        this.desensitization = desensitization;
    }

    public Boolean getClearRemoteOnExit() {
        return clearRemoteOnExit;
    }

    public void setClearRemoteOnExit(Boolean clearRemoteOnExit) {
        this.clearRemoteOnExit = clearRemoteOnExit;
    }

    public Boolean getAllowNullValues() {
        return allowNullValues;
    }

    public void setAllowNullValues(Boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public Map<String, Expire> getExpires() {
        return expires;
    }

    public void setExpires(Map<String, Expire> expires) {
        this.expires = expires;
    }

    @Override
    public String toString() {
        return "JetCacheProperties{"
                + "desensitization=" + desensitization
                + ", clearRemoteOnExit=" + clearRemoteOnExit
                + ", allowNullValues=" + allowNullValues
                + ", separator='" + separator + '\''
                + '}';
    }
}
