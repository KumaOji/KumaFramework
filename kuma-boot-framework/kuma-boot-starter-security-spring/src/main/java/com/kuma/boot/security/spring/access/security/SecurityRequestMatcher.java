//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.security.spring.access.security;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.util.UrlPathHelper;

public final class SecurityRequestMatcher implements RequestMatcher, Serializable {
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
        this(pattern, (String)null);
    }

    public SecurityRequestMatcher(com.kuma.boot.security.spring.access.security.SecurityRequest request) {
        this(request.getPattern(), request.getHttpMethod());
    }

    public SecurityRequestMatcher(String pattern, String httpMethod) {
        this(pattern, httpMethod, true);
    }

    public SecurityRequestMatcher(String pattern, String httpMethod, boolean caseSensitive) {
        this(pattern, httpMethod, caseSensitive, (UrlPathHelper)null);
    }

    public SecurityRequestMatcher(String pattern, String httpMethod, boolean caseSensitive, UrlPathHelper urlPathHelper) {
        Assert.hasText(pattern, "Pattern cannot be null or empty");
        this.caseSensitive = caseSensitive;
        this.hasWildcard = this.containSpecialCharacters(pattern);
        if (!pattern.equals("/**") && !"**".equals(pattern)) {
            if (pattern.endsWith("/**") && pattern.indexOf(63) == -1 && pattern.indexOf(123) == -1 && pattern.indexOf(125) == -1 && pattern.indexOf("*") == pattern.length() - 2) {
                this.matcher = new SubPathMatcher(pattern.substring(0, pattern.length() - 3), caseSensitive);
            } else {
                this.matcher = new SpringAntMatcher(pattern, caseSensitive);
            }
        } else {
            pattern = "/**";
            this.matcher = null;
        }

        this.pattern = pattern;
        this.httpMethod = this.checkHttpMethod(httpMethod);
        this.urlPathHelper = urlPathHelper;
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

    public boolean matches(HttpServletRequest request) {
        if (StringUtils.isNotBlank(this.httpMethod) && StringUtils.isNotBlank(request.getMethod()) && !StringUtils.equalsIgnoreCase(this.httpMethod, request.getMethod())) {
            return false;
        } else if (this.pattern.equals("/**")) {
            return true;
        } else {
            String url = this.getRequestPath(request);
            return this.matcher.matches(url);
        }
    }

    public boolean matches(com.kuma.boot.security.spring.access.security.SecurityRequest request) {
        if (StringUtils.isNotBlank(this.httpMethod) && StringUtils.isNotBlank(request.getHttpMethod()) && !StringUtils.equalsIgnoreCase(this.httpMethod, request.getHttpMethod())) {
            return false;
        } else if (this.pattern.equals("/**")) {
            return true;
        } else if (StringUtils.equals(this.getPattern(), request.getPattern())) {
            return true;
        } else if (this.isHasWildcard() && !request.isHasWildcard()) {
            return this.matcher.matches(request.getPattern());
        } else if (!this.isHasWildcard() && request.isHasWildcard()) {
            Matcher matcher = new SpringAntMatcher(request.getPattern(), this.caseSensitive);
            return matcher.matches(this.getPattern());
        } else {
            return false;
        }
    }

    private String getRequestPath(HttpServletRequest request) {
        if (this.urlPathHelper != null) {
            return this.urlPathHelper.getPathWithinApplication(request);
        } else {
            String url = request.getServletPath();
            String pathInfo = request.getPathInfo();
            if (pathInfo != null) {
                url = StringUtils.isNotBlank(url) ? url + pathInfo : pathInfo;
            }

            return url;
        }
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
        } else if (o != null && this.getClass() == o.getClass()) {
            SecurityRequestMatcher that = (SecurityRequestMatcher)o;
            return this.caseSensitive == that.caseSensitive && Objects.equal(this.pattern, that.pattern) && Objects.equal(this.httpMethod, that.httpMethod);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.pattern, this.httpMethod, this.caseSensitive});
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("pattern", this.pattern).add("httpMethod", this.httpMethod).toString();
    }

    private static final class SpringAntMatcher implements Matcher {
        private AntPathMatcher antMatcher;
        private String pattern;

        public SpringAntMatcher() {
        }

        private SpringAntMatcher(String pattern, boolean caseSensitive) {
            this.pattern = pattern;
            this.antMatcher = createMatcher(caseSensitive);
        }

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

    private static final class SubPathMatcher implements Matcher {
        private String subPath;
        private int length;
        private boolean caseSensitive;

        public SubPathMatcher() {
        }

        private SubPathMatcher(String subPath, boolean caseSensitive) {
            Assert.isTrue(!subPath.contains("*"), "subpath cannot contain \"*\"");
            this.subPath = caseSensitive ? subPath : subPath.toLowerCase();
            this.length = subPath.length();
            this.caseSensitive = caseSensitive;
        }

        public boolean matches(String path) {
            if (!this.caseSensitive) {
                path = path.toLowerCase();
            }

            return path.startsWith(this.subPath) && (path.length() == this.length || path.charAt(this.length) == '/');
        }
    }

    private interface Matcher extends Serializable {
        boolean matches(String path);
    }
}
