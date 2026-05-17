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

import com.kuma.boot.core.utils.context.EnableContextUtils;
import com.kuma.boot.security.spring.annotation.EnableOauth2ResourceServer;
//import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.annotation.*;

/**
 * KumaBootApplication
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:02:52
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableContextUtils
@ServletComponentScan(basePackages = {"com.kuma.boot.web.servlet"})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableOauth2ResourceServer
//@EnableEncryptableProperties
@SpringBootApplication
public @interface KumaBootApplication {}
