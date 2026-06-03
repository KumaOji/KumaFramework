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

package com.kuma.boot.idempotent.idempotentenhance.adapter.mysql.config;

import com.kuma.boot.common.utils.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/** properties */
@ConfigurationProperties(prefix = IdempotentMysqlAdapterProperties.PREFIX)
public class IdempotentMysqlAdapterProperties {

    public static final String PREFIX = "kuma.boot.idempotent.enhance.adapter.mysql";
	private Boolean enabled = true;

	public Boolean getEnabled() {
		return enabled;
	}
}
