/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.context.event.EventListener
 *  org.springframework.security.authorization.event.AuthorizationGrantedEvent
 */
package com.kuma.boot.security.spring.authorization.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;

public class AuthorizationGrantedEventListener {
    @EventListener(value={AuthorizationGrantedEvent.class})
    public void authorizationDeniedEvent(AuthorizationGrantedEvent<?> event) {
        LogUtils.info((String)"\u8ba4\u8bc1\u6210\u529f authorizationDeniedEvent {}", (Object[])new Object[]{event.getObject()});
    }
}

