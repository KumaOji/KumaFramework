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
 * RESTful API接口版本定义
 *
 * <p>为接口提供优雅的版本路径，效果如下：
 *
 * <blockquote>
 *
 * <p>&#064;ApiVersion(1)
 *
 * <p>&#064;RequestMapping("/{version}/user")
 *
 * </blockquote>
 *
 * 实际请求路径值：/v1/user
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-27 20:20:55
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ApiVersion {

    /**
     * RESTful API接口版本号
     *
     * <p>最近优先原则：在方法上的 {@link ApiVersion} 可覆盖在类上面的 {@link ApiVersion}，如下：
     *
     * <p>类上面的 {@link #value()} 值 = 1.1，
     *
     * <p>方法上面的 {@link #value()} 值 = 2.1，
     *
     * <p>最终效果：v2.1
     */
    double value() default 1;

    /**
     * 是否废弃版本接口
     *
     * <p>客户端请求废弃版本接口时将抛出错误提示：
     *
     * <p>当前版本已停用，请升级到最新版本
     */
    boolean deprecated() default false;
}
