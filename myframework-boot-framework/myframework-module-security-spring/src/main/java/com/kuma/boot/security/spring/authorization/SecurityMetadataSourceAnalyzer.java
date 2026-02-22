/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.collections4.MapUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.authorization;

import com.kuma.boot.security.spring.access.security.SecurityAttribute;
import com.kuma.boot.security.spring.access.security.SecurityConfigAttribute;
import com.kuma.boot.security.spring.access.security.SecurityRequest;
import com.kuma.boot.security.spring.enums.Category;
import com.kuma.boot.security.spring.enums.PermissionExpression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityMetadataSourceAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(SecurityMetadataSourceAnalyzer.class);
    private final SecurityMetadataSourceStorage securityMetadataSourceStorage;
    private final SecurityMatcherConfigurer securityMatcherConfigurer;

    public SecurityMetadataSourceAnalyzer(SecurityMetadataSourceStorage securityMetadataSourceStorage, SecurityMatcherConfigurer securityMatcherConfigurer) {
        this.securityMetadataSourceStorage = securityMetadataSourceStorage;
        this.securityMatcherConfigurer = securityMatcherConfigurer;
    }

    private String hasAuthority(String authority) {
        return "hasAuthority('" + authority + "')";
    }

    private void appendToGroup(Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> container, Category category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> resources) {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> value = new LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>();
        if (container.containsKey((Object)category)) {
            value = container.get((Object)category);
        }
        value.putAll(resources);
        container.put(category, value);
    }

    private Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> groupSecurityMatchers(LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> securityMatchers) {
        LinkedHashMap<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> group = new LinkedHashMap<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>>();
        securityMatchers.forEach((key, value) -> {
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> resources = new LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>();
            resources.put((SecurityRequest)key, (List<SecurityConfigAttribute>)value);
            this.appendToGroup(group, Category.getCategory(key.getPattern()), resources);
        });
        log.info("Grouping security matcher by category.");
        return group;
    }

    private List<SecurityConfigAttribute> analysis(SecurityAttribute securityAttribute) {
        ArrayList<SecurityConfigAttribute> attributes = new ArrayList<SecurityConfigAttribute>();
        if (StringUtils.isNotBlank((CharSequence)securityAttribute.getPermissions())) {
            String[] permissions = org.springframework.util.StringUtils.commaDelimitedListToStringArray((String)securityAttribute.getPermissions());
            Arrays.stream(permissions).forEach(item -> attributes.add(new SecurityConfigAttribute(this.hasAuthority((String)item))));
        }
        if (StringUtils.isNotBlank((CharSequence)securityAttribute.getWebExpression())) {
            attributes.add(new SecurityConfigAttribute(securityAttribute.getWebExpression()));
        }
        return attributes;
    }

    private LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> convert(String url, String methods, List<SecurityConfigAttribute> configAttributes) {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> result = new LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>();
        if (StringUtils.isBlank((CharSequence)methods)) {
            result.put(new SecurityRequest(url), configAttributes);
        } else if (StringUtils.contains((CharSequence)methods, (CharSequence)",")) {
            String[] multiMethod;
            for (String method : multiMethod = StringUtils.split((String)methods, (String)",")) {
                result.put(new SecurityRequest(url, method), configAttributes);
            }
        } else {
            result.put(new SecurityRequest(url, methods), configAttributes);
        }
        return result;
    }

    private Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> groupingSecurityMetadata(List<SecurityAttribute> securityAttributes) {
        LinkedHashMap<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> group = new LinkedHashMap<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>>();
        securityAttributes.forEach(securityAttribute -> {
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> resources = this.convert(securityAttribute.getUrl(), securityAttribute.getRequestMethod(), this.analysis((SecurityAttribute)securityAttribute));
            this.appendToGroup(group, Category.getCategory(securityAttribute.getUrl()), resources);
        });
        log.info("Grouping security metadata by category.");
        return group;
    }

    private LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> getRequestMatchers() {
        List<String> permitAllMatcher = this.securityMatcherConfigurer.getPermitAllList();
        if (CollectionUtils.isNotEmpty(permitAllMatcher)) {
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> result = new LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>();
            permitAllMatcher.forEach(item -> result.put(new SecurityRequest((String)item), List.of(new SecurityConfigAttribute(PermissionExpression.PERMIT_ALL.getValue()))));
            return result;
        }
        return new LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>();
    }

    public void processRequestMatchers() {
        log.info("[3] Process local configured security metadata.");
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> requestMatchers = this.getRequestMatchers();
        if (MapUtils.isNotEmpty(requestMatchers)) {
            Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> grouping = this.groupSecurityMatchers(requestMatchers);
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> wildcards = grouping.get((Object)Category.WILDCARD);
            this.securityMetadataSourceStorage.addToStorage(wildcards, false);
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> fullPaths = grouping.get((Object)Category.FULL_PATH);
            this.securityMetadataSourceStorage.addToStorage(fullPaths, true);
        }
    }

    public void processSecurityAttribute(List<SecurityAttribute> securityAttributes) {
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> compatibles = this.securityMetadataSourceStorage.getCompatible();
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> matchers = new LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>(compatibles);
        Map<Category, LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>>> grouping = this.groupingSecurityMetadata(securityAttributes);
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> wildcards = grouping.get((Object)Category.WILDCARD);
        if (MapUtils.isNotEmpty(wildcards)) {
            matchers.putAll(wildcards);
            this.securityMetadataSourceStorage.addToStorage(wildcards, false);
        }
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> placeholders = grouping.get((Object)Category.PLACEHOLDER);
        log.info("Store placeholder type security attributes.");
        this.securityMetadataSourceStorage.addToStorage(matchers, placeholders, false);
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> fullPaths = grouping.get((Object)Category.FULL_PATH);
        log.info("Store full path type security attributes.");
        this.securityMetadataSourceStorage.addToStorage(matchers, fullPaths, true);
        log.info("[7] Security attributes process has FINISHED!");
    }
}

