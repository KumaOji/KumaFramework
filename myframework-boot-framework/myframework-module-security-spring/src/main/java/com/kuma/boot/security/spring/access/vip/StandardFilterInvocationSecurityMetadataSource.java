/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.dromara.hutool.core.collection.CollUtil
 *  org.springframework.security.access.ConfigAttribute
 *  org.springframework.security.access.SecurityConfig
 *  org.springframework.security.web.FilterInvocation
 *  org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource
 */
package com.kuma.boot.security.spring.access.vip;

import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

public class StandardFilterInvocationSecurityMetadataSource
implements FilterInvocationSecurityMetadataSource {
    private final UrlSecurityPermsLoad urlPermsLoad;
    private final FilterInvocationSecurityMetadataSource superMetadataSource;

    public StandardFilterInvocationSecurityMetadataSource(UrlSecurityPermsLoad urlPermsLoad, FilterInvocationSecurityMetadataSource superMetadataSource) {
        this.urlPermsLoad = urlPermsLoad;
        this.superMetadataSource = superMetadataSource;
    }

    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        List list;
        String requestUrl = ((FilterInvocation)object).getRequestUrl();
        String matchRoles = this.urlPermsLoad.findMatchRoles(requestUrl);
        if (StringUtils.isBlank((CharSequence)matchRoles)) {
            matchRoles = "authc";
        }
        return CollUtil.isEmpty((Collection)(list = SecurityConfig.createList((String[])new String[]{matchRoles}))) ? this.superMetadataSource.getAttributes(object) : list;
    }

    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}

