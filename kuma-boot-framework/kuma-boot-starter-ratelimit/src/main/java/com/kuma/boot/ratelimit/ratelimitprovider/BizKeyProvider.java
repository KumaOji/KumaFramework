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

package com.kuma.boot.ratelimit.ratelimitprovider;

import com.kuma.boot.common.utils.log.LogUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
/**
 * 商业关键供应商
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-10-26 08:57:05
 */
public class BizKeyProvider implements BeanFactoryAware {

    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    private static final TemplateParserContext PARSER_CONTEXT = new TemplateParserContext();

    private final ExpressionParser parser = new SpelExpressionParser();

    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

    private BeanFactory beanFactory;

    public String getKeyName(JoinPoint joinPoint, RateLimit rateLimit) {
        Method method = getMethod(joinPoint);
        List<String> definitionKeys = getSpelDefinitionKey(rateLimit.keys(), method, joinPoint.getArgs());
        List<String> keyList = new ArrayList<>(definitionKeys);
        List<String> parameterKeys = getParameterKey(method.getParameters(), joinPoint.getArgs());
        keyList.addAll(parameterKeys);
        return StringUtils.collectionToDelimitedString(keyList, "", "-", "");
    }

    public Long getRateValue(RateLimit rateLimit) {
        if (StringUtils.hasText(rateLimit.rateExpression())) {
            String value = parser.parseExpression(resolve(rateLimit.rateExpression()), PARSER_CONTEXT)
                    .getValue(String.class);
            if (value != null) {
                return Long.parseLong(value);
            }
        }
        return rateLimit.rate();
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint
                        .getTarget()
                        .getClass()
                        .getDeclaredMethod(signature.getName(), method.getParameterTypes());
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }
        return method;
    }

    @SuppressWarnings("ConstantConditions")
    private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {
        List<String> definitionKeyList = new ArrayList<>();
        for (String definitionKey : definitionKeys) {
            if (!ObjectUtils.isEmpty(definitionKey)) {
                EvaluationContext context =
                        new MethodBasedEvaluationContext(null, method, parameterValues, nameDiscoverer);
                Object objKey = parser.parseExpression(definitionKey).getValue(context);
                definitionKeyList.add(ObjectUtils.nullSafeToString(objKey));
            }
        }
        return definitionKeyList;
    }

    private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {
        List<String> parameterKey = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getAnnotation(RateLimitKey.class) != null) {
                RateLimitKey keyAnnotation = parameters[i].getAnnotation(RateLimitKey.class);
                if (keyAnnotation.value().isEmpty()) {
                    Object parameterValue = parameterValues[i];
                    parameterKey.add(ObjectUtils.nullSafeToString(parameterValue));
                } else {
                    StandardEvaluationContext context = new StandardEvaluationContext(parameterValues[i]);
                    Object key = parser.parseExpression(keyAnnotation.value()).getValue(context);
                    parameterKey.add(ObjectUtils.nullSafeToString(key));
                }
            }
        }
        return parameterKey;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
    }

    private String resolve(String value) {
        return ((ConfigurableBeanFactory) this.beanFactory).resolveEmbeddedValue(value);
    }
}
