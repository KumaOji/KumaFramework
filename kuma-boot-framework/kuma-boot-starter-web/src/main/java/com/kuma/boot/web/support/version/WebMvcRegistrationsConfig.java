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

package com.kuma.boot.web.support.version;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * WebMvcRegistrations
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-27 20:20:42
 */
@AutoConfiguration
@EnableConfigurationProperties(com.kuma.boot.web.support.version.ApiVersionProperties.class)
public class WebMvcRegistrationsConfig implements WebMvcRegistrations {

    private final com.kuma.boot.web.support.version.ApiVersionProperties apiVersionProperties;

    public WebMvcRegistrationsConfig( com.kuma.boot.web.support.version.ApiVersionProperties apiVersionProperties) {
        this.apiVersionProperties = apiVersionProperties;
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        if (!apiVersionProperties.isEnabled()) {
            return WebMvcRegistrations.super.getRequestMappingHandlerMapping();
        }

        LogUtils.info(
                "【初始化配置-ApiVersionRequestMappingHandlerMapping】默认配置为true，当前环境为true：RESTful"
                        + " API接口版本控制，执行初始化 ...");
        return new com.kuma.boot.web.support.version.ApiVersionRequestMappingHandlerMapping(apiVersionProperties);
    }
}
