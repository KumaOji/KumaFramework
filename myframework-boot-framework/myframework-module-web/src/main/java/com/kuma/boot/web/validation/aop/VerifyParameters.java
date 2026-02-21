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

package com.kuma.boot.web.validation.aop;

import java.lang.annotation.*;

/**
 * 基于反射校验参数
 *
 * 增加注解对入参进行校验，保证至少有一个参数不为空，若是有时间参数，则起始时间和结束时间之间的距离不能超过30天。
 *
 * <pre class="code">
 *     @ApiOperation(value = "查询列表")
 *        @GetMapping("/test")
 *        @VerifyParameters(startTimeParamName = "beginTime", endTimeParamName = "endTime",paramName = "orderRequest")
 * 		public Page<Map<String, Object>> findOrderTestList(Pageable pageable, ERPOrderRequest orderRequest) {
 *     			log.info("模拟逻辑处理");
 *     		return null;
 *        }
 * </pre>
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VerifyParameters {
    /**
     * 起始时间参数名称
     */
    String startTimeParamName() default "startTime";

    /**
     * 结束时间参数名称
     */
    String endTimeParamName() default "endTime";

    /**
     * 需要校验的参数名称
     */
    String paramName() default "";
}
