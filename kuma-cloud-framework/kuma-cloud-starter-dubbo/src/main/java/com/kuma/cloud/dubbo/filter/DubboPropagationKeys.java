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

package com.kuma.cloud.dubbo.filter;

import java.util.List;

/**
 * 上下文透传配置的静态持有者.
 *
 * <p>Dubbo 过滤器经由 SPI 加载、不受 Spring 容器管理，无法直接注入
 * {@code DubboCloudProperties}。自动配置类在启动时调用 {@link #configure} 把配置写入此处，
 * 过滤器再从此读取。在 Spring 未介入的场景下（如纯 Dubbo 用法），默认值同样可用。
 *
 * @author kuma
 */
public final class DubboPropagationKeys {

    /** 默认透传的 attachment key，与 OpenFeign 请求头透传保持一致. */
    private static final List<String> DEFAULT_KEYS = List.of(
            "Authorization",
            "X-Request-Id",
            "X-Trace-Id",
            "X-B3-TraceId",
            "X-B3-SpanId",
            "X-Gray-Tag");

    private static volatile boolean enabled = true;

    private static volatile List<String> keys = DEFAULT_KEYS;

    private DubboPropagationKeys() {}

    /**
     * 由自动配置类在启动时写入透传配置.
     *
     * @param enabled 是否启用透传
     * @param keys    需要透传的 attachment key；为空时回退到默认值
     */
    public static void configure(boolean enabled, List<String> keys) {
        DubboPropagationKeys.enabled = enabled;
        if (keys != null && !keys.isEmpty()) {
            DubboPropagationKeys.keys = List.copyOf(keys);
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static List<String> getKeys() {
        return keys;
    }
}
