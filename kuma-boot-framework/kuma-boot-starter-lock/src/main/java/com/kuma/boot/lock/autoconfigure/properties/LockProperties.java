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

package com.kuma.boot.lock.autoconfigure.properties;

import com.kuma.boot.lock.enums.LockTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RedisLockProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-07 21:15:27
 */
@ConfigurationProperties(prefix = LockProperties.PREFIX)
public class LockProperties {

    public static final String PREFIX = "kuma.boot.lock";

    private boolean enabled = true;

    private LockTypeEnum type = LockTypeEnum.LOCAL;

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LockTypeEnum getType() {
        return type;
    }

    public void setType(LockTypeEnum type) {
        this.type = type;
    }
}
