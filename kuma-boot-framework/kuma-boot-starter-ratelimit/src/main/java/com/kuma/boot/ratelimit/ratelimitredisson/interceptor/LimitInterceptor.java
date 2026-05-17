/*
 * Copyright 2021-2024 spring-boot-extension the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.ratelimit.ratelimitredisson.interceptor;

import com.kuma.boot.core.support.aop.AnnotationAbstractPointcutTypeAdvisor;
import com.kuma.boot.ratelimit.ratelimitredisson.LimitExecutor;
import com.kuma.boot.ratelimit.ratelimitredisson.LimitSupport;
import com.kuma.boot.ratelimit.ratelimitredisson.annotation.Limit;
import com.kuma.boot.ratelimit.ratelimitredisson.exception.LimitException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectProvider;

/**
 * The type Limit interceptor.
 *
 * @author livk
 */
public class LimitInterceptor extends AnnotationAbstractPointcutTypeAdvisor<Limit> {

    /**
     * 执行器
     */
    private final ObjectProvider<LimitExecutor> provider;

    public LimitInterceptor(ObjectProvider<LimitExecutor> provider) {
        this.provider = provider;
    }

    @Override
    protected Object invoke(MethodInvocation invocation, Limit limit) throws Throwable {
        LimitSupport limitSupport = new LimitSupport(provider.getIfAvailable());
        boolean status = limitSupport.exec(limit, invocation.getMethod(), invocation.getArguments());
        if (status) {
            return invocation.proceed();
        } else {
            throw new LimitException("key=" + limit.key() + " is reach max limited access count=" + limit.rate()
                    + " within period=" + limit.rateInterval() + " " + limit.rateIntervalUnit().name());
        }
    }

}
