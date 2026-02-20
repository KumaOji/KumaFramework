/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.kuma.boot.common.utils.reflect.AnnotationUtils
 *  io.github.itning.retry.Retryer
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.Signature
 *  org.aspectj.lang.reflect.MethodSignature
 */
package com.kuma.boot.web.support.methodPartAndRetryer;

import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.reflect.AnnotationUtils;
import io.github.itning.retry.Retryer;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

public class RetryAspectAop {
    public Object around(final ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        final Object[] args = point.getArgs();
        boolean isHandler1 = this.isHandler(args);
        if (isHandler1) {
            String className = point.getSignature().getDeclaringTypeName();
            String methodName = point.getSignature().getName();
            Object firstArg = args[0];
            List paramList = (List)firstArg;
            Method method = this.getCurrentMethod(point);
            MethodPartAndRetryer retryable = (MethodPartAndRetryer)AnnotationUtils.getAnnotation((Method)method, MethodPartAndRetryer.class);
            Retryer retryer = new RetryUtil().getDefaultRetryer(retryable.times(), retryable.waitTime());
            List requestList = Lists.partition((List)paramList, (int)retryable.parts());
            for (List partList : requestList) {
                args[0] = partList;
                Object tempResult = retryer.call((Callable)new Callable<Object>(){
                    {
                        Objects.requireNonNull(this$0);
                    }

                    @Override
                    public Object call() throws Exception {
                        try {
                            return point.proceed(args);
                        }
                        catch (Throwable throwable) {
                            throw new RuntimeException("\u5206\u7247\u91cd\u8bd5\u51fa\u9519");
                        }
                    }
                });
                if (null != tempResult) {
                    if (tempResult instanceof Boolean) {
                        if (!((Boolean)tempResult).booleanValue()) {
                            throw new RuntimeException("\u5206\u7247\u6267\u884c\u62a5\u9519!");
                        }
                        result = tempResult;
                        continue;
                    }
                    if (tempResult instanceof List) {
                        if (result == null) {
                            result = Lists.newArrayList();
                        }
                        ((List)result).addAll((List)tempResult);
                        continue;
                    }
                    throw new RuntimeException("\u4e0d\u652f\u6301\u8be5\u8fd4\u56de\u7c7b\u578b");
                }
                throw new RuntimeException("\u8c03\u7528\u7ed3\u679c\u4e3a\u7a7a");
            }
        } else {
            result = point.proceed(args);
        }
        return result;
    }

    private boolean isHandler(Object[] args) {
        Object firstArg;
        boolean isHandler = false;
        if (null != args && args.length > 0 && (firstArg = args[0]) != null && firstArg instanceof List && ((List)firstArg).size() > 1) {
            isHandler = true;
        }
        return isHandler;
    }

    private Method getCurrentMethod(ProceedingJoinPoint point) {
        try {
            Signature sig = point.getSignature();
            MethodSignature msig = (MethodSignature)sig;
            Object target = point.getTarget();
            return target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}

