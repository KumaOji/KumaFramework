/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.context;

import java.util.HashMap;
import java.util.Map;

public class LogContext {
    private String traceId;
    private long startTime;
    private long executionTime;
    private String className;
    private String methodName;
    private String methodSignature;
    private Object[] args;
    private Object result;
    private Throwable exception;
    private Map<String, Object> variables = new HashMap<String, Object>();

    public LogContext addVariable(String key, Object value) {
        this.variables.put(key, value);
        return this;
    }

    public Object getVariable(String key) {
        return this.variables.get(key);
    }

    public Object removeVariable(String key) {
        return this.variables.remove(key);
    }

    public void clearVariables() {
        this.variables.clear();
    }

    public String getTraceId() {
        return this.traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getExecutionTime() {
        return this.executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodSignature() {
        return this.methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getException() {
        return this.exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Map<String, Object> getVariables() {
        return this.variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}

