/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.context.event.EventListener
 *  org.springframework.security.authorization.event.AuthorizationDeniedEvent
 */
package com.kuma.boot.security.spring.authorization.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;

public class AuthorizationDeniedEventListener {
    @EventListener(value={AuthorizationDeniedEvent.class})
    public void authorizationDeniedEvent(AuthorizationDeniedEvent<?> event) {
        LogUtils.info((String)"\u6388\u6743\u5931\u8d25 authorizationDeniedEvent {}", (Object[])new Object[]{event.getObject()});
    }
}

