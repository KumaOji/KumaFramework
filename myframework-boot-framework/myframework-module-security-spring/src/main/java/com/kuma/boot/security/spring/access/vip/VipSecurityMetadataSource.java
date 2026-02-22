/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.access.ConfigAttribute
 *  org.springframework.security.web.FilterInvocation
 *  org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource
 *  org.springframework.util.AntPathMatcher
 */
package com.kuma.boot.security.spring.access.vip;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;

public class VipSecurityMetadataSource
implements FilterInvocationSecurityMetadataSource {
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private Set<PermRoleEntity> permRoleEntitySet;
    private final FilterInvocationSecurityMetadataSource superMetadataSource;
    private final VipSecurityOauthService vipSecurityOauthService;

    public VipSecurityMetadataSource(FilterInvocationSecurityMetadataSource superMetadataSource, VipSecurityOauthService vipSecurityOauthService) {
        this.superMetadataSource = superMetadataSource;
        this.vipSecurityOauthService = vipSecurityOauthService;
    }

    private void loadPerms() {
        this.permRoleEntitySet = this.vipSecurityOauthService.loadPerms();
    }

    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        this.loadPerms();
        FilterInvocation fi = (FilterInvocation)object;
        String accessUri = fi.getRequestUrl();
        for (PermRoleEntity permRoleEntity : this.permRoleEntitySet) {
            if (!ANT_PATH_MATCHER.match(permRoleEntity.getAccessUri(), accessUri)) continue;
            return permRoleEntity.getConfigAttributeList();
        }
        return this.superMetadataSource.getAttributes(object);
    }

    public Collection<ConfigAttribute> getAllConfigAttributes() {
        this.loadPerms();
        HashSet<ConfigAttribute> attributeSet = new HashSet<ConfigAttribute>();
        this.permRoleEntitySet.stream().map(PermRoleEntity::getConfigAttributeList).forEach(attributeSet::addAll);
        return attributeSet;
    }

    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}

