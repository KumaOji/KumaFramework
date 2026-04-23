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

package com.kuma.cloud.gateway.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.ArrayList;
import java.util.List;

/**
 * GatewayCloudProperties
 *
 * @author kuma
 * @since 2026-04-23
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = GatewayCloudProperties.PREFIX)
public class GatewayCloudProperties {

    public static final String PREFIX = "kuma.cloud.gateway";

    private boolean enabled = true;
    private Cors cors = new Cors();
    private Auth auth = new Auth();
    private Log log = new Log();

    @Data
    public static class Cors {
        private boolean enabled = true;
        private List<String> allowedOrigins = new ArrayList<>(List.of("*"));
        private List<String> allowedMethods = new ArrayList<>(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        private List<String> allowedHeaders = new ArrayList<>(List.of("*"));
        private boolean allowCredentials = false;
        private long maxAge = 3600L;
    }

    @Data
    public static class Auth {
        private boolean enabled = false;
        private String tokenHeader = "Authorization";
        private List<String> whiteList = new ArrayList<>();
    }

    @Data
    public static class Log {
        private boolean enabled = true;
    }
}
