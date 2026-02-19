/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.annotation.After
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.annotation.Before
 *  org.aspectj.lang.annotation.Pointcut
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.core.StandardReflectionParameterNameDiscoverer
 */
package com.kuma.boot.web.validation.aop;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

@Aspect
public class VerifyParametersAspect {
    @Pointcut(value="@annotation(com.kuma.boot.web.validation.aop.VerifyParameters)")
    public void serviceAspect() {
    }

    @Before(value="serviceAspect()")
    public void doBeforeService(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature)joinPoint.getSignature();
            Method method = signature.getMethod();
            StandardReflectionParameterNameDiscoverer u = new StandardReflectionParameterNameDiscoverer();
            String[] parameterNames = u.getParameterNames(method);
            Map<String, Object> params = new HashMap<String, Object>(8);
            params = this.getParamMap(joinPoint, method, parameterNames, params);
            VerifyParameters verifyParameters = method.getAnnotation(VerifyParameters.class);
            String paramName = verifyParameters.paramName();
            Object o = params.get(paramName);
            if (o == null) {
                throw new RuntimeException("\u53c2\u6570\u4e0d\u80fd\u4e3a\u7a7a");
            }
            if (!VerifyParametersAspect.atLeastOnePropertyNotNull(o)) {
                throw new RuntimeException("\u8bf7\u81f3\u5c11\u8f93\u5165\u4e00\u4e2a\u67e5\u8be2\u6761\u4ef6\u8fdb\u884c\u67e5\u8be2\u548c\u5bfc\u51fa");
            }
            String s = verifyParameters.startTimeParamName();
            String e = verifyParameters.endTimeParamName();
            Map map = JacksonUtils.toMap((Object)o);
            Object startTime = map.get(s);
            Object endTime = map.get(e);
            if (startTime != null || endTime != null) {
                if (startTime == null || endTime == null) {
                    throw new RuntimeException("\u5f00\u59cb\u65f6\u95f4\u548c\u7ed3\u675f\u65f6\u95f4\u5fc5\u987b\u540c\u65f6\u5b58\u5728");
                }
                int time = Integer.parseInt(String.valueOf(endTime)) - Integer.parseInt(String.valueOf(startTime));
                if (time > 2592000) {
                    throw new RuntimeException("\u65f6\u95f4\u95f4\u9694\u4e0d\u80fd\u8d85\u8fc7\u4e00\u4e2a\u6708");
                }
            }
        }
        catch (NumberFormatException ex) {
            LogUtils.error((String)ex.getMessage(), (Object[])new Object[]{ex});
        }
    }

    private Map<String, Object> getParamMap(JoinPoint joinPoint, Method method, String[] parameterNames, Map<String, Object> params) {
        int i = 0;
        if (parameterNames != null) {
            for (String parameterName : parameterNames) {
                params.put(parameterName, joinPoint.getArgs()[i]);
                ++i;
            }
        }
        return params;
    }

    public static boolean atLeastOnePropertyNotNull(Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if ("serialVersionUID".equals(field.getName())) continue;
            field.setAccessible(true);
            try {
                if (field.get(obj) == null || field.get(obj).toString().isEmpty()) continue;
                return true;
            }
            catch (IllegalAccessException e) {
                LogUtils.error((Throwable)e);
            }
        }
        return false;
    }

    @After(value="serviceAspect()")
    public void doAfterInService(JoinPoint joinPoint) {
    }
}

