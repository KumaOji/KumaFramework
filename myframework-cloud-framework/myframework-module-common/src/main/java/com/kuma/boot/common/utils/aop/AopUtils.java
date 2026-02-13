/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.aspectj.lang.JoinPoint
 *  org.springframework.aop.framework.AdvisedSupport
 *  org.springframework.aop.framework.AopProxy
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.util.ClassUtils
 */
package com.kuma.boot.common.utils.aop;

import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.util.ClassUtils;

public class AopUtils
extends org.springframework.aop.support.AopUtils {
    public static Object getTarget(Object proxy) {
        if (!AopUtils.isAopProxy((Object)proxy)) {
            return proxy;
        }
        try {
            if (AopUtils.isJdkDynamicProxy((Object)proxy)) {
                return AopUtils.getJdkDynamicProxyTargetObject(proxy);
            }
            return AopUtils.getCglibProxyTargetObject(proxy);
        }
        catch (Exception e) {
            LogUtils.error("\u83b7\u53d6\u4ee3\u7406\u5bf9\u8c61\u5f02\u5e38", e);
            return null;
        }
    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }

    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy)h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();
    }

    public static <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> annotationClass) {
        Method[] methods;
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        for (Method m : methods = joinPoint.getSignature().getDeclaringType().getMethods()) {
            if (!m.getName().equals(methodName) || m.getParameterTypes().length != arguments.length) continue;
            return m.getAnnotation(annotationClass);
        }
        return null;
    }

    public static Method getMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        List<? extends Class<?>> collect = Arrays.stream(arguments).map(Object::getClass).toList();
        return ClassUtils.getMethodIfAvailable((Class)joinPoint.getSignature().getDeclaringType(), (String)methodName, (Class[])collect.toArray(new Class[collect.size()]));
    }
}

