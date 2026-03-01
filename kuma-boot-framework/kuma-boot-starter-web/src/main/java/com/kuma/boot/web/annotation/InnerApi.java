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

package com.kuma.boot.web.annotation;

import java.lang.annotation.*;

/**
 * 仅仅作为标识Feign api接口
 *
 * {@snippet :
 * 		@ApiInfo(
 *               create = @Create(version = V2022_07, date = "2022-07-01 17:11:55"),
 *               update = {
 *                   @Update(version  = V2022_07, content = "主要修改了配置信息的接口查询", date = "2022-07-01 17:11:55"),
 *                   @Update(version  = V2022_08, content = "主要修改了配置信息的接口查询08", date = "2022-07-01 17:11:55")
 *               })
 *       @GetMapping("/sys/feign/dict/code")
 *       FeignDictResponse findByCode(@RequestParam(value = "code") String code);
 * }
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-02 09:30:27
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InnerApi {}
