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

package com.kuma.boot.web.request.properties;

import com.kuma.boot.web.request.enums.RequestLoggerTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 瀹¤鏃ュ織閰嶇疆
 *
 * @author kuma
 * @version 2022.03
 * @since 2020/5/2 11:19
 */
@ConfigurationProperties(prefix = RequestLoggerProperties.PREFIX)
public class RequestLoggerProperties {

    public static final String PREFIX = "kuma.boot.web.request";

    /** 鏄惁寮€鍚姹傛棩蹇?*/
    private Boolean enabled = true;

    /** 鏃ュ織璁板綍绫诲瀷(logger/redis/kafka) */
    private RequestLoggerTypeEnum[] types =
            new RequestLoggerTypeEnum[] {RequestLoggerTypeEnum.LOGGER};

    public RequestLoggerProperties() {}

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public RequestLoggerTypeEnum[] getTypes() {
        if (types == null || types.length == 0) {
            return new RequestLoggerTypeEnum[] {RequestLoggerTypeEnum.LOGGER};
        }
        return types;
    }

    public void setTypes( RequestLoggerTypeEnum[] types) {
        this.types = types;
    }
}
