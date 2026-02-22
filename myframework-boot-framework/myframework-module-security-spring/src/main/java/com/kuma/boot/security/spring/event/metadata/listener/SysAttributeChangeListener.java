/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.security.spring.event.metadata.listener;

import com.kuma.boot.security.spring.event.domain.TtcAttribute;
import com.kuma.boot.security.spring.event.metadata.event.SysAttributeChangeEvent;
import com.kuma.boot.security.spring.event.metadata.processor.SecurityMetadataDistributeProcessor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SysAttributeChangeListener
implements ApplicationListener<SysAttributeChangeEvent> {
    private static final Logger log = LoggerFactory.getLogger(SysAttributeChangeListener.class);
    private final SecurityMetadataDistributeProcessor securityMetadataDistributeProcessor;

    public SysAttributeChangeListener(SecurityMetadataDistributeProcessor securityMetadataDistributeProcessor) {
        this.securityMetadataDistributeProcessor = securityMetadataDistributeProcessor;
    }

    public void onApplicationEvent(SysAttributeChangeEvent event) {
        log.debug(" SysAttribute Change Listener, response event!");
        TtcAttribute ttcAttribute = (TtcAttribute)event.getData();
        if (ObjectUtils.isNotEmpty((Object)ttcAttribute)) {
            log.debug(" Got SysAttribute, start to process SysAttribute change.");
            this.securityMetadataDistributeProcessor.distributeChangedSecurityAttribute(ttcAttribute);
        }
    }
}

