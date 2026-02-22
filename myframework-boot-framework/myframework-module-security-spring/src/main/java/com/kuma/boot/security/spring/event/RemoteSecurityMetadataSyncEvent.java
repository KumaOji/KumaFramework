/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package com.kuma.boot.security.spring.event;

import org.springframework.context.ApplicationEvent;

public class RemoteSecurityMetadataSyncEvent
extends ApplicationEvent {
    private String data;

    public RemoteSecurityMetadataSyncEvent(String data) {
        super((Object)data);
    }

    public String getData() {
        return (String)this.getSource();
    }
}

