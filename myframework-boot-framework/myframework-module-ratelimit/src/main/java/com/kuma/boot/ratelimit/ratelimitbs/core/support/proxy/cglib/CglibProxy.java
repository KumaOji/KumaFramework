/*
 *  net.sf.cglib.proxy.Callback
 *  net.sf.cglib.proxy.Enhancer
 *  net.sf.cglib.proxy.MethodInterceptor
 *  net.sf.cglib.proxy.MethodProxy
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy.cglib;

import com.kuma.boot.ratelimit.ratelimitbs.core.bs.RateLimitBs;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy.AbstractProxy;
import java.lang.reflect.Method;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxy
extends AbstractProxy
implements MethodInterceptor {
    private final Object target;

    public CglibProxy(Object target) {
        this.target = target;
    }

    public CglibProxy(Object target, RateLimitBs bs) {
        super(bs);
        this.target = target;
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        this.rateLimitBs.tryAcquire(method, objects);
        return method.invoke(this.target, objects);
    }

    public Object proxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback((Callback)this);
        return enhancer.create();
    }
}

