//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.security.spring.access.security;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.io.Serializable;

public class SecurityAttribute implements Serializable {
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
        } else if (o != null && this.getClass() == o.getClass()) {
            SecurityAttribute that = (SecurityAttribute)o;
            return Objects.equal(this.attributeId, that.attributeId);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.attributeId});
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("attributeId", this.attributeId).add("attributeCode", this.attributeCode).add("attributeName", this.attributeName).add("authorities", this.webExpression).add("permissions", this.permissions).add("url", this.url).add("requestMethod", this.requestMethod).add("serviceId", this.serviceId).toString();
    }
}
