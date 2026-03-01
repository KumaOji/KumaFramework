/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.web.exception.event;

import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 服务 异常 事件
 */
public class ErrorEvent implements Serializable {

    /**
     * 唯一 id，捕捉异常入库时初始化
     */
    @Nullable private String id;

    /**
     * 应用名
     */
    @Nullable private String appName;

    /**
     * 环境
     */
    @Nullable private String env;

    /**
     * git/svn commit id
     */
    @Nullable private String commitId;

    /**
     * 异常类型
     */
    // @Nullable
    // private ErrorType errorType;
    /**
     * 远程ip 主机名
     */
    @Nullable private String remoteHost;

    /**
     * 请求类型 api/feign
     */
    @Nullable private String requestType;

    /**
     * 请求id
     */
    @Nullable private String requestId;

    /**
     * 请求方法名
     */
    @Nullable private String requestMethod;

    /**
     * 请求url
     */
    @Nullable private String requestUrl;

    /**
     * 请求ip
     */
    @Nullable private String requestIp;

    /**
     * 堆栈信息
     */
    @Nullable private String stackTrace;

    /**
     * 异常名
     */
    @Nullable private String exceptionName;

    /**
     * 异常消息
     */
    @Nullable private String message;

    /**
     * 类名
     */
    @Nullable private String className;

    /**
     * 文件名
     */
    @Nullable private String fileName;

    /**
     * 方法名
     */
    @Nullable private String methodName;

    /**
     * 代码行数
     */
    @Nullable private Integer lineNumber;

    /**
     * 异常时间
     */
    private LocalDateTime createdAt;

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    @Nullable
    public String getAppName() {
        return appName;
    }

    public void setAppName(@Nullable String appName) {
        this.appName = appName;
    }

    @Nullable
    public String getEnv() {
        return env;
    }

    public void setEnv(@Nullable String env) {
        this.env = env;
    }

    @Nullable
    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(@Nullable String commitId) {
        this.commitId = commitId;
    }

    @Nullable
    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(@Nullable String remoteHost) {
        this.remoteHost = remoteHost;
    }

    @Nullable
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(@Nullable String requestId) {
        this.requestId = requestId;
    }

    @Nullable
    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(@Nullable String requestMethod) {
        this.requestMethod = requestMethod;
    }

    @Nullable
    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(@Nullable String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Nullable
    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(@Nullable String requestIp) {
        this.requestIp = requestIp;
    }

    @Nullable
    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(@Nullable String stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Nullable
    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(@Nullable String exceptionName) {
        this.exceptionName = exceptionName;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @Nullable
    public String getClassName() {
        return className;
    }

    public void setClassName(@Nullable String className) {
        this.className = className;
    }

    @Nullable
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@Nullable String fileName) {
        this.fileName = fileName;
    }

    @Nullable
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(@Nullable String methodName) {
        this.methodName = methodName;
    }

    @Nullable
    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(@Nullable Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(@Nullable String requestType) {
        this.requestType = requestType;
    }
}
