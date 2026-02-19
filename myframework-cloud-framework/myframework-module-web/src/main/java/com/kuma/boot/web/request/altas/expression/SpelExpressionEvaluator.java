/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.context.ApplicationContext
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ExpressionParser
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 */
package com.kuma.boot.web.request.altas.expression;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.altas.context.LogContext;
import com.kuma.boot.web.request.altas.exception.ExpressionEvaluationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelExpressionEvaluator {
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ConcurrentHashMap<String, Expression> expressionCache = new ConcurrentHashMap();
    private final ApplicationContext applicationContext;
    private final boolean cacheEnabled;
    private final long timeoutMs;
    private final boolean failSafe;
    private final Map<ExpressionType, ExpressionProcessor> processors = new HashMap<ExpressionType, ExpressionProcessor>();

    public SpelExpressionEvaluator(ApplicationContext applicationContext, boolean cacheEnabled, long timeoutMs, boolean failSafe) {
        this.applicationContext = applicationContext;
        this.cacheEnabled = cacheEnabled;
        this.timeoutMs = timeoutMs;
        this.failSafe = failSafe;
        this.initializeProcessors();
    }

    private void initializeProcessors() {
        this.processors.put(ExpressionType.PLAIN_TEXT, new PlainTextProcessor());
        this.processors.put(ExpressionType.PURE_SPEL, new PureSpelProcessor(this, this.failSafe));
        this.processors.put(ExpressionType.TEMPLATE, new TemplateProcessor(this, this.failSafe));
    }

    public String evaluateExpression(String expressionString, LogContext logContext) {
        if (expressionString == null || expressionString.trim().isEmpty()) {
            return "";
        }
        this.validateLogContextState(logContext, expressionString);
        try {
            ExpressionType type = ExpressionTypeDetector.detectType(expressionString);
            LogUtils.debug((String)"\u8868\u8fbe\u5f0f\u7c7b\u578b\u68c0\u6d4b: {} -> {}, LogContext.args: {}", (Object[])new Object[]{expressionString, type, logContext != null && logContext.getArgs() != null ? Arrays.toString(logContext.getArgs()) : "null"});
            ExpressionProcessor processor = this.processors.get((Object)type);
            if (processor == null) {
                throw new IllegalStateException("\u672a\u627e\u5230\u652f\u6301\u7684\u8868\u8fbe\u5f0f\u5904\u7406\u5668: " + String.valueOf((Object)type));
            }
            this.validateLogContextState(logContext, expressionString);
            String result = processor.processExpression(expressionString, logContext);
            LogUtils.debug((String)"Expression processing result: {} -> {}", (Object[])new Object[]{expressionString, result});
            return result;
        }
        catch (Exception e) {
            String errorMsg = "SpEL\u8868\u8fbe\u5f0f\u8bc4\u4f30\u5931\u8d25: " + expressionString;
            LogUtils.warn((String)(errorMsg + ", LogContext state: " + this.describeLogContextState(logContext)), (Object[])new Object[]{e});
            if (this.failSafe) {
                return "[" + errorMsg + "]";
            }
            throw new ExpressionEvaluationException(expressionString, errorMsg, e);
        }
    }

    public boolean evaluateCondition(String conditionString, LogContext logContext) {
        if (conditionString == null || conditionString.trim().isEmpty()) {
            return true;
        }
        try {
            String result = this.evaluateExpression(conditionString, logContext);
            return this.convertToBoolean(result);
        }
        catch (Exception e) {
            try {
                Expression expression = this.getExpression(conditionString);
                EvaluationContext evaluationContext = this.createEvaluationContext(logContext);
                Object result = this.evaluateWithTimeout(expression, evaluationContext);
                if (result instanceof Boolean) {
                    return (Boolean)result;
                }
                return result != null;
            }
            catch (Exception fallbackException) {
                String errorMsg = "\u6761\u4ef6\u8868\u8fbe\u5f0f\u8bc4\u4f30\u5931\u8d25: " + conditionString;
                LogUtils.warn((String)errorMsg, (Object[])new Object[]{fallbackException});
                if (this.failSafe) {
                    return true;
                }
                throw new ExpressionEvaluationException(conditionString, errorMsg, fallbackException);
            }
        }
    }

    private boolean convertToBoolean(String result) {
        if (result == null || result.trim().isEmpty()) {
            return false;
        }
        String trimmed = result.trim().toLowerCase();
        if ("true".equals(trimmed)) {
            return true;
        }
        if ("false".equals(trimmed)) {
            return false;
        }
        try {
            double number = Double.parseDouble(trimmed);
            return number != 0.0;
        }
        catch (NumberFormatException numberFormatException) {
            return true;
        }
    }

    private Expression getExpression(String expressionString) {
        if (this.cacheEnabled) {
            return this.expressionCache.computeIfAbsent(expressionString, arg_0 -> ((ExpressionParser)this.parser).parseExpression(arg_0));
        }
        return this.parser.parseExpression(expressionString);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    EvaluationContext createEvaluationContext(LogContext logContext) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (this.applicationContext != null) {
            context.setBeanResolver((evaluationContext, beanName) -> {
                try {
                    return this.applicationContext.getBean(beanName);
                }
                catch (Exception e) {
                    LogUtils.debug((String)"Failed to get bean: {}", (Object[])new Object[]{beanName, e});
                    return null;
                }
            });
        }
        if (logContext != null) {
            LogContext logContext2 = logContext;
            synchronized (logContext2) {
                Object[] args = logContext.getArgs();
                context.setVariable("args", (Object)(args != null ? args : new Object[]{}));
                context.setVariable("result", logContext.getResult());
                context.setVariable("exception", (Object)logContext.getException());
                context.setVariable("methodName", (Object)(logContext.getMethodName() != null ? logContext.getMethodName() : "unknown"));
                context.setVariable("className", (Object)(logContext.getClassName() != null ? logContext.getClassName() : "unknown"));
                context.setVariable("executionTime", (Object)logContext.getExecutionTime());
                context.setVariable("traceId", (Object)(logContext.getTraceId() != null ? logContext.getTraceId() : ""));
                Map<String, Object> variables = logContext.getVariables();
                if (variables != null) {
                    variables.forEach((arg_0, arg_1) -> ((StandardEvaluationContext)context).setVariable(arg_0, arg_1));
                }
                LogUtils.debug((String)"\u521b\u5efaEvaluationContext - method: {}, args: {}, argsLength: {}", (Object[])new Object[]{logContext.getMethodName(), args != null ? "non-null" : "null", args != null ? args.length : 0});
            }
        } else {
            LogUtils.warn((String)"LogContext is null, creating EvaluationContext with default values", (Object[])new Object[0]);
            context.setVariable("args", (Object)new Object[0]);
            context.setVariable("result", null);
            context.setVariable("exception", null);
            context.setVariable("methodName", (Object)"unknown");
            context.setVariable("className", (Object)"unknown");
            context.setVariable("executionTime", (Object)0L);
            context.setVariable("traceId", (Object)"");
        }
        return context;
    }

    private Object evaluateWithTimeout(Expression expression, EvaluationContext context) {
        if (this.timeoutMs <= 0L) {
            return expression.getValue(context);
        }
        long startTime = System.currentTimeMillis();
        Object result = expression.getValue(context);
        long duration = System.currentTimeMillis() - startTime;
        if (duration > this.timeoutMs) {
            LogUtils.warn((String)"SpEL expression execution timeout: {}ms > {}ms", (Object[])new Object[]{duration, this.timeoutMs});
        }
        return result;
    }

    public void clearCache() {
        this.expressionCache.clear();
    }

    public int getCacheSize() {
        return this.expressionCache.size();
    }

    public String[] getSupportedExpressionTypes() {
        return (String[])this.processors.keySet().stream().map(Enum::name).toArray(String[]::new);
    }

    public ExpressionType detectExpressionType(String expression) {
        return ExpressionTypeDetector.detectType(expression);
    }

    private void validateLogContextState(LogContext logContext, String expression) {
        if (logContext == null) {
            LogUtils.warn((String)"LogContext is null, expression: {}", (Object[])new Object[]{expression});
            return;
        }
        if (expression.contains("args") && logContext.getArgs() == null) {
            LogUtils.warn((String)"\u8868\u8fbe\u5f0f\u5f15\u7528args\u4f46LogContext.args\u4e3anull\uff0c\u8868\u8fbe\u5f0f: {}, LogContext: {}", (Object[])new Object[]{expression, this.describeLogContextState(logContext)});
        }
    }

    private String describeLogContextState(LogContext logContext) {
        if (logContext == null) {
            return "null";
        }
        return String.format("LogContext{methodName='%s', args=%s, argsLength=%d}", logContext.getMethodName(), logContext.getArgs() != null ? "non-null" : "null", logContext.getArgs() != null ? logContext.getArgs().length : 0);
    }
}

