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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class TemplateProcessor
implements ExpressionProcessor {
    private final ExpressionParser parser = new SpelExpressionParser();
    private final SpelExpressionEvaluator mainEvaluator;
    private final boolean failSafe;

    public TemplateProcessor(SpelExpressionEvaluator mainEvaluator, boolean failSafe) {
        this.mainEvaluator = mainEvaluator;
        this.failSafe = failSafe;
    }

    @Override
    public String processExpression(String expression, LogContext logContext) {
        if (expression == null || expression.trim().isEmpty()) {
            return "";
        }
        if (logContext == null) {
            LogUtils.warn((String)"TemplateProcessor received null LogContext, expression: {}", (Object[])new Object[]{expression});
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
            List<SpelPlaceholder> placeholders = this.extractSpelPlaceholders(expression);
            if (placeholders.isEmpty()) {
                return expression;
            }
            EvaluationContext evaluationContext = this.createSafeEvaluationContext(logContext);
            return this.replacePlaceholders(expression, placeholders, evaluationContext);
        }
        catch (Exception e) {
            String errorMsg = "\u6a21\u677f\u8868\u8fbe\u5f0f\u5904\u7406\u5931\u8d25: " + expression + ", LogContext: " + (logContext != null ? logContext.getMethodName() : "null");
            LogUtils.warn((String)errorMsg, (Object[])new Object[]{e});
            if (this.failSafe) {
                return "[" + errorMsg + "]";
            }
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public boolean canHandle(String expression) {
        return ExpressionTypeDetector.detectType(expression) == ExpressionType.TEMPLATE;
    }

    @Override
    public ExpressionType getSupportedType() {
        return ExpressionType.TEMPLATE;
    }

    private List<SpelPlaceholder> extractSpelPlaceholders(String template) {
        ArrayList<SpelPlaceholder> placeholders = new ArrayList<SpelPlaceholder>();
        Pattern pattern = ExpressionTypeDetector.getSpelPlaceholderPattern();
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String fullPlaceholder = matcher.group(0);
            String expression = matcher.group(1);
            int startIndex = matcher.start();
            int endIndex = matcher.end();
            placeholders.add(new SpelPlaceholder(fullPlaceholder, expression, startIndex, endIndex));
            LogUtils.debug((String)"Extracted SpEL placeholder: {} -> {}", (Object[])new Object[]{fullPlaceholder, expression});
        }
        return placeholders;
    }

    private String replacePlaceholders(String template, List<SpelPlaceholder> placeholders, EvaluationContext context) {
        StringBuilder result = new StringBuilder(template);
        for (int i = placeholders.size() - 1; i >= 0; --i) {
            SpelPlaceholder placeholder = placeholders.get(i);
            try {
                String processedExpression = this.ensureVariableSyntax(placeholder.expression);
                Expression expression = this.parser.parseExpression(processedExpression);
                Object value = expression.getValue(context);
                String valueStr = value != null ? value.toString() : "";
                result.replace(placeholder.startIndex, placeholder.endIndex, valueStr);
                LogUtils.debug((String)"Replaced placeholder: {} -> {}", (Object[])new Object[]{placeholder.fullPlaceholder, valueStr});
                continue;
            }
            catch (Exception e) {
                Object errorValue = this.failSafe ? "[\u8868\u8fbe\u5f0f\u9519\u8bef: " + placeholder.expression + "]" : placeholder.fullPlaceholder;
                result.replace(placeholder.startIndex, placeholder.endIndex, (String)errorValue);
                LogUtils.warn((String)"SpEL placeholder evaluation failed: {}", (Object[])new Object[]{placeholder.fullPlaceholder, e});
            }
        }
        return result.toString();
    }

    private EvaluationContext createSafeEvaluationContext(LogContext logContext) {
        EvaluationContext context = this.mainEvaluator.createEvaluationContext(logContext);
        try {
            Object args = context.lookupVariable("args");
            LogUtils.debug((String)"TemplateProcessor\u4e2dargs\u53d8\u91cf\u72b6\u6001: {}, LogContext.args: {}", (Object[])new Object[]{args != null ? "non-null" : "null", logContext != null && logContext.getArgs() != null ? "non-null" : "null"});
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

    private static class SpelPlaceholder {
        final String fullPlaceholder;
        final String expression;
        final int startIndex;
        final int endIndex;

        SpelPlaceholder(String fullPlaceholder, String expression, int startIndex, int endIndex) {
            this.fullPlaceholder = fullPlaceholder;
            this.expression = expression;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }
}

