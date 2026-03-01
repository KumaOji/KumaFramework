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

package com.kuma.boot.security.spring.annotation;

import com.kuma.boot.security.spring.autoconfigure.JwtDecoderAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.ReactiveJwtDecoderAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.SecurityAccessAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.SecurityAuthenticationAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.SecurityAuthorizationAutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 开启oauth2资源
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-06-22 14:24:32
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({
        SecurityAccessAutoConfiguration.class,
        SecurityAuthorizationAutoConfiguration.class,
        ReactiveJwtDecoderAutoConfiguration.class,
        JwtDecoderAutoConfiguration.class,
        SecurityAuthenticationAutoConfiguration.class,
})
public @interface EnableSecurityConfiguration {}
