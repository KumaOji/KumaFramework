//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.security.spring.access.security;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.io.Serializable;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

public final class SecurityRequest implements Serializable {
    private String pattern;
    private String httpMethod;
    private boolean hasWildcard;

    public SecurityRequest() {
    }

    public SecurityRequest(String pattern) {
        this(pattern, (String)null);
    }

    public SecurityRequest(String pattern, String httpMethod) {
        Assert.hasText(pattern, "Pattern cannot be null or empty");
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
        if (StringUtils.isNotBlank(method)) {
            HttpMethod httpMethod = HttpMethod.valueOf(method);
            if (ObjectUtils.isNotEmpty(httpMethod)) {
                return httpMethod.name();
            }
        }

        return null;
    }

    private boolean containSpecialCharacters(String source) {
        return StringUtils.isNotBlank(source) ? StringUtils.containsAny(source, new String[]{"*", "?", "{"}) : false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            SecurityRequest that = (SecurityRequest)o;
            return Objects.equal(this.pattern, that.pattern) && Objects.equal(this.httpMethod, that.httpMethod);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.pattern, this.httpMethod});
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("pattern", this.pattern).add("httpMethod", this.httpMethod).toString();
    }
}
