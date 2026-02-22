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

import com.kuma.boot.security.spring.event.LocalChangeUserStatusEvent;
import com.kuma.boot.security.spring.event.domain.TtcUserStatus;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class LocalChangeUserStatusListener
implements ApplicationListener<LocalChangeUserStatusEvent> {
    private static final Logger log = LoggerFactory.getLogger(LocalChangeUserStatusListener.class);

    public void onApplicationEvent(LocalChangeUserStatusEvent event) {
        log.info(" Change user status gather LOCAL listener, response event!");
        TtcUserStatus ttcUserStatus = (TtcUserStatus)event.getData();
        if (ObjectUtils.isNotEmpty((Object)ttcUserStatus)) {
            // empty if block
        }
    }
}

