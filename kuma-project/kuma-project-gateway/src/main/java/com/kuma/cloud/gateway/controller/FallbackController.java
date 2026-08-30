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

package com.kuma.cloud.gateway.controller;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.cloud.gateway.support.GatewayResults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 熔断降级兜底端点，由路由 {@code CircuitBreaker} 过滤器的 {@code fallbackUri} 转发触发。
 *
 * @author kuma
 * @since 2026-04-23
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/blog")
    public Mono<Void> blogFallback(ServerWebExchange exchange) {
        return writeFallback(exchange, ResultEnum.TIMEOUT_ERROR);
    }

    @GetMapping("/uaa")
    public Mono<Void> uaaFallback(ServerWebExchange exchange) {
        return writeFallback(exchange, ResultEnum.TIMEOUT_ERROR);
    }

    private static Mono<Void> writeFallback(ServerWebExchange exchange, ResultEnum resultEnum) {
        Result<Void> body = GatewayResults.fail(exchange, resultEnum);
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(JacksonUtils.toJsonAsBytes(body))));
    }
}
