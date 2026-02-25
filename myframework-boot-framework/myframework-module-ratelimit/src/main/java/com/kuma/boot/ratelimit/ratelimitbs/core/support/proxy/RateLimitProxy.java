/*
 *  cn.hutool.core.util.ObjUtil
 *  com.kuma.boot.common.support.proxy.NoneProxy
 */
package com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy;

import cn.hutool.core.util.ObjUtil;
import com.kuma.boot.common.support.proxy.NoneProxy;
import com.kuma.boot.ratelimit.ratelimitbs.core.bs.RateLimitBs;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy.cglib.CglibProxy;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy.dynamic.DynamicProxy;
import java.lang.reflect.Proxy;

public final class RateLimitProxy {
    private RateLimitProxy() {
    }

    public static <T> T getProxy(T object) {
        RateLimitBs rateLimitBs = RateLimitBs.newInstance();
        return RateLimitProxy.getProxy(object, rateLimitBs);
    }

    public static <T> T getProxy(T object, RateLimitBs rateLimitBs) {
        if (ObjUtil.isNull(object)) {
            return (T)new NoneProxy(object).proxy();
        }
        Class<?> clazz = object.getClass();
        if (clazz.isInterface() || Proxy.isProxyClass(clazz)) {
            return (T)new DynamicProxy(object, rateLimitBs).proxy();
        }
        return (T)new CglibProxy(object, rateLimitBs).proxy();
    }
}

