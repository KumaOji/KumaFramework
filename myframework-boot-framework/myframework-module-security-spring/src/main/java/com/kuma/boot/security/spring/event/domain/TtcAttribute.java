/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.security.spring.event.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
import java.util.Set;

@Schema(title="\u7cfb\u7edf\u5b89\u5168\u5c5e\u6027\u6570\u636e")
public class TtcAttribute {
    @Schema(title="\u5143\u6570\u636eID")
    private String attributeId;
    @Schema(title="\u9ed8\u8ba4\u6743\u9650\u4ee3\u7801")
    private String attributeCode;
    @Schema(name="\u8bf7\u6c42\u65b9\u6cd5")
    private String requestMethod;
    @Schema(name="\u670d\u52a1ID")
    private String serviceId;
    @Schema(name="\u63a5\u53e3\u6240\u5728\u7c7b")
    private String className;
    @Schema(name="\u63a5\u53e3\u5bf9\u5e94\u65b9\u6cd5")
    private String methodName;
    @Schema(name="\u8bf7\u6c42URL")
    private String url;
    @Schema(title="\u8868\u8fbe\u5f0f", description="Security\u8868\u8fbe\u5f0f\u5b57\u7b26\u4e32\uff0c\u901a\u8fc7\u8be5\u503c\u8bbe\u7f6e\u52a8\u6001\u6743\u9650")
    private String webExpression;
    @Schema(name="\u5c5e\u6027\u5bf9\u5e94\u6743\u9650", title="\u6839\u636e\u5c5e\u6027\u5173\u8054\u6743\u9650\u6570\u636e")
    private Set<TtcPermission> permissions = new HashSet<TtcPermission>();

    public String getAttributeId() {
        return this.attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeCode() {
        return this.attributeCode;
    }

    public void setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
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

    public String getWebExpression() {
        return this.webExpression;
    }

    public void setWebExpression(String webExpression) {
        this.webExpression = webExpression;
    }

    public Set<TtcPermission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<TtcPermission> permissions) {
        this.permissions = permissions;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TtcAttribute that = (TtcAttribute)o;
        return Objects.equal((Object)this.attributeId, (Object)that.attributeId);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.attributeId});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("attributeId", (Object)this.attributeId).add("attributeCode", (Object)this.attributeCode).add("requestMethod", (Object)this.requestMethod).add("serviceId", (Object)this.serviceId).add("className", (Object)this.className).add("methodName", (Object)this.methodName).add("url", (Object)this.url).add("webExpression", (Object)this.webExpression).toString();
    }
}

