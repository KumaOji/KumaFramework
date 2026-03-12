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

package com.kuma.boot.actuator.endpoint.kmc;

import jakarta.annotation.Nullable;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 自定义状态端点 {@code /actuator/kmc}，支持运行时动态读写 status / detail。
 *
 * @author kuma
 * @since 2021-09-02
 */
@Endpoint(id = "kmc")
public class KmcEndPoint {

    private volatile String status = "up";
    private volatile String detail = "一切正常";

    @ReadOperation
    public Map<String, String> info() {
        return Map.of("status", status, "detail", detail);
    }

    @ReadOperation
    public Map<String, String> infoByKey(@Selector String key) {
        return switch (key) {
            case "status" -> Map.of("status", status);
            case "detail" -> Map.of("detail", detail);
            default       -> Map.of();
        };
    }

    /** 动态修改指标，e.g. POST /actuator/kmc/{key}  body: { "value": "..." } */
    @WriteOperation
    public void update(@Selector String key, @Nullable String value) {
        if (!StringUtils.hasText(value)) return;
        switch (key) {
            case "status" -> status = value;
            case "detail" -> detail = value;
            default -> {}
        }
    }
}
