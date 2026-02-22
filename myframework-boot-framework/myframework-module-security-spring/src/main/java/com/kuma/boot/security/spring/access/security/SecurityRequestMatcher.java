/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 *  jakarta.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.http.HttpMethod
 *  org.springframework.security.web.util.matcher.RequestMatcher
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.Assert
 *  org.springframework.web.util.UrlPathHelper
 */
package com.kuma.boot.security.spring.access.security;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.util.UrlPathHelper;

public final class SecurityRequestMatcher
implements RequestMatcher,
Serializable {
    private static final Logger log = LoggerFactory.getLogger(SecurityRequestMatcher.class);
    private static final String MATCH_ALL = "/**";
    private Matcher matcher;
    private String pattern;
    private String httpMethod;
    private boolean caseSensitive;
    private boolean hasWildcard;
    private UrlPathHelper urlPathHelper;

    public SecurityRequestMatcher() {
    }

    public SecurityRequestMatcher(String pattern) {
        this(pattern, null);
    }

    public SecurityRequestMatcher(SecurityRequest request) {
        this(request.getPattern(), request.getHttpMethod());
    }

    public SecurityRequestMatcher(String pattern, String httpMethod) {
        this(pattern, httpMethod, true);
    }

    public SecurityRequestMatcher(String pattern, String httpMethod, boolean caseSensitive) {
        this(pattern, httpMethod, caseSensitive, null);
    }

    public SecurityRequestMatcher(String pattern, String httpMethod, boolean caseSensitive, UrlPathHelper urlPathHelper) {
        Assert.hasText((String)pattern, (String)"Pattern cannot be null or empty");
        this.caseSensitive = caseSensitive;
        this.hasWildcard = this.containSpecialCharacters(pattern);
        if (pattern.equals(MATCH_ALL) || "**".equals(pattern)) {
            pattern = MATCH_ALL;
            this.matcher = null;
        } else {
            this.matcher = pattern.endsWith(MATCH_ALL) && pattern.indexOf(63) == -1 && pattern.indexOf(123) == -1 && pattern.indexOf(125) == -1 && pattern.indexOf("*") == pattern.length() - 2 ? new SubPathMatcher(pattern.substring(0, pattern.length() - 3), caseSensitive) : new SpringAntMatcher(pattern, caseSensitive);
        }
        this.pattern = pattern;
        this.httpMethod = this.checkHttpMethod(httpMethod);
        this.urlPathHelper = urlPathHelper;
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

    public boolean matches(HttpServletRequest request) {
        if (StringUtils.isNotBlank((CharSequence)this.httpMethod) && StringUtils.isNotBlank((CharSequence)request.getMethod()) && !StringUtils.equalsIgnoreCase((CharSequence)this.httpMethod, (CharSequence)request.getMethod())) {
            return false;
        }
        if (this.pattern.equals(MATCH_ALL)) {
            return true;
        }
        String url = this.getRequestPath(request);
        return this.matcher.matches(url);
    }

    public boolean matches(SecurityRequest request) {
        if (StringUtils.isNotBlank((CharSequence)this.httpMethod) && StringUtils.isNotBlank((CharSequence)request.getHttpMethod()) && !StringUtils.equalsIgnoreCase((CharSequence)this.httpMethod, (CharSequence)request.getHttpMethod())) {
            return false;
        }
        if (this.pattern.equals(MATCH_ALL)) {
            return true;
        }
        if (StringUtils.equals((CharSequence)this.getPattern(), (CharSequence)request.getPattern())) {
            return true;
        }
        if (this.isHasWildcard() && !request.isHasWildcard()) {
            return this.matcher.matches(request.getPattern());
        }
        if (!this.isHasWildcard() && request.isHasWildcard()) {
            SpringAntMatcher matcher = new SpringAntMatcher(request.getPattern(), this.caseSensitive);
            return matcher.matches(this.getPattern());
        }
        return false;
    }

    private String getRequestPath(HttpServletRequest request) {
        if (this.urlPathHelper != null) {
            return this.urlPathHelper.getPathWithinApplication(request);
        }
        String url = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            url = StringUtils.isNotBlank((CharSequence)url) ? url + pathInfo : pathInfo;
        }
        return url;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.hasWildcard = this.containSpecialCharacters(this.pattern);
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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SecurityRequestMatcher that = (SecurityRequestMatcher)o;
        return this.caseSensitive == that.caseSensitive && Objects.equal((Object)this.pattern, (Object)that.pattern) && Objects.equal((Object)this.httpMethod, (Object)that.httpMethod);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.pattern, this.httpMethod, this.caseSensitive});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("pattern", (Object)this.pattern).add("httpMethod", (Object)this.httpMethod).toString();
    }

    private static interface Matcher
    extends Serializable {
        public boolean matches(String var1);
    }

    private static final class SubPathMatcher
    implements Matcher {
        private String subPath;
        private int length;
        private boolean caseSensitive;

        public SubPathMatcher() {
        }

        private SubPathMatcher(String subPath, boolean caseSensitive) {
            Assert.isTrue((!subPath.contains("*") ? 1 : 0) != 0, (String)"subpath cannot contain \"*\"");
            this.subPath = caseSensitive ? subPath : subPath.toLowerCase();
            this.length = subPath.length();
            this.caseSensitive = caseSensitive;
        }

        @Override
        public boolean matches(String path) {
            if (!this.caseSensitive) {
                path = path.toLowerCase();
            }
            return path.startsWith(this.subPath) && (path.length() == this.length || path.charAt(this.length) == '/');
        }
    }

    private static final class SpringAntMatcher
    implements Matcher {
        private AntPathMatcher antMatcher;
        private String pattern;

        public SpringAntMatcher() {
        }

        private SpringAntMatcher(String pattern, boolean caseSensitive) {
            this.pattern = pattern;
            this.antMatcher = SpringAntMatcher.createMatcher(caseSensitive);
        }

        @Override
        public boolean matches(String path) {
            return this.antMatcher.match(this.pattern, path);
        }

        private static AntPathMatcher createMatcher(boolean caseSensitive) {
            AntPathMatcher matcher = new AntPathMatcher();
            matcher.setTrimTokens(false);
            matcher.setCaseSensitive(caseSensitive);
            return matcher;
        }
    }
}

