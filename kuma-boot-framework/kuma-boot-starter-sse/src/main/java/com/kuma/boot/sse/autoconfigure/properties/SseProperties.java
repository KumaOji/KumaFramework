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

package com.kuma.boot.sse.autoconfigure.properties;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSE 模块配置（{@code kuma.boot.sse.*}）.
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = SseProperties.PREFIX)
public class SseProperties {

    public static final String PREFIX = "kuma.boot.sse";

    /** 是否启用 SSE 支持. */
    private boolean enabled = true;

    /** 单个 SseEmitter 超时时长，0 表示永不超时. */
    private Duration timeout = Duration.ofMinutes(5);

    /** 心跳推送间隔；为 0 时不启用心跳. */
    private Duration heartbeatInterval = Duration.ofSeconds(30);

    /** 单个 clientId 允许的最大连接数（≥1），超出时踢出旧连接. */
    private int maxConnectionsPerClient = 1;
}
