/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.comparator.CompareUtil
 *  cn.hutool.core.text.CharSequenceUtil
 *  cn.hutool.core.util.NumberUtil
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.exception.ApiVersionDeprecatedException
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.web.servlet.mvc.condition.RequestCondition
 */
package com.kuma.boot.web.support.version;

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.exception.ApiVersionDeprecatedException;
import com.kuma.boot.web.annotation.ApiVersion;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

public class ApiVersionRequestCondition
implements RequestCondition<ApiVersionRequestCondition> {
    private ApiVersion apiVersion;
    private ApiVersionProperties apiVersionProperties;
    private Integer versionPlaceholderIndex;

    public ApiVersionRequestCondition(ApiVersion apiVersion, ApiVersionProperties apiVersionProperties, Integer versionPlaceholderIndex) {
        this.apiVersion = apiVersion;
        this.apiVersionProperties = apiVersionProperties;
        this.versionPlaceholderIndex = versionPlaceholderIndex;
    }

    public ApiVersionRequestCondition combine(ApiVersionRequestCondition apiVersionRequestCondition) {
        return new ApiVersionRequestCondition(apiVersionRequestCondition.getApiVersion(), apiVersionRequestCondition.getApiVersionProperties(), apiVersionRequestCondition.getVersionPlaceholderIndex());
    }

    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        double apiVersionValue;
        String requestURI = request.getRequestURI();
        String[] versionPaths = CharSequenceUtil.splitToArray((CharSequence)requestURI, (CharSequence)"/");
        double pathVersion = Double.parseDouble(versionPaths[this.versionPlaceholderIndex].substring(1));
        if (pathVersion >= (apiVersionValue = this.getApiVersion().value())) {
            double minimumVersion = this.apiVersionProperties.getMinimumVersion();
            if ((this.getApiVersion().deprecated() || minimumVersion > pathVersion) && NumberUtil.equals((double)pathVersion, (double)apiVersionValue)) {
                throw new ApiVersionDeprecatedException(StrUtil.format((CharSequence)"\u5ba2\u6237\u7aef\u8c03\u7528\u5f03\u7528\u7248\u672cAPI\u63a5\u53e3\uff0crequestURI\uff1a{}", (Object[])new Object[]{requestURI}));
            }
            if (this.getApiVersion().deprecated()) {
                return null;
            }
            return this;
        }
        return null;
    }

    public int compareTo(ApiVersionRequestCondition apiVersionRequestCondition, HttpServletRequest request) {
        return CompareUtil.compare((Comparable)Double.valueOf(apiVersionRequestCondition.getApiVersion().value()), (Comparable)Double.valueOf(this.getApiVersion().value()));
    }

    public ApiVersion getApiVersion() {
        return this.apiVersion;
    }

    public void setApiVersion(ApiVersion apiVersion) {
        this.apiVersion = apiVersion;
    }

    public ApiVersionProperties getApiVersionProperties() {
        return this.apiVersionProperties;
    }

    public void setApiVersionProperties(ApiVersionProperties apiVersionProperties) {
        this.apiVersionProperties = apiVersionProperties;
    }

    public Integer getVersionPlaceholderIndex() {
        return this.versionPlaceholderIndex;
    }

    public void setVersionPlaceholderIndex(Integer versionPlaceholderIndex) {
        this.versionPlaceholderIndex = versionPlaceholderIndex;
    }
}

