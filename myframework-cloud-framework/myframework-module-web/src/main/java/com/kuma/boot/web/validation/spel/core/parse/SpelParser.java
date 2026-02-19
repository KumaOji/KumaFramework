/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.intellij.lang.annotations.Language
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.config.AutowireCapableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.expression.BeanFactoryResolver
 *  org.springframework.expression.BeanResolver
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 */
package com.kuma.boot.web.validation.spel.core.parse;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.validation.spel.core.exception.SpelParserException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelParser {
    private static final SpelExpressionParser parser = new SpelExpressionParser();
    private static final StandardEvaluationContext context = new StandardEvaluationContext();
    private static final Map<String, Expression> expressionCache = new ConcurrentHashMap<String, Expression>();

    private SpelParser() {
    }

    private static void init() {
        ApplicationContext applicationContext = SpelValidatorBeanRegistrar.getApplicationContext();
        if (applicationContext != null) {
            AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
            context.setBeanResolver((BeanResolver)new BeanFactoryResolver((BeanFactory)beanFactory));
        } else {
            LogUtils.info((String)"ApplicationContext is null, SpelParser will not support spring bean reference", (Object[])new Object[0]);
            LogUtils.info((String)"If you want to use spring bean reference in SpelParser, please use @EnableSpelValidatorBeanRegistrar to enable ApplicationContext support", (Object[])new Object[0]);
        }
        LogUtils.debug((String)"SpelParser init success", (Object[])new Object[0]);
    }

    @Nullable
    public static Object parse(@Language(value="spel") String expression, Object rootObject) {
        try {
            LogUtils.debug((String)"======> Parse expression [{}]", (Object[])new Object[]{expression});
            Expression parsed = expressionCache.computeIfAbsent(expression, arg_0 -> ((SpelExpressionParser)parser).parseExpression(arg_0));
            Object value = parsed.getValue((EvaluationContext)context, rootObject, Object.class);
            LogUtils.debug((String)"======> Parse result [{}]", (Object[])new Object[]{value});
            return value;
        }
        catch (RuntimeException e) {
            throw new SpelParserException("Parse expression error, expression [" + expression + "], message [" + e.getMessage() + "]", e);
        }
    }

    @NotNull
    public static <T> T parse(@Language(value="spel") String expression, Object rootObject, Class<T> requiredType) {
        Object any = SpelParser.parse(expression, rootObject);
        if (any == null) {
            throw new SpelParserException("Expression [" + expression + "] calculate result can not be null");
        }
        if (!requiredType.isInstance(any)) {
            throw new SpelParserException("Expression [" + expression + "] calculate result must be [" + requiredType.getName() + "]");
        }
        return (T)any;
    }

    static {
        SpelParser.init();
    }
}

