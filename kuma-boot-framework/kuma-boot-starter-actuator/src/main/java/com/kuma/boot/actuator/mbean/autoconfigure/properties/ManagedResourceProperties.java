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

package com.kuma.boot.actuator.mbean.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * ManagedResource 配置属性。
 *
 * @author kuma
 * @since 2021-09-02
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = ManagedResourceProperties.PREFIX)
public class ManagedResourceProperties {

    public static final String PREFIX = "kuma.boot.actuator.managedresource";

    /** 是否启用 ManagedResource（默认开启） */
    private boolean enabled = true;
}
