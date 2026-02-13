/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.expression.BeanExpressionContextAccessor
 *  org.springframework.context.expression.BeanFactoryAccessor
 *  org.springframework.context.expression.EnvironmentAccessor
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ExpressionParser
 *  org.springframework.expression.ParserContext
 *  org.springframework.expression.PropertyAccessor
 *  org.springframework.expression.TypeConverter
 *  org.springframework.expression.TypeLocator
 *  org.springframework.expression.common.TemplateParserContext
 *  org.springframework.expression.spel.SpelParserConfiguration
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.MapAccessor
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.expression.spel.support.StandardTypeConverter
 *  org.springframework.expression.spel.support.StandardTypeLocator
 */
package com.kuma.boot.common.support.expression;

import com.kuma.boot.common.support.expression.CacheExpressionResolver;
import com.kuma.boot.common.support.expression.Context;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.MapAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;

public class SpringExpressionResolver
extends CacheExpressionResolver<EvaluationContext, Expression> {
    private final ExpressionParser expressionParser;
    private final ParserContext beanExpressionParserContext = new TemplateParserContext();

    public SpringExpressionResolver() {
        this(new SpelParserConfiguration());
    }

    public SpringExpressionResolver(ClassLoader beanClassLoader) {
        this(new SpelParserConfiguration(null, beanClassLoader));
    }

    public SpringExpressionResolver(SpelParserConfiguration configuration) {
        this.expressionParser = new SpelExpressionParser(configuration);
    }

    @Override
    protected Expression compile(String value) {
        return this.expressionParser.parseExpression(value, this.beanExpressionParserContext);
    }

    @Override
    protected String wrapIfNecessary(String expression) {
        if (!expression.contains("#")) {
            return expression;
        }
        if (!expression.contains("#{")) {
            return "#{" + expression + "}";
        }
        return expression;
    }

    @Override
    protected EvaluationContext transform(Context context) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.addPropertyAccessor((PropertyAccessor)new BeanExpressionContextAccessor());
        evaluationContext.addPropertyAccessor((PropertyAccessor)new BeanFactoryAccessor());
        evaluationContext.addPropertyAccessor((PropertyAccessor)new MapAccessor());
        evaluationContext.addPropertyAccessor((PropertyAccessor)new EnvironmentAccessor());
        evaluationContext.setTypeLocator((TypeLocator)new StandardTypeLocator());
        evaluationContext.setTypeConverter((TypeConverter)new StandardTypeConverter());
        evaluationContext.setVariables(context.asMap());
        return evaluationContext;
    }

    @Override
    protected <T> T calculate(Expression expression, EvaluationContext context, Class<T> returnType) {
        return (T)expression.getValue(context, returnType);
    }
}

