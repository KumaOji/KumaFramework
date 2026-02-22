/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 */
package com.kuma.boot.security.spring.event.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class RequestMapping {
    private String mappingId;
    private String mappingCode;
    private String requestMethod;
    private String serviceId;
    private String className;
    private String methodName;
    private String url;
    private String description;

    public String getMappingId() {
        return this.mappingId;
    }

    public void setMappingId(String mappingId) {
        this.mappingId = mappingId;
    }

    public String getMappingCode() {
        return this.mappingCode;
    }

    public void setMappingCode(String mappingCode) {
        this.mappingCode = mappingCode;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RequestMapping that = (RequestMapping)o;
        return Objects.equal((Object)this.mappingId, (Object)that.mappingId);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.mappingId});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("mappingId", (Object)this.mappingId).add("mappingCode", (Object)this.mappingCode).add("requestMethod", (Object)this.requestMethod).add("serviceId", (Object)this.serviceId).add("className", (Object)this.className).add("methodName", (Object)this.methodName).add("url", (Object)this.url).add("description", (Object)this.description).toString();
    }
}

