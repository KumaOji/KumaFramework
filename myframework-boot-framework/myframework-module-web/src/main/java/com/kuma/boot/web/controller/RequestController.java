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

package com.kuma.boot.web.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.annotation.NotAuth;
import com.kuma.boot.web.annotation.BusinessApi;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@BusinessApi
@Validated
@RestController
@RequestMapping("/request/gateway")
public class RequestController {

    @NotAuth
    @Operation(summary = "网关请求测试", description = "网关请求测试", hidden = true)
    @GetMapping("/test")
    public Result<Boolean> gatewayTest() {
        LogUtils.info("网关请求测试----------------------------");
        return Result.success(true);
    }
}
