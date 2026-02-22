/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.scheduling.annotation.Async
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.security.spring.event.metadata.processor;

import com.kuma.boot.security.spring.event.domain.RequestMapping;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RequestMappingStoreProcessor {
    private static final Logger log = LoggerFactory.getLogger(RequestMappingStoreProcessor.class);
    private final SecurityMetadataDistributeProcessor securityMetadataDistributeProcessor;

    public RequestMappingStoreProcessor(SecurityMetadataDistributeProcessor securityMetadataDistributeProcessor) {
        this.securityMetadataDistributeProcessor = securityMetadataDistributeProcessor;
    }

    @Async
    public void postProcess(List<RequestMapping> requestMappings) {
        log.debug(" [4] Async store request mapping process BEGIN!");
        this.securityMetadataDistributeProcessor.postRequestMappings(requestMappings);
    }
}

