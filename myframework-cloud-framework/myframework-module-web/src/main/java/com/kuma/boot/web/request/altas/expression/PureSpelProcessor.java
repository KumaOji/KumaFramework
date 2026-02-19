/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ExpressionParser
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 */
package com.kuma.boot.web.request.altas.expression;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.altas.context.LogContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class PureSpelProcessor
implements ExpressionProcessor {
    private final ExpressionParser parser = new SpelExpressionParser();
    private final SpelExpressionEvaluator mainEvaluator;
    private final boolean failSafe;

    public PureSpelProcessor(SpelExpressionEvaluator mainEvaluator, boolean failSafe) {
        this.mainEvaluator = mainEvaluator;
        this.failSafe = failSafe;
    }

    @Override
    public String processExpression(String expression, LogContext logContext) {
        if (expression == null || expression.trim().isEmpty()) {
            return "";
        }
        if (logContext == null) {
            LogUtils.warn((String)"PureSpelProcessor received null LogContext, expression: {}", (Object[])new Object[]{expression});
            return this.failSafe ? "[LogContext\u4e3anull]" : expression;
        }
        if (expression.contains("args") && logContext.getArgs() == null) {
            String warnMsg = String.format("\u8868\u8fbe\u5f0f\u5f15\u7528args\u4f46LogContext.args\u4e3anull\uff0c\u8868\u8fbe\u5f0f: %s, method: %s", expression, logContext.getMethodName());
            LogUtils.warn((String)warnMsg, (Object[])new Object[0]);
            if (this.failSafe) {
                return "[" + warnMsg + "]";
            }
        }
        try {
            EvaluationContext evaluationContext;
            String processedExpression;
            Expression spelExpression;
            Object result;
            String cleanExpression = expression.trim();
            if (cleanExpression.startsWith("#{") && cleanExpression.endsWith("}")) {
                cleanExpression = cleanExpression.substring(2, cleanExpression.length() - 1);
            }
            return (result = (spelExpression = this.parser.parseExpression(processedExpression = this.ensureVariableSyntax(cleanExpression))).getValue(evaluationContext = this.createSafeEvaluationContext(logContext))) != null ? result.toString() : "";
        }
        catch (Exception e) {
            String errorMsg = "\u7eafSpEL\u8868\u8fbe\u5f0f\u5904\u7406\u5931\u8d25: " + expression;
            LogUtils.warn((String)errorMsg, (Object[])new Object[]{e});
            if (this.failSafe) {
                return "[" + errorMsg + "]";
            }
            throw new RuntimeException(errorMsg, e);
        }
    }

    private String ensureVariableSyntax(String expression) {
        Object processed = expression;
        if (((String)processed).matches("^([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\[.*\\].*")) {
            processed = ((String)processed).replaceFirst("^([a-zA-Z_][a-zA-Z0-9_]*)", "#$1");
        } else if (((String)processed).matches("^([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\..*")) {
            processed = ((String)processed).replaceFirst("^([a-zA-Z_][a-zA-Z0-9_]*)", "#$1");
        } else if (((String)processed).matches("^([a-zA-Z_][a-zA-Z0-9_]*)$")) {
            processed = "#" + (String)processed;
        }
        LogUtils.debug((String)"Expression syntax processing: {} -> {}", (Object[])new Object[]{expression, processed});
        return processed;
    }

    private EvaluationContext createSafeEvaluationContext(LogContext logContext) {
        EvaluationContext context = this.mainEvaluator.createEvaluationContext(logContext);
        try {
            Object args = context.lookupVariable("args");
            LogUtils.debug((String)"PureSpelProcessor\u4e2dargs\u53d8\u91cf\u72b6\u6001: {}, LogContext.args: {}", (Object[])new Object[]{args != null ? "non-null" : "null", logContext != null && logContext.getArgs() != null ? "non-null" : "null"});
            if (args == null && logContext != null && logContext.getArgs() != null) {
                LogUtils.warn((String)"args is null in EvaluationContext but LogContext.args is not null, resetting", (Object[])new Object[0]);
                ((StandardEvaluationContext)context).setVariable("args", (Object)logContext.getArgs());
            }
        }
        catch (Exception e) {
            LogUtils.debug((String)"Exception occurred while validating EvaluationContext state", (Object[])new Object[]{e});
        }
        return context;
    }

    @Override
    public boolean canHandle(String expression) {
        return ExpressionTypeDetector.detectType(expression) == ExpressionType.PURE_SPEL;
    }

    @Override
    public ExpressionType getSupportedType() {
        return ExpressionType.PURE_SPEL;
    }
}

