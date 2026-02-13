/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.env.Environment
 *  org.springframework.expression.ExpressionException
 *  org.springframework.util.Assert
 */
package com.kuma.boot.common.support.expression;

import com.kuma.boot.common.support.expression.AbstractExpressionResolver;
import com.kuma.boot.common.support.expression.Context;
import com.kuma.boot.common.support.expression.ContextFactory;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.env.Environment;
import org.springframework.expression.ExpressionException;
import org.springframework.util.Assert;

public abstract class CacheExpressionResolver<CONTEXT, EXPRESSION>
extends AbstractExpressionResolver {
    private final Map<String, EXPRESSION> expressionCache = new ConcurrentHashMap<String, EXPRESSION>(256);
    private Environment environment;

    protected CacheExpressionResolver(ContextFactory contextFactory) {
        super(contextFactory);
    }

    protected CacheExpressionResolver() {
    }

    public Map<String, EXPRESSION> getExpressionCache() {
        return this.expressionCache;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public <T> T evaluate(String value, Context context, Class<T> returnType) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        try {
            EXPRESSION expression;
            value = this.wrapIfNecessary(value);
            if (this.environment != null) {
                value = this.environment.resolvePlaceholders(value);
            }
            if ((expression = this.expressionCache.get(value)) == null) {
                expression = this.compile(value);
                this.expressionCache.put(value, expression);
            }
            CONTEXT frameworkContext = this.transform(context);
            Assert.notNull(frameworkContext, (String)"frameworkContext not be null");
            return this.calculate(expression, frameworkContext, returnType);
        }
        catch (Throwable ex) {
            throw new ExpressionException("Expression parsing failed", ex);
        }
    }

    protected String wrapIfNecessary(String expression) {
        return expression;
    }

    protected abstract EXPRESSION compile(String var1) throws Throwable;

    protected abstract CONTEXT transform(Context var1);

    protected abstract <T> T calculate(EXPRESSION var1, CONTEXT var2, Class<T> var3) throws Exception;
}

