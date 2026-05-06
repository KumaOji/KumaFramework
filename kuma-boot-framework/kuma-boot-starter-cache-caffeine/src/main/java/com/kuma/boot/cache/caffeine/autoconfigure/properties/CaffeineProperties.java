/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.cloud.top/).
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

package com.kuma.boot.cache.caffeine.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CaffeineProperties 閰嶇疆
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-03 09:50:41
 */
@ConfigurationProperties(CaffeineProperties.PREFIX)
public class CaffeineProperties {

    public static final String PREFIX = "kuma.boot.cache.caffeine";

    /** 鏄惁鍚敤锛堥粯璁ゅ紑鍚級 */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
