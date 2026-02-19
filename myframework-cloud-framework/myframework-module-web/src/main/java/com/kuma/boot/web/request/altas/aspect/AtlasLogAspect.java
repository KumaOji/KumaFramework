/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.web.request.altas.aspect;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.altas.annotation.ExceptionHandler;
import com.kuma.boot.web.request.altas.annotation.JsonPathCompare;
import com.kuma.boot.web.request.altas.annotation.Log;
import com.kuma.boot.web.request.altas.annotation.LogIgnore;
import com.kuma.boot.web.request.altas.annotation.LogLevel;
import com.kuma.boot.web.request.altas.annotation.Logs;
import com.kuma.boot.web.request.altas.context.LogContext;
import com.kuma.boot.web.request.altas.context.TraceIdHolder;
import com.kuma.boot.web.request.altas.expression.SpelExpressionEvaluator;
import com.kuma.boot.web.request.altas.processor.JsonPathCompareProcessor;
import com.kuma.boot.web.request.altas.serializer.ArgumentFormatterManager;
import com.kuma.boot.web.request.altas.serializer.ArgumentSerializer;
import com.kuma.boot.web.request.altas.serializer.DefaultFormatterContext;
import com.kuma.boot.web.request.altas.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AtlasLogAspect {
    private final SpelExpressionEvaluator spelExpressionEvaluator;
    private final ArgumentSerializer argumentSerializer;
    private final JsonPathCompareProcessor jsonPathCompareProcessor;
    private final ArgumentFormatterManager argumentFormatterManager;

    public AtlasLogAspect(SpelExpressionEvaluator spelExpressionEvaluator, ArgumentSerializer argumentSerializer, JsonPathCompareProcessor jsonPathCompareProcessor, ArgumentFormatterManager argumentFormatterManager) {
        this.spelExpressionEvaluator = spelExpressionEvaluator;
        this.argumentSerializer = argumentSerializer;
        this.jsonPathCompareProcessor = jsonPathCompareProcessor;
        this.argumentFormatterManager = argumentFormatterManager;
    }

    @Around(value="@annotation(com.kuma.boot.web.request.altas.annotation.Log) || @annotation(com.kuma.boot.web.request.altas.annotation.Logs) || @annotation(com.kuma.boot.web.request.altas.annotation.JsonPathCompare)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        List<Log> logAnnotations;
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object[] beforeArgs = null;
        if (method.isAnnotationPresent(JsonPathCompare.class)) {
            beforeArgs = this.deepCopyArgs(args);
        }
        if ((logAnnotations = this.getAllLogAnnotations(method)).isEmpty() && !method.isAnnotationPresent(JsonPathCompare.class)) {
            return joinPoint.proceed();
        }
        if (method.isAnnotationPresent(LogIgnore.class)) {
            return joinPoint.proceed();
        }
        long startTime = System.currentTimeMillis();
        String traceId = TraceIdHolder.getTraceIdIfPresent();
        if (traceId == null) {
            LogUtils.warn((String)"TraceId is null in AOP, Filter may not be working properly, method: {}", (Object[])new Object[]{method.getName()});
            traceId = "MISSING-TRACE-ID";
        }
        Object result = null;
        Throwable exception = null;
        try {
            for (Log logAnnotation : logAnnotations) {
                this.recordEnterLog(logAnnotation, method, args, traceId);
            }
            result = joinPoint.proceed();
            if (this.jsonPathCompareProcessor != null && method.isAnnotationPresent(JsonPathCompare.class)) {
                this.jsonPathCompareProcessor.processJsonPathCompare(method, args, result, beforeArgs);
            }
            Object object = result;
            return object;
        }
        catch (Throwable e) {
            exception = e;
            throw e;
        }
        finally {
            long executionTime = System.currentTimeMillis() - startTime;
            for (Log logAnnotation : logAnnotations) {
                if (exception != null) {
                    this.recordExceptionLog(logAnnotation, method, args, exception, executionTime, traceId);
                    continue;
                }
                this.recordExitLog(logAnnotation, method, args, result, executionTime, traceId);
            }
        }
    }

    private void recordEnterLog(Log logAnnotation, Method method, Object[] args, String traceId) {
        if (!this.shouldLog(logAnnotation, method, args, null, null)) {
            return;
        }
        if (logAnnotation.enterMessage().isEmpty()) {
            return;
        }
        LogContext logContext = this.createLogContext(method, args, null, null, 0L, traceId);
        try {
            String message = this.spelExpressionEvaluator.evaluateExpression(logAnnotation.enterMessage(), logContext);
            Logger logger = this.getLogger(method);
            this.logWithLevel(logger, logAnnotation.level(), message, this.buildLogDetails(logAnnotation, logContext, method));
        }
        catch (Exception e) {
            LogUtils.warn((String)"Failed to record enter log: {}", (Object[])new Object[]{method.getName(), e});
        }
    }

    private void recordExitLog(Log logAnnotation, Method method, Object[] args, Object result, long executionTime, String traceId) {
        LogContext logContext = this.createLogContext(method, args, result, null, executionTime, traceId);
        if (!this.shouldLog(logAnnotation, method, args, result, null)) {
            return;
        }
        try {
            String message = this.buildLogMessage(logAnnotation, logContext, false);
            Logger logger = this.getLogger(method);
            this.logWithLevel(logger, logAnnotation.level(), message, this.buildLogDetails(logAnnotation, logContext, method));
        }
        catch (Exception e) {
            LogUtils.warn((String)"Failed to record exit log: {}", (Object[])new Object[]{method.getName(), e});
        }
    }

    private void recordExceptionLog(Log logAnnotation, Method method, Object[] args, Throwable exception, long executionTime, String traceId) {
        if (!logAnnotation.logException()) {
            return;
        }
        LogContext logContext = this.createLogContext(method, args, null, exception, executionTime, traceId);
        try {
            boolean logStackTrace;
            LogLevel logLevel;
            String message;
            ExceptionHandler exceptionHandler = this.findExceptionHandler(exception, logAnnotation.exceptionHandlers());
            if (exceptionHandler != null) {
                message = exceptionHandler.message().isEmpty() ? this.buildLogMessage(logAnnotation, logContext, true) : this.spelExpressionEvaluator.evaluateExpression(exceptionHandler.message(), logContext);
                logLevel = exceptionHandler.level();
                logStackTrace = exceptionHandler.logStackTrace();
            } else {
                message = logAnnotation.exceptionMessage().isEmpty() ? this.buildLogMessage(logAnnotation, logContext, true) : this.spelExpressionEvaluator.evaluateExpression(logAnnotation.exceptionMessage(), logContext);
                logLevel = LogLevel.ERROR;
                logStackTrace = true;
            }
            Logger logger = this.getLogger(method);
            String logDetails = this.buildLogDetails(logAnnotation, logContext, method);
            if (logStackTrace) {
                this.logWithLevel(logger, logLevel, message, logDetails, exception);
            } else {
                this.logWithLevel(logger, logLevel, message, logDetails);
            }
        }
        catch (Exception e) {
            LogUtils.warn((String)"Failed to record exception log: {}", (Object[])new Object[]{method.getName(), e});
        }
    }

    private boolean shouldLog(Log logAnnotation, Method method, Object[] args, Object result, Throwable exception) {
        if (!logAnnotation.condition().isEmpty()) {
            LogContext logContext = this.createLogContext(method, args, result, exception, 0L, TraceIdHolder.getTraceId());
            try {
                return this.spelExpressionEvaluator.evaluateCondition(logAnnotation.condition(), logContext);
            }
            catch (Exception e) {
                LogUtils.warn((String)"Condition expression evaluation failed, logging by default: {}", (Object[])new Object[]{logAnnotation.condition(), e});
                return true;
            }
        }
        return true;
    }

    private String buildLogMessage(Log logAnnotation, LogContext logContext, boolean isException) {
        if (logContext == null) {
            LogUtils.warn((String)"buildLogMessage received null LogContext", (Object[])new Object[0]);
            return "[LogContext\u4e3anull]";
        }
        if (logContext.getArgs() == null) {
            LogUtils.warn((String)"LogContext.args is null in buildLogMessage, method: {}", (Object[])new Object[]{logContext.getMethodName()});
        }
        if (isException && !logAnnotation.exceptionMessage().isEmpty()) {
            return this.spelExpressionEvaluator.evaluateExpression(logAnnotation.exceptionMessage(), logContext);
        }
        if (!isException && !logAnnotation.exitMessage().isEmpty()) {
            return this.spelExpressionEvaluator.evaluateExpression(logAnnotation.exitMessage(), logContext);
        }
        if (!logAnnotation.value().isEmpty()) {
            return this.spelExpressionEvaluator.evaluateExpression(logAnnotation.value(), logContext);
        }
        String action = isException ? "\u6267\u884c\u5f02\u5e38" : "\u6267\u884c\u5b8c\u6210";
        return String.format("%s: %s", action, logContext.getMethodName());
    }

    private String buildLogDetails(Log logAnnotation, LogContext logContext, Method method) {
        StringBuilder details = new StringBuilder();
        if (logContext.getTraceId() != null) {
            details.append("TraceId: ").append(logContext.getTraceId()).append(" | ");
        }
        if (logAnnotation.tags().length > 0) {
            details.append("Tags: ").append(Arrays.toString(logAnnotation.tags())).append(" | ");
        }
        if (logAnnotation.logArgs() && logContext.getArgs() != null) {
            try {
                String argsStr = this.serializeArgs(logContext.getArgs(), logAnnotation, this.getMethodParameters(logContext.getMethodSignature()), method);
                details.append("Args: ").append(argsStr).append(" | ");
            }
            catch (Exception e) {
                details.append("Args: [\u5e8f\u5217\u5316\u5931\u8d25] | ");
            }
        }
        if (logAnnotation.logResult() && logContext.getResult() != null) {
            try {
                String resultStr = this.serializeResult(logContext.getResult(), logAnnotation, method);
                details.append("Result: ").append(resultStr).append(" | ");
            }
            catch (Exception e) {
                details.append("Result: [\u5e8f\u5217\u5316\u5931\u8d25] | ");
            }
        }
        if (logAnnotation.logExecutionTime()) {
            details.append("ExecutionTime: ").append(logContext.getExecutionTime()).append("ms | ");
        }
        if (logContext.getException() != null) {
            details.append("Exception: ").append(logContext.getException().getClass().getSimpleName()).append(": ").append(logContext.getException().getMessage()).append(" | ");
        }
        if (details.length() > 3) {
            details.setLength(details.length() - 3);
        }
        return details.toString();
    }

    private String serializeArgs(Object[] args, Log logAnnotation, Parameter[] parameters, Method method) {
        String formatterName = logAnnotation.argumentFormatter();
        if (formatterName != null && !formatterName.trim().isEmpty()) {
            String methodName = method != null ? method.getName() : "unknown";
            String className = method != null ? method.getDeclaringClass().getSimpleName() : "unknown";
            DefaultFormatterContext context = new DefaultFormatterContext(methodName, className, logAnnotation.maxArgLength());
            LogUtils.debug((String)"Using custom formatter '{}' for method {}.{}", (Object[])new Object[]{formatterName, className, methodName});
            return this.argumentFormatterManager.formatArguments(formatterName, args, context);
        }
        return this.argumentSerializer.serializeArgs(args, logAnnotation);
    }

    private String serializeResult(Object result, Log logAnnotation, Method method) {
        String formatterName = logAnnotation.resultFormatter();
        if (formatterName != null && !formatterName.trim().isEmpty()) {
            DefaultFormatterContext context = new DefaultFormatterContext(method.getName(), method.getDeclaringClass().getSimpleName(), logAnnotation.maxResultLength());
            return this.argumentFormatterManager.formatResult(formatterName, result, context);
        }
        return this.argumentSerializer.serializeResult(result, logAnnotation);
    }

    private Parameter[] getMethodParameters(String methodSignature) {
        return new Parameter[0];
    }

    private LogContext createLogContext(Method method, Object[] args, Object result, Throwable exception, long executionTime, String traceId) {
        Object[] safeArgs = args != null ? Arrays.copyOf(args, args.length) : new Object[]{};
        LogContext context = new LogContext();
        context.setTraceId(traceId != null ? traceId : "");
        context.setClassName(method.getDeclaringClass().getSimpleName());
        context.setMethodName(method.getName());
        context.setMethodSignature(ReflectionUtils.formatMethodSignature(method));
        context.setArgs(safeArgs);
        context.setResult(result);
        context.setException(exception);
        context.setExecutionTime(executionTime);
        LogUtils.debug((String)"\u521b\u5efaLogContext: method={}, args={}, argsLength={}", (Object[])new Object[]{method.getName(), safeArgs != null ? "non-null" : "null", safeArgs != null ? safeArgs.length : 0});
        return context;
    }

    private List<Log> getAllLogAnnotations(Method method) {
        Logs classMultiLogs;
        Class<?> clazz;
        Log classSingleLog;
        Logs multiLogs;
        ArrayList<Log> annotations = new ArrayList<Log>();
        Log singleLog = (Log)AnnotationUtils.findAnnotation((Method)method, Log.class);
        if (singleLog != null) {
            annotations.add(singleLog);
        }
        if ((multiLogs = (Logs)AnnotationUtils.findAnnotation((Method)method, Logs.class)) != null) {
            annotations.addAll(Arrays.asList(multiLogs.value()));
        }
        if ((classSingleLog = (Log)AnnotationUtils.findAnnotation(clazz = method.getDeclaringClass(), Log.class)) != null) {
            annotations.add(classSingleLog);
        }
        if ((classMultiLogs = (Logs)AnnotationUtils.findAnnotation(clazz, Logs.class)) != null) {
            annotations.addAll(Arrays.asList(classMultiLogs.value()));
        }
        return annotations;
    }

    private ExceptionHandler findExceptionHandler(Throwable exception, ExceptionHandler[] handlers) {
        if (handlers == null || handlers.length == 0) {
            return null;
        }
        Class<?> exceptionClass = exception.getClass();
        for (ExceptionHandler handler : handlers) {
            if (!handler.exception().equals(exceptionClass)) continue;
            return handler;
        }
        for (ExceptionHandler handler : handlers) {
            if (!handler.exception().isAssignableFrom(exceptionClass)) continue;
            return handler;
        }
        return null;
    }

    private Logger getLogger(Method method) {
        return LoggerFactory.getLogger(method.getDeclaringClass());
    }

    private void logWithLevel(Logger logger, LogLevel level, String message, String details) {
        String fullMessage = String.format("%s | %s", details, message);
        switch (level) {
            case TRACE: {
                logger.trace(fullMessage);
                break;
            }
            case DEBUG: {
                logger.debug(fullMessage);
                break;
            }
            case INFO: {
                logger.info(fullMessage);
                break;
            }
            case WARN: {
                logger.warn(fullMessage);
                break;
            }
            case ERROR: {
                logger.error(fullMessage);
            }
        }
    }

    private void logWithLevel(Logger logger, LogLevel level, String message, String details, Throwable exception) {
        String fullMessage = String.format("%s | %s", details, message);
        switch (level) {
            case TRACE: {
                logger.trace(fullMessage, exception);
                break;
            }
            case DEBUG: {
                logger.debug(fullMessage, exception);
                break;
            }
            case INFO: {
                logger.info(fullMessage, exception);
                break;
            }
            case WARN: {
                logger.warn(fullMessage, exception);
                break;
            }
            case ERROR: {
                logger.error(fullMessage, exception);
            }
        }
    }

    private Object[] deepCopyArgs(Object[] args) {
        if (args == null) {
            return null;
        }
        Object[] copy = new Object[args.length];
        for (int i = 0; i < args.length; ++i) {
            copy[i] = args[i];
        }
        return copy;
    }
}

