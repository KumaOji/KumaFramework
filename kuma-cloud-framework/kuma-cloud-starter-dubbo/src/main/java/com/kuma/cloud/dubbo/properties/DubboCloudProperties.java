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

package com.kuma.cloud.dubbo.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Dubbo RPC 调用配置.
 *
 * <p>本 starter 仅做轻量增强：统一开关、上下文透传等；Dubbo 自身的协议、注册中心、
 * 超时、重试等仍通过原生的 {@code dubbo.*} 配置项设置，业务应用照常在启动类标注
 * {@code @EnableDubbo} 以扫描 {@code @DubboService} / {@code @DubboReference}。
 *
 * @author kuma
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = DubboCloudProperties.PREFIX)
public class DubboCloudProperties {

    public static final String PREFIX = "kuma.cloud.dubbo";

    /** 是否启用. */
    private boolean enabled = true;

    /** 上下文透传配置. */
    private Propagation propagation = new Propagation();

    /**
     * 上下文透传：服务 A 调用服务 B 时，将入站请求中的指定 attachment 自动带到出站调用，
     * 用于贯穿调用链的鉴权 Token、TraceId、灰度标签等。
     *
     * <p>透传基于 Dubbo {@code RpcContext} 的 attachment，由本 starter 的过滤器在
     * 消费端 / 提供端两侧自动激活。
     */
    @Data
    public static class Propagation {

        /** 是否启用上下文透传. */
        private boolean enabled = true;

        /** 需要透传的 attachment key（大小写敏感）. */
        private List<String> keys = new ArrayList<>(List.of(
                "Authorization",
                "X-Request-Id",
                "X-Trace-Id",
                "X-B3-TraceId",
                "X-B3-SpanId",
                "X-Gray-Tag"));
    }
}
