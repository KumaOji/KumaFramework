/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package com.kuma.boot.web.request.event;

import com.kuma.boot.web.request.model.RequestLog;
import org.springframework.context.ApplicationEvent;

public class RequestLoggerEvent
extends ApplicationEvent {
    public RequestLoggerEvent(RequestLog requestLog) {
        super((Object)requestLog);
    }
}

