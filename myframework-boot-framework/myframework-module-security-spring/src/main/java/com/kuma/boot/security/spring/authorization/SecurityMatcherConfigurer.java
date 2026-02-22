/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.CollectionUtils
 *  org.springframework.security.web.util.matcher.RequestMatcher
 */
package com.kuma.boot.security.spring.authorization;

import com.kuma.boot.security.spring.constants.WebResources;
import com.kuma.boot.security.spring.properties.OAuth2AuthorizationProperties;
import com.kuma.boot.security.spring.utils.ListUtils;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class SecurityMatcherConfigurer {
    private List<String> staticResources;
    private List<String> permitAllResources;
    private List<String> hasAuthenticatedResources;
    private final OAuth2AuthorizationProperties authorizationProperties;

    public SecurityMatcherConfigurer(OAuth2AuthorizationProperties authorizationProperties) {
        this.authorizationProperties = authorizationProperties;
        this.staticResources = new ArrayList<String>();
        this.permitAllResources = new ArrayList<String>();
        this.hasAuthenticatedResources = new ArrayList<String>();
    }

    public List<String> getStaticResourceList() {
        if (CollectionUtils.isEmpty(this.staticResources)) {
            this.staticResources = ListUtils.merge(this.authorizationProperties.getMatcher().getStaticResources(), WebResources.DEFAULT_IGNORED_STATIC_RESOURCES);
        }
        return this.staticResources;
    }

    public List<String> getPermitAllList() {
        if (CollectionUtils.isEmpty(this.permitAllResources)) {
            this.permitAllResources = ListUtils.merge(this.authorizationProperties.getMatcher().getPermitAll(), WebResources.DEFAULT_PERMIT_ALL_RESOURCES);
        }
        return this.permitAllResources;
    }

    public List<String> getHasAuthenticatedList() {
        if (CollectionUtils.isEmpty(this.hasAuthenticatedResources)) {
            this.hasAuthenticatedResources = ListUtils.merge(this.authorizationProperties.getMatcher().getHasAuthenticated(), WebResources.DEFAULT_HAS_AUTHENTICATED_RESOURCES);
        }
        return this.hasAuthenticatedResources;
    }

    public RequestMatcher[] getStaticResourceArray() {
        return SecurityUtils.toRequestMatchers(this.getStaticResourceList());
    }

    public RequestMatcher[] getPermitAllArray() {
        return SecurityUtils.toRequestMatchers(this.getStaticResourceList());
    }

    public OAuth2AuthorizationProperties getAuthorizationProperties() {
        return this.authorizationProperties;
    }
}

