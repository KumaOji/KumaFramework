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

package com.kuma.boot.security.justauth.autoconfigure.properties;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import me.zhyd.oauth.config.AuthConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * JustAuth自动装配配置类
 *
 * @author yangkai.shen
 * @since Created in 2019-07-22 10:59
 */
@RefreshScope
@ConfigurationProperties(prefix = JustAuthProperties.PREFIX)
public class JustAuthProperties {

    public static final String PREFIX = "kuma.boot.security.justauth";

    /** 是否启用 JustAuth */
    private boolean enabled;

    /** JustAuth 默认配置 */
    private Map<String, AuthConfig> type = new HashMap<>();

    /** http 相关的配置，可设置请求超时时间和代理配置 */
    private JustAuthHttpConfig httpConfig;

    /** JustAuth 自定义配置 */
    @NestedConfigurationProperty private com.kuma.boot.security.justauth.autoconfigure.properties.ExtendProperties extend;

    /** 缓存配置类 */
    @NestedConfigurationProperty private com.kuma.boot.security.justauth.autoconfigure.properties.CacheProperties cache = new com.kuma.boot.security.justauth.autoconfigure.properties.CacheProperties();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, AuthConfig> getType() {
        return type;
    }

    public void setType(Map<String, AuthConfig> type) {
        this.type = type;
    }

    public JustAuthHttpConfig getHttpConfig() {
        return httpConfig;
    }

    public void setHttpConfig(JustAuthHttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    public com.kuma.boot.security.justauth.autoconfigure.properties.ExtendProperties getExtend() {
        return extend;
    }

    public void setExtend(com.kuma.boot.security.justauth.autoconfigure.properties.ExtendProperties extend) {
        this.extend = extend;
    }

    public com.kuma.boot.security.justauth.autoconfigure.properties.CacheProperties getCache() {
        return cache;
    }

    public void setCache(com.kuma.boot.security.justauth.autoconfigure.properties.CacheProperties cache) {
        this.cache = cache;
    }

    public static class JustAuthProxyConfig {

        private String type = Proxy.Type.HTTP.name();
        private String hostname;
        private int port;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class JustAuthHttpConfig {

        private int timeout;
        private Map<String, JustAuthProxyConfig> proxy;

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public Map<String, JustAuthProxyConfig> getProxy() {
            return proxy;
        }

        public void setProxy(Map<String, JustAuthProxyConfig> proxy) {
            this.proxy = proxy;
        }
    }
}
