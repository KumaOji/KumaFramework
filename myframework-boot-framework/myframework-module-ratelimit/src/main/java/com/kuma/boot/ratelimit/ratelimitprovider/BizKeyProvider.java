/*
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.context.expression.BeanFactoryResolver
 *  org.springframework.context.expression.MethodBasedEvaluationContext
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.expression.BeanResolver
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.ExpressionParser
 *  org.springframework.expression.ParserContext
 *  org.springframework.expression.common.TemplateParserContext
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.ratelimit.ratelimitprovider;

import com.kuma.boot.common.utils.log.LogUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class BizKeyProvider
implements BeanFactoryAware {
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    private static final TemplateParserContext PARSER_CONTEXT = new TemplateParserContext();
    private final ExpressionParser parser = new SpelExpressionParser();
    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    private BeanFactory beanFactory;

    public String getKeyName(JoinPoint joinPoint, RateLimit rateLimit) {
        Method method = this.getMethod(joinPoint);
        List<String> definitionKeys = this.getSpelDefinitionKey(rateLimit.keys(), method, joinPoint.getArgs());
        ArrayList<String> keyList = new ArrayList<String>(definitionKeys);
        List<String> parameterKeys = this.getParameterKey(method.getParameters(), joinPoint.getArgs());
        keyList.addAll(parameterKeys);
        return StringUtils.collectionToDelimitedString(keyList, (String)"", (String)"-", (String)"");
    }

    public Long getRateValue(RateLimit rateLimit) {
        String value;
        if (StringUtils.hasText((String)rateLimit.rateExpression()) && (value = (String)this.parser.parseExpression(this.resolve(rateLimit.rateExpression()), (ParserContext)PARSER_CONTEXT).getValue(String.class)) != null) {
            return Long.parseLong(value);
        }
        return rateLimit.rate();
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), method.getParameterTypes());
            }
            catch (Exception e) {
                LogUtils.error((Throwable)e);
            }
        }
        return method;
    }

    private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {
        ArrayList<String> definitionKeyList = new ArrayList<String>();
        for (String definitionKey : definitionKeys) {
            if (ObjectUtils.isEmpty((Object)definitionKey)) continue;
            MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, this.nameDiscoverer);
            Object objKey = this.parser.parseExpression(definitionKey).getValue((EvaluationContext)context);
            definitionKeyList.add(ObjectUtils.nullSafeToString((Object)objKey));
        }
        return definitionKeyList;
    }

    private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {
        ArrayList<String> parameterKey = new ArrayList<String>();
        for (int i = 0; i < parameters.length; ++i) {
            if (parameters[i].getAnnotation(RateLimitKey.class) == null) continue;
            RateLimitKey keyAnnotation = parameters[i].getAnnotation(RateLimitKey.class);
            if (keyAnnotation.value().isEmpty()) {
                Object parameterValue = parameterValues[i];
                parameterKey.add(ObjectUtils.nullSafeToString((Object)parameterValue));
                continue;
            }
            StandardEvaluationContext context = new StandardEvaluationContext(parameterValues[i]);
            Object key = this.parser.parseExpression(keyAnnotation.value()).getValue((EvaluationContext)context);
            parameterKey.add(ObjectUtils.nullSafeToString((Object)key));
        }
        return parameterKey;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.evaluationContext.setBeanResolver((BeanResolver)new BeanFactoryResolver(beanFactory));
    }

    private String resolve(String value) {
        return ((ConfigurableBeanFactory)this.beanFactory).resolveEmbeddedValue(value);
    }
}

