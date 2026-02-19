/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.StandardReflectionParameterNameDiscoverer
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ParserContext
 *  org.springframework.expression.common.TemplateParserContext
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 */
package com.kuma.boot.web.aop.aop.util;

import java.lang.reflect.Method;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpringExpressionUtils {
    public static <T> T parse(String spel, Method method, Object[] args, Class<T> clazz) {
        String[] parameterNames;
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (args != null && (parameterNames = new StandardReflectionParameterNameDiscoverer().getParameterNames(method)) != null) {
            int len = args.length;
            for (int i = 0; i < len; ++i) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Expression expression = spelExpressionParser.parseExpression(spel);
        return (T)expression.getValue((EvaluationContext)context, clazz);
    }

    public static String parseTemplate(String template, Object object) {
        TemplateParserContext templateParserContext = new TemplateParserContext();
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(template, (ParserContext)templateParserContext);
        return (String)expression.getValue(object, String.class);
    }
}

