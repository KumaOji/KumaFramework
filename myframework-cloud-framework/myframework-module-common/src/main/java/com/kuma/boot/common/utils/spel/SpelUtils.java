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

package com.kuma.boot.common.utils.spel;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * SpelUtil
 *
 * @author kuma
 * @version 2022.09
 * @since 2023-01-03 11:31:03
 */
public class SpelUtils {

    /** ?表达式解析器 spel表达式解析器 */
    private static final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    /** 参数名称发现者 参数名发现器 */
    private static final DefaultParameterNameDiscoverer parameterNameDiscoverer =
            new DefaultParameterNameDiscoverer();

    /**
     * 转换 jspl参数
     * @param joinPoint
     * @param spel
     * @return {@link String }
     * @since 2023-01-03 11:31:03
     */
    public static String compileParams(JoinPoint joinPoint, String spel) { // Spel表达式解析日志信息
        // 获得方法参数名数组
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
        if (parameterNames != null && parameterNames.length > 0) {
            EvaluationContext context = new StandardEvaluationContext();

            // 获取方法参数值
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                // 替换spel里的变量值为实际值， 比如 #user --> user对象
                context.setVariable(parameterNames[i], args[i]);
            }
            return spelExpressionParser.parseExpression(spel).getValue(context).toString();
        }
        return "";
    }

    /**
     * 转换 jspl参数
     * @param joinPoint joinPoint
     * @param rvt rvt
     * @param spel jspl表达式
     * @return {@link String }
     * @since 2023-01-03 11:31:04
     */
    public static String compileParams(
            JoinPoint joinPoint, Object rvt, String spel) { // Spel表达式解析日志信息
        // 获得方法参数名数组
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
        if (parameterNames != null && parameterNames.length > 0) {
            EvaluationContext context = new StandardEvaluationContext();

            // 获取方法参数值
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                // 替换spel里的变量值为实际值， 比如 #user --> user对象
                context.setVariable(parameterNames[i], args[i]);
            }
            context.setVariable("rvt", rvt);
            return spelExpressionParser.parseExpression(spel).getValue(context).toString();
        }
        return "";
    }
}
