/*
 *  com.kuma.boot.common.support.aop.AnnotationAbstractPointcutTypeAdvisor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.beans.factory.ObjectProvider
 */
package com.kuma.boot.ratelimit.ratelimitredisson.interceptor;

import com.kuma.boot.common.support.aop.AnnotationAbstractPointcutTypeAdvisor;
import com.kuma.boot.ratelimit.ratelimitredisson.LimitExecutor;
import com.kuma.boot.ratelimit.ratelimitredisson.LimitSupport;
import com.kuma.boot.ratelimit.ratelimitredisson.annotation.Limit;
import com.kuma.boot.ratelimit.ratelimitredisson.exception.LimitException;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectProvider;

public class LimitInterceptor
extends AnnotationAbstractPointcutTypeAdvisor<Limit> {
    private final ObjectProvider<LimitExecutor> provider;

    public LimitInterceptor(ObjectProvider<LimitExecutor> provider) {
        this.provider = provider;
    }

    protected Object invoke(MethodInvocation invocation, Limit limit) throws Throwable {
        LimitSupport limitSupport = new LimitSupport((LimitExecutor)this.provider.getIfAvailable());
        boolean status = limitSupport.exec(limit, invocation.getMethod(), invocation.getArguments());
        if (status) {
            return invocation.proceed();
        }
        throw new LimitException("key=" + limit.key() + " is reach max limited access count=" + limit.rate() + " within period=" + limit.rateInterval() + " " + limit.rateIntervalUnit().name());
    }
}

