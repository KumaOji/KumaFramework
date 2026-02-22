/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.security.spring.event.metadata.listener;

import com.kuma.boot.security.spring.event.LocalRequestMappingGatherEvent;
import com.kuma.boot.security.spring.event.metadata.processor.RequestMappingStoreProcessor;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class LocalRequestMappingGatherListener
implements ApplicationListener<LocalRequestMappingGatherEvent> {
    private static final Logger log = LoggerFactory.getLogger(LocalRequestMappingGatherListener.class);
    private final RequestMappingStoreProcessor requestMappingStoreProcessor;

    public LocalRequestMappingGatherListener(RequestMappingStoreProcessor requestMappingStoreProcessor) {
        this.requestMappingStoreProcessor = requestMappingStoreProcessor;
    }

    public void onApplicationEvent(LocalRequestMappingGatherEvent event) {
        log.info(" Request mapping gather LOCAL listener, response event!");
        List requestMappings = (List)event.getData();
        if (CollectionUtils.isNotEmpty((Collection)requestMappings)) {
            this.requestMappingStoreProcessor.postProcess(requestMappings);
        }
    }
}

