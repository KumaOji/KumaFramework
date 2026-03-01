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

package com.kuma.boot.web.support.ext;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Target 标识在类型上。类，枚举等
 * @Retention
 *   runtime 在运行时生效
 *   source 在源代码级别生效不深入到编译字节码中
 *   class 在编译期会在字节码中生效
 * 用以扫描需要进行动态代理的package路径
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(value = com.kuma.boot.web.support.ext.MultiBizProxyRegister.class)
public @interface RouterBaseScan {
    String path() default "";
}
