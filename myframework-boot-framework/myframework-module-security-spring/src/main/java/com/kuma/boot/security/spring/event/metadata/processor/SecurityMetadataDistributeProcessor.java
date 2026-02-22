/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.dromara.hutool.core.collection.CollUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 *  org.springframework.transaction.annotation.Transactional
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.event.metadata.processor;

import com.google.common.collect.ImmutableList;
import com.kuma.boot.security.spring.access.security.SecurityAttribute;
import com.kuma.boot.security.spring.authorization.SecurityMetadataSourceAnalyzer;
import com.kuma.boot.security.spring.event.ApplicationStrategyEvent;
import com.kuma.boot.security.spring.event.domain.RequestMapping;
import com.kuma.boot.security.spring.event.domain.TtcAttribute;
import com.kuma.boot.security.spring.event.domain.TtcPermission;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.dromara.hutool.core.collection.CollUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class SecurityMetadataDistributeProcessor
implements ApplicationStrategyEvent<List<SecurityAttribute>> {
    private static final Logger log = LoggerFactory.getLogger(SecurityMetadataDistributeProcessor.class);
    private final SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer;

    public SecurityMetadataDistributeProcessor(SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer) {
        this.securityMetadataSourceAnalyzer = securityMetadataSourceAnalyzer;
    }

    @Override
    public void postLocalProcess(List<SecurityAttribute> data) {
        this.securityMetadataSourceAnalyzer.processSecurityAttribute(data);
    }

    @Override
    public void postRemoteProcess(String data, String originService, String destinationService) {
    }

    @Transactional(rollbackFor={Exception.class})
    public void postRequestMappings(List<RequestMapping> requestMappings) {
    }

    private void postGroupProcess(List<TtcAttribute> ttcAttributes) {
        if (CollUtil.isNotEmpty(ttcAttributes)) {
            Map<String, List<SecurityAttribute>> grouped = ttcAttributes.stream().map(this::convertSysAttributeToSecurityAttribute).collect(Collectors.groupingBy(SecurityAttribute::getServiceId));
            log.debug(" Grouping SysInterface and distribute to every server.");
            grouped.forEach(this::postProcess);
        }
    }

    public void distributeChangedSecurityAttribute(TtcAttribute ttcAttribute) {
        SecurityAttribute securityAttribute = this.convertSysAttributeToSecurityAttribute(ttcAttribute);
        this.postProcess(securityAttribute.getServiceId(), ImmutableList.of((Object)securityAttribute));
    }

    private SecurityAttribute convertSysAttributeToSecurityAttribute(TtcAttribute ttcAttribute) {
        SecurityAttribute securityAttribute = new SecurityAttribute();
        securityAttribute.setAttributeId(ttcAttribute.getAttributeId());
        securityAttribute.setAttributeCode(ttcAttribute.getAttributeCode());
        securityAttribute.setWebExpression(ttcAttribute.getWebExpression());
        securityAttribute.setPermissions(this.convertPermissionToCommaDelimitedString(ttcAttribute.getPermissions()));
        securityAttribute.setUrl(ttcAttribute.getUrl());
        securityAttribute.setRequestMethod(ttcAttribute.getRequestMethod());
        securityAttribute.setServiceId(ttcAttribute.getServiceId());
        return securityAttribute;
    }

    private String convertPermissionToCommaDelimitedString(Set<TtcPermission> sysAuthorities) {
        if (CollUtil.isNotEmpty(sysAuthorities)) {
            List<String> codes = sysAuthorities.stream().map(TtcPermission::getPermissionCode).toList();
            return StringUtils.collectionToCommaDelimitedString(codes);
        }
        return "";
    }
}

