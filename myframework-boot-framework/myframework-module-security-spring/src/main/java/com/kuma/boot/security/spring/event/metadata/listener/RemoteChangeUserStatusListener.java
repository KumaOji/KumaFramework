/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.common.JsonUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.security.spring.event.metadata.listener;

import com.kuma.boot.common.utils.common.JsonUtils;
import com.kuma.boot.security.spring.event.RemoteChangeUserStatusEvent;
import com.kuma.boot.security.spring.event.domain.TtcUserStatus;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RemoteChangeUserStatusListener
implements ApplicationListener<RemoteChangeUserStatusEvent> {
    private static final Logger log = LoggerFactory.getLogger(RemoteChangeUserStatusListener.class);

    public void onApplicationEvent(RemoteChangeUserStatusEvent event) {
        TtcUserStatus ttcUserStatus;
        log.info(" Request mapping gather REMOTE listener, response event!");
        String data = event.getData();
        log.debug(" Fetch data [{}]", (Object)data);
        if (!ObjectUtils.isNotEmpty((Object)data) || ObjectUtils.isNotEmpty((Object)(ttcUserStatus = (TtcUserStatus)JsonUtils.toObject((String)data, TtcUserStatus.class)))) {
            // empty if block
        }
    }
}

