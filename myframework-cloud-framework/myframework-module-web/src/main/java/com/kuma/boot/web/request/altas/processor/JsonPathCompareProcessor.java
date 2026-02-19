/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.web.request.altas.processor;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.altas.annotation.JsonPathCompare;
import com.kuma.boot.web.request.altas.annotation.LogLevel;
import com.kuma.boot.web.request.altas.comparator.JsonPathValueExtractor;
import java.lang.invoke.CallSite;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonPathCompareProcessor {
    private final JsonPathValueExtractor valueExtractor;

    public JsonPathCompareProcessor(JsonPathValueExtractor valueExtractor) {
        this.valueExtractor = valueExtractor;
    }

    public void processJsonPathCompare(Method method, Object[] args, Object result, Object[] beforeArgs) {
        JsonPathCompare annotation = method.getAnnotation(JsonPathCompare.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(JsonPathCompare.class);
        }
        if (annotation == null || annotation.value().length == 0) {
            return;
        }
        try {
            this.processComparison(annotation, method, args, result, beforeArgs);
        }
        catch (Exception e) {
            this.handleFailure(annotation, method, e);
        }
    }

    private void processComparison(JsonPathCompare annotation, Method method, Object[] args, Object result, Object[] beforeArgs) {
        List<String> jsonPaths = Arrays.asList(annotation.value());
        Logger methodLogger = LoggerFactory.getLogger(method.getDeclaringClass());
        switch (annotation.mode()) {
            case ARGS_VS_RESULT: {
                this.processArgsVsResult(annotation, method, args, result, jsonPaths, methodLogger);
                break;
            }
            case BEFORE_VS_AFTER: {
                this.processBeforeVsAfter(annotation, method, beforeArgs, args, jsonPaths, methodLogger);
                break;
            }
            case EXTRACT_ONLY: {
                this.processExtractOnly(annotation, method, args, result, jsonPaths, methodLogger);
            }
        }
    }

    private void processArgsVsResult(JsonPathCompare annotation, Method method, Object[] args, Object result, List<String> jsonPaths, Logger logger) {
        Object argsObject = this.createArgsObject(args);
        List<JsonPathValueExtractor.ValueComparisonResult> results = this.valueExtractor.compareValues(argsObject, result, jsonPaths);
        if (annotation.logComparison()) {
            this.logComparisonResults(annotation, method, results, logger);
        }
    }

    private void processBeforeVsAfter(JsonPathCompare annotation, Method method, Object[] beforeArgs, Object[] afterArgs, List<String> jsonPaths, Logger logger) {
        Object beforeObject = this.createArgsObject(beforeArgs);
        Object afterObject = this.createArgsObject(afterArgs);
        List<JsonPathValueExtractor.ValueComparisonResult> results = this.valueExtractor.compareValues(beforeObject, afterObject, jsonPaths);
        if (annotation.logComparison()) {
            this.logComparisonResults(annotation, method, results, logger);
        }
    }

    private void processExtractOnly(JsonPathCompare annotation, Method method, Object[] args, Object result, List<String> jsonPaths, Logger logger) {
        Object argsObject = this.createArgsObject(args);
        Map<String, Object> argsValues = this.valueExtractor.extractValues(argsObject, jsonPaths);
        Map<String, Object> resultValues = this.valueExtractor.extractValues(result, jsonPaths);
        if (annotation.logComparison()) {
            this.logExtractedValues(annotation, method, argsValues, resultValues, logger);
        }
    }

    private Object createArgsObject(Object[] args) {
        if (args == null || args.length == 0) {
            return new Object();
        }
        if (args.length == 1) {
            return args[0];
        }
        HashMap<CallSite, Object> argsMap = new HashMap<CallSite, Object>();
        for (int i = 0; i < args.length; ++i) {
            argsMap.put((CallSite)((Object)("arg" + i)), args[i]);
        }
        return argsMap;
    }

    private void logComparisonResults(JsonPathCompare annotation, Method method, List<JsonPathValueExtractor.ValueComparisonResult> results, Logger logger) {
        for (JsonPathValueExtractor.ValueComparisonResult result : results) {
            String message = this.formatMessage(annotation.messageTemplate(), method, result);
            LogLevel logLevel = annotation.logLevel();
            if (!result.isEqual() && annotation.logDifferences()) {
                this.logWithLevel(logger, logLevel, message);
                continue;
            }
            if (!result.isEqual() && annotation.logDifferences()) continue;
            this.logWithLevel(logger, logLevel, message);
        }
    }

    private void logExtractedValues(JsonPathCompare annotation, Method method, Map<String, Object> argsValues, Map<String, Object> resultValues, Logger logger) {
        for (String path : argsValues.keySet()) {
            Object argValue = argsValues.get(path);
            Object resultValue = resultValues.get(path);
            String message = String.format("JsonPath\u63d0\u53d6: %s | \u65b9\u6cd5: %s | \u53c2\u6570\u503c: %s | \u8fd4\u56de\u503c: %s", path, method.getName(), argValue, resultValue);
            this.logWithLevel(logger, annotation.logLevel(), message);
        }
    }

    private String formatMessage(String template, Method method, JsonPathValueExtractor.ValueComparisonResult result) {
        return template.replace("{path}", result.getJsonPath()).replace("{value1}", String.valueOf(result.getValue1())).replace("{value2}", String.valueOf(result.getValue2())).replace("{equal}", String.valueOf(result.isEqual())).replace("{method}", method.getName());
    }

    private void logWithLevel(Logger logger, LogLevel level, String message) {
        switch (level) {
            case TRACE: {
                if (!logger.isTraceEnabled()) break;
                logger.trace(message);
                break;
            }
            case DEBUG: {
                if (!logger.isDebugEnabled()) break;
                logger.debug(message);
                break;
            }
            case INFO: {
                if (!logger.isInfoEnabled()) break;
                logger.info(message);
                break;
            }
            case WARN: {
                if (!logger.isWarnEnabled()) break;
                logger.warn(message);
                break;
            }
            case ERROR: {
                if (!logger.isErrorEnabled()) break;
                logger.error(message);
            }
        }
    }

    private void handleFailure(JsonPathCompare annotation, Method method, Exception e) {
        String errorMessage = String.format("JsonPath\u6bd4\u8f83\u5931\u8d25: \u65b9\u6cd5=%s, \u9519\u8bef=%s", method.getName(), e.getMessage());
        switch (annotation.onFailure()) {
            case LOG_WARNING: {
                LogUtils.warn((String)errorMessage, (Object[])new Object[]{e});
                break;
            }
            case LOG_ERROR: {
                LogUtils.error((String)errorMessage, (Object[])new Object[]{e});
                break;
            }
            case IGNORE: {
                break;
            }
            case THROW_EXCEPTION: {
                throw new RuntimeException("JsonPath comparison failed: " + errorMessage, e);
            }
        }
    }

    public boolean isEnabled() {
        return this.valueExtractor != null && JsonPathValueExtractor.isJsonPathAvailable();
    }
}

