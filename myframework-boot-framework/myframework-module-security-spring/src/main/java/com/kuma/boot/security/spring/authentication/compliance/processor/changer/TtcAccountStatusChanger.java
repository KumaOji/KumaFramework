/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  org.springframework.context.ApplicationEvent
 */
package com.kuma.boot.security.spring.authentication.compliance.processor.changer;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.security.spring.event.LocalChangeUserStatusEvent;
import com.kuma.boot.security.spring.event.RemoteChangeUserStatusEvent;
import com.kuma.boot.security.spring.event.domain.TtcUserStatus;
import org.springframework.context.ApplicationEvent;

public class TtcAccountStatusChanger
implements AccountStatusChanger {
    @Override
    public String getDestinationServiceName() {
        return "kuma-cloud-sys";
    }

    @Override
    public void postLocalProcess(TtcUserStatus data) {
        ContextUtils.getApplicationContext().publishEvent((ApplicationEvent)new LocalChangeUserStatusEvent(data));
    }

    @Override
    public void postRemoteProcess(String data, String originService, String destinationService) {
        ContextUtils.getApplicationContext().publishEvent((ApplicationEvent)new RemoteChangeUserStatusEvent(data));
    }
}

