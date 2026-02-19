/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.event.EventListener
 *  org.springframework.scheduling.annotation.Async
 */
package com.kuma.boot.web.request.listener;

import com.kuma.boot.web.request.event.RequestLoggerEvent;
import com.kuma.boot.web.request.model.RequestLog;
import com.kuma.boot.web.request.service.RequestLoggerService;
import java.util.List;
import java.util.Objects;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public class RequestLoggerListener {
    private final List<RequestLoggerService> requestLoggerService;

    public RequestLoggerListener(List<RequestLoggerService> requestLoggerServices) {
        this.requestLoggerService = requestLoggerServices;
    }

    @Async
    @EventListener(value={RequestLoggerEvent.class})
    public void saveRequestLog(RequestLoggerEvent event) {
        RequestLog requestLog = (RequestLog)event.getSource();
        if (Objects.nonNull(this.requestLoggerService) && !this.requestLoggerService.isEmpty()) {
            this.requestLoggerService.stream().filter(Objects::nonNull).forEach(service -> service.save(requestLog));
        }
    }
}

