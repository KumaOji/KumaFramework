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

package com.kuma.boot.web.validation.aop;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证参数方面
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-25 13:27:38
 */
@Aspect
public class VerifyParametersAspect {

    /**
     * 切点
     */
    @Pointcut("@annotation(com.kuma.boot.web.validation.aop.VerifyParameters)")
    public void serviceAspect() {}

    /**
     * service 方法前调用
     *
     * @param joinPoint
     */
    @Before("serviceAspect()")
    public void doBeforeService(JoinPoint joinPoint) {
        try {
            // 获取方法参数名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取方法
            Method method = signature.getMethod();
            // 获取参数名
            StandardReflectionParameterNameDiscoverer u =
                    new StandardReflectionParameterNameDiscoverer();
            String[] parameterNames = u.getParameterNames(method);
            Map<String, Object> params = new HashMap<>(8);
            params = getParamMap(joinPoint, method, parameterNames, params);
            // 获取注解
            com.kuma.boot.web.validation.aop.VerifyParameters verifyParameters = method.getAnnotation(
                    com.kuma.boot.web.validation.aop.VerifyParameters.class);

            // 参数名
            String paramName = verifyParameters.paramName();
            Object o = params.get(paramName);
            if (o == null) {
                throw new RuntimeException("参数不能为空");
            }
            if (!atLeastOnePropertyNotNull(o)) {
                throw new RuntimeException("请至少输入一个查询条件进行查询和导出");
            }
            // 开始时间和结束时间的参数名
            String s = verifyParameters.startTimeParamName();
            String e = verifyParameters.endTimeParamName();
            Map<?, ?> map = JacksonUtils.toMap(o);
            Object startTime = map.get(s);
            Object endTime = map.get(e);
            if (startTime != null || endTime != null) {
                if (startTime == null || endTime == null) {
                    throw new RuntimeException("开始时间和结束时间必须同时存在");
                }
                int time =
                        Integer.parseInt(String.valueOf(endTime))
                                - Integer.parseInt(String.valueOf(startTime));
                if (time > 30 * 24 * 60 * 60) {
                    throw new RuntimeException("时间间隔不能超过一个月");
                }
            }
        } catch (NumberFormatException ex) {
            LogUtils.error(ex.getMessage(), ex);
        }
    }

    private Map<String, Object> getParamMap(
            JoinPoint joinPoint,
            Method method,
            String[] parameterNames,
            Map<String, Object> params) {
        int i = 0;
        if (parameterNames != null) {
            for (String parameterName : parameterNames) {
                params.put(parameterName, joinPoint.getArgs()[i]);
                i++;
            }
        }
        return params;
    }

    public static boolean atLeastOnePropertyNotNull(Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            // 忽略serialVersionUID
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            field.setAccessible(true);
            try {
                if (field.get(obj) != null && !field.get(obj).toString().isEmpty()) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                LogUtils.error(e);
            }
        }
        return false;
    }

    /**
     * 方法后调用
     */
    @After("serviceAspect()")
    public void doAfterInService(JoinPoint joinPoint) {}
}
