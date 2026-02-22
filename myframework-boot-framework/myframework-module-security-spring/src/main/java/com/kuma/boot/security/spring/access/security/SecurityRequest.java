/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.http.HttpMethod
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.access.security;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.io.Serializable;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

public final class SecurityRequest
implements Serializable {
    private String pattern;
    private String httpMethod;
    private boolean hasWildcard;

    public SecurityRequest() {
    }

    public SecurityRequest(String pattern) {
        this(pattern, null);
    }

    public SecurityRequest(String pattern, String httpMethod) {
        Assert.hasText((String)pattern, (String)"Pattern cannot be null or empty");
        this.pattern = pattern;
        this.hasWildcard = this.containSpecialCharacters(pattern);
        this.httpMethod = this.checkHttpMethod(httpMethod);
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public boolean isHasWildcard() {
        return this.hasWildcard;
    }

    private String checkHttpMethod(String method) {
        HttpMethod httpMethod;
        if (StringUtils.isNotBlank((CharSequence)method) && ObjectUtils.isNotEmpty((Object)(httpMethod = HttpMethod.valueOf((String)method)))) {
            return httpMethod.name();
        }
        return null;
    }

    private boolean containSpecialCharacters(String source) {
        if (StringUtils.isNotBlank((CharSequence)source)) {
            return StringUtils.containsAny((CharSequence)source, (CharSequence[])new String[]{"*", "?", "{"});
        }
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SecurityRequest that = (SecurityRequest)o;
        return Objects.equal((Object)this.pattern, (Object)that.pattern) && Objects.equal((Object)this.httpMethod, (Object)that.httpMethod);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.pattern, this.httpMethod});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("pattern", (Object)this.pattern).add("httpMethod", (Object)this.httpMethod).toString();
    }
}

