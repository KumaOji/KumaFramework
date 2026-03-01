/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.ContextClosedEvent
 */
package com.kuma.boot.dingtalk.listeners;

import com.kuma.boot.dingtalk.multi.MultiDingerRefresh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class ExitEventListener
extends MultiDingerRefresh
implements ApplicationListener<ContextClosedEvent> {
    private static final Logger log = LoggerFactory.getLogger(ExitEventListener.class);

    public void onApplicationEvent(ContextClosedEvent event) {
        this.refresh();
    }

    private void refresh() {
        ExitEventListener.multiDingerRefresh();
        DingerListenersProperty.clear();
    }
}

