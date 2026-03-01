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

import java.util.HashMap;
import java.util.Map;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.request.AuthRequest;

/**
 * 扩展配置
 *
 * @author yangkai.shen
 * @since Created in 2019/10/9 11:21
 */
public class ExtendProperties {

    /** 枚举类全路径 */
    private Class<? extends AuthSource> enumClass;

    private Map<String, ExtendRequestConfig> config = new HashMap<>();

    public Class<? extends AuthSource> getEnumClass() {
        return enumClass;
    }

    public void setEnumClass(Class<? extends AuthSource> enumClass) {
        this.enumClass = enumClass;
    }

    public Map<String, ExtendRequestConfig> getConfig() {
        return config;
    }

    public void setConfig(Map<String, ExtendRequestConfig> config) {
        this.config = config;
    }

    public static class ExtendRequestConfig extends AuthConfig {

        /** 请求对应全路径 */
        private Class<? extends AuthRequest> requestClass;

        public Class<? extends AuthRequest> getRequestClass() {
            return requestClass;
        }

        public void setRequestClass(Class<? extends AuthRequest> requestClass) {
            this.requestClass = requestClass;
        }
    }
}
