/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.kuma.boot.web.request.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class RequestLog
implements Serializable {
    private static final long serialVersionUID = -749360940290141180L;
    @JsonProperty(value="trace_id")
    private String traceId;
    @JsonProperty(value="application_name")
    private String applicationName;
    @JsonProperty(value="username")
    private String username;
    @JsonProperty(value="user_id")
    private String userId;
    @JsonProperty(value="client_id")
    private String clientId;
    @JsonProperty(value="description")
    private String description;
    @JsonProperty(value="ip")
    private String ip;
    @JsonProperty(value="operate_type")
    private Integer operateType;
    @JsonProperty(value="request_type")
    private Integer requestType;
    @JsonProperty(value="method_name")
    private String methodName;
    @JsonProperty(value="method")
    private String method;
    @JsonProperty(value="url")
    private String url;
    @JsonProperty(value="args")
    private String args;
    @JsonProperty(value="params")
    private String params;
    @JsonProperty(value="headers")
    private String headers;
    @JsonProperty(value="classpath")
    private String classpath;
    @JsonProperty(value="start_time")
    private Long startTime;
    @JsonProperty(value="end_time")
    private Long endTime;
    @JsonProperty(value="consuming_time")
    private Long consumingTime;
    @JsonProperty(value="ex_detail")
    private String exDetail;
    @JsonProperty(value="ex_desc")
    private String exDesc;
    @JsonProperty(value="tenant_id")
    private String tenantId;
    @JsonProperty(value="source")
    private String source;
    @JsonProperty(value="ctime")
    private String ctime;
    @JsonProperty(value="result")
    private String result;
    @JsonProperty(value="logday")
    private String logday;
    @JsonProperty(value="location")
    private String location;
    @JsonProperty(value="os")
    private String os;
    @JsonProperty(value="browser")
    private String browser;

    public String getTraceId() {
        return this.traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getOperateType() {
        return this.operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getRequestType() {
        return this.requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArgs() {
        return this.args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getParams() {
        return this.params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getHeaders() {
        return this.headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getClasspath() {
        return this.classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public Long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getConsumingTime() {
        return this.consumingTime;
    }

    public void setConsumingTime(Long consumingTime) {
        this.consumingTime = consumingTime;
    }

    public String getExDetail() {
        return this.exDetail;
    }

    public void setExDetail(String exDetail) {
        this.exDetail = exDetail;
    }

    public String getExDesc() {
        return this.exDesc;
    }

    public void setExDesc(String exDesc) {
        this.exDesc = exDesc;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCtime() {
        return this.ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLogday() {
        return this.logday;
    }

    public void setLogday(String logday) {
        this.logday = logday;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOs() {
        return this.os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBrowser() {
        return this.browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }
}

