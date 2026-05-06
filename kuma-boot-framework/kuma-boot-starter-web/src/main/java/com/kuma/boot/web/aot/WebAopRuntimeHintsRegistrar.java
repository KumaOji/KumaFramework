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

package com.kuma.boot.web.aot;

import static org.springframework.aot.hint.MemberCategory.ACCESS_DECLARED_FIELDS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;

import com.kuma.boot.web.annotation.CountTime;
import com.kuma.boot.web.aop.CountTimeAop;
import com.kuma.boot.web.aop.aop.caching.Caching;
import com.kuma.boot.web.aop.aop.caching.CachingAspect;
import com.kuma.boot.web.aop.aop.limiting.LimitRate;
import com.kuma.boot.web.aop.aop.limiting.LimitRateAspect;
import com.kuma.boot.web.aop.aop.log.LogExecutionElapsed;
import com.kuma.boot.web.aop.aop.log.LogExecutionElapsedAspect;
import com.kuma.boot.web.aop.aop.validator.ValidateParameter;
import com.kuma.boot.web.aop.aop.validator.RequestParamAspect;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/** Runtime hints for kuma-web custom AOP aspects and annotations (GraalVM native). */
public class WebAopRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ReflectionHints reflection = hints.reflection();
        for (Class<?> type :
                new Class<?>[] {
                    CountTime.class,
                    CountTimeAop.class,
                    Caching.class,
                    CachingAspect.class,
                    LimitRate.class,
                    LimitRateAspect.class,
                    LogExecutionElapsed.class,
                    LogExecutionElapsedAspect.class,
                    ValidateParameter.class,
                    RequestParamAspect.class,
                }) {
            reflection.registerType(
                    type, INVOKE_PUBLIC_METHODS, INVOKE_DECLARED_CONSTRUCTORS, ACCESS_DECLARED_FIELDS);
        }
    }
}
