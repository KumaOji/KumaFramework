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

package com.kuma.cloud.gateway.support;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.server.ServerWebExchange;

/**
 * 网关侧 {@link Result} 构建工具，与框架统一响应规范对齐。
 *
 * @author kuma
 * @since 2026-04-23
 */
public final class GatewayResults {

    private GatewayResults() {}

    public static <T> Result<T> fail(ServerWebExchange exchange, ResultEnum resultEnum) {
        return Result.<T>builder()
                .status(StatusEnum.FAILURE.name())
                .code(resultEnum.codeDesc())
                .message(resultEnum.getDesc())
                .requestId(resolveTraceId(exchange))
                .build();
    }

    public static String resolveTraceId(ServerWebExchange exchange) {
        String traceId = exchange.getRequest().getHeaders().getFirst(CommonConstants.KMC_TRACE_ID);
        return StrUtil.isNotBlank(traceId) ? traceId : IdGeneratorUtils.getIdStr();
    }
}
