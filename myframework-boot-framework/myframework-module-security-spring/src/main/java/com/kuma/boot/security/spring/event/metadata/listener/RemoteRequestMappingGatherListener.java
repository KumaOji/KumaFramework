/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.common.JsonUtils
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.security.spring.event.metadata.listener;

import com.kuma.boot.common.utils.common.JsonUtils;
import com.kuma.boot.security.spring.event.RemoteRequestMappingGatherEvent;
import com.kuma.boot.security.spring.event.domain.RequestMapping;
import com.kuma.boot.security.spring.event.metadata.processor.RequestMappingStoreProcessor;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RemoteRequestMappingGatherListener
implements ApplicationListener<RemoteRequestMappingGatherEvent> {
    private static final Logger log = LoggerFactory.getLogger(RemoteRequestMappingGatherListener.class);
    private final RequestMappingStoreProcessor requestMappingStoreProcessor;

    public RemoteRequestMappingGatherListener(RequestMappingStoreProcessor requestMappingStoreProcessor) {
        this.requestMappingStoreProcessor = requestMappingStoreProcessor;
    }

    public void onApplicationEvent(RemoteRequestMappingGatherEvent event) {
        List requestMappings;
        log.info(" Request mapping gather REMOTE listener, response event!");
        String requestMapping = event.getData();
        log.debug(" Fetch data [{}]", (Object)requestMapping);
        if (ObjectUtils.isNotEmpty((Object)requestMapping) && CollectionUtils.isNotEmpty((Collection)(requestMappings = JsonUtils.toList((String)requestMapping, RequestMapping.class)))) {
            this.requestMappingStoreProcessor.postProcess(requestMappings);
        }
    }
}

