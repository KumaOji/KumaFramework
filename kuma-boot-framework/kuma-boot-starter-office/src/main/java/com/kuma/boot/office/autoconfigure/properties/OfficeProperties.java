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

package com.kuma.boot.office.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * XssProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:15:05
 */
@ConfigurationProperties(prefix = OfficeProperties.PREFIX)
public class OfficeProperties {

    public static final String PREFIX = "kuma.boot.office";

    private Boolean enabled = false;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
