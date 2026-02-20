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

package com.kuma.boot.web.mvc.resolver;

import java.lang.annotation.*;

/**
 * 请求json参数处理注解
 * @author wangpengchao01
 * @since 2022-11-07 14:18
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestJsonParam {
    /**
     * 绑定的请求参数名
     */
    String value() default "body";

    /**
     * 参数是否必须
     */
    boolean required() default false;

    /**
     * 默认值
     */
    String defaultValue() default "";

    /**
     * 集合json反序列化后记录的类型
     */
    Class recordClass() default Void.class;
}
