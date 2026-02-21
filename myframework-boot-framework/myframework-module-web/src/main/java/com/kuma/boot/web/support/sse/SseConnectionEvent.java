package com.kuma.boot.web.support.sse;

import org.springframework.context.ApplicationEvent;

/**
 * @author zhanghongbin
 */
public class SseConnectionEvent extends ApplicationEvent {

    private String id;

    public SseConnectionEvent(Object source) {
        super(source);
        this.id = source.toString();
    }

    public String getId() {
        return id;
    }
}
