/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.common.JsonUtils
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationListener
 */
package com.kuma.boot.security.spring.event.listener;

import com.kuma.boot.common.utils.common.JsonUtils;
import com.kuma.boot.security.spring.access.security.SecurityAttribute;
import com.kuma.boot.security.spring.authorization.SecurityMetadataSourceAnalyzer;
import com.kuma.boot.security.spring.event.RemoteSecurityMetadataSyncEvent;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class RemoteSecurityMetadataSyncListener
implements ApplicationListener<RemoteSecurityMetadataSyncEvent> {
    private static final Logger log = LoggerFactory.getLogger(RemoteSecurityMetadataSyncListener.class);
    private final SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer;

    public RemoteSecurityMetadataSyncListener(SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer) {
        this.securityMetadataSourceAnalyzer = securityMetadataSourceAnalyzer;
    }

    public void onApplicationEvent(RemoteSecurityMetadataSyncEvent event) {
        List securityMetadata;
        log.info("Remote security metadata sync listener, response event!");
        String data = event.getData();
        if (StringUtils.isNotBlank((CharSequence)data) && CollectionUtils.isNotEmpty((Collection)(securityMetadata = JsonUtils.toList((String)data, SecurityAttribute.class)))) {
            this.securityMetadataSourceAnalyzer.processSecurityAttribute(securityMetadata);
        }
    }
}

