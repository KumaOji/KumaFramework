/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 */
package com.kuma.boot.security.spring.access.security;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.io.Serializable;

public class SecurityAttribute
implements Serializable {
    private String attributeId;
    private String attributeCode;
    private String attributeName;
    private String webExpression;
    private String permissions;
    private String url;
    private String requestMethod;
    private String serviceId;

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

    public String getAttributeName() {
        return this.attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getWebExpression() {
        return this.webExpression;
    }

    public void setWebExpression(String webExpression) {
        this.webExpression = webExpression;
    }

    public String getPermissions() {
        return this.permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SecurityAttribute that = (SecurityAttribute)o;
        return Objects.equal((Object)this.attributeId, (Object)that.attributeId);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.attributeId});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("attributeId", (Object)this.attributeId).add("attributeCode", (Object)this.attributeCode).add("attributeName", (Object)this.attributeName).add("authorities", (Object)this.webExpression).add("permissions", (Object)this.permissions).add("url", (Object)this.url).add("requestMethod", (Object)this.requestMethod).add("serviceId", (Object)this.serviceId).toString();
    }
}

