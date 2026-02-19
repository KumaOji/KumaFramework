/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.web.exception.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.jspecify.annotations.Nullable;

public class ErrorEvent
implements Serializable {
    private @Nullable String id;
    private @Nullable String appName;
    private @Nullable String env;
    private @Nullable String commitId;
    private @Nullable String remoteHost;
    private @Nullable String requestType;
    private @Nullable String requestId;
    private @Nullable String requestMethod;
    private @Nullable String requestUrl;
    private @Nullable String requestIp;
    private @Nullable String stackTrace;
    private @Nullable String exceptionName;
    private @Nullable String message;
    private @Nullable String className;
    private @Nullable String fileName;
    private @Nullable String methodName;
    private @Nullable Integer lineNumber;
    private LocalDateTime createdAt;

    public @Nullable String getId() {
        return this.id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    public @Nullable String getAppName() {
        return this.appName;
    }

    public void setAppName(@Nullable String appName) {
        this.appName = appName;
    }

    public @Nullable String getEnv() {
        return this.env;
    }

    public void setEnv(@Nullable String env) {
        this.env = env;
    }

    public @Nullable String getCommitId() {
        return this.commitId;
    }

    public void setCommitId(@Nullable String commitId) {
        this.commitId = commitId;
    }

    public @Nullable String getRemoteHost() {
        return this.remoteHost;
    }

    public void setRemoteHost(@Nullable String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public @Nullable String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(@Nullable String requestId) {
        this.requestId = requestId;
    }

    public @Nullable String getRequestMethod() {
        return this.requestMethod;
    }

    public void setRequestMethod(@Nullable String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public @Nullable String getRequestUrl() {
        return this.requestUrl;
    }

    public void setRequestUrl(@Nullable String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public @Nullable String getRequestIp() {
        return this.requestIp;
    }

    public void setRequestIp(@Nullable String requestIp) {
        this.requestIp = requestIp;
    }

    public @Nullable String getStackTrace() {
        return this.stackTrace;
    }

    public void setStackTrace(@Nullable String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public @Nullable String getExceptionName() {
        return this.exceptionName;
    }

    public void setExceptionName(@Nullable String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public @Nullable String getMessage() {
        return this.message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    public @Nullable String getClassName() {
        return this.className;
    }

    public void setClassName(@Nullable String className) {
        this.className = className;
    }

    public @Nullable String getFileName() {
        return this.fileName;
    }

    public void setFileName(@Nullable String fileName) {
        this.fileName = fileName;
    }

    public @Nullable String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(@Nullable String methodName) {
        this.methodName = methodName;
    }

    public @Nullable Integer getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(@Nullable Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public @Nullable String getRequestType() {
        return this.requestType;
    }

    public void setRequestType(@Nullable String requestType) {
        this.requestType = requestType;
    }
}

