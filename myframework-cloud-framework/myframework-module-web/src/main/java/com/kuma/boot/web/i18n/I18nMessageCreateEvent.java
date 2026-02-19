/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package com.kuma.boot.web.i18n;

import java.util.List;
import org.springframework.context.ApplicationEvent;

public class I18nMessageCreateEvent
extends ApplicationEvent {
    public I18nMessageCreateEvent(List<I18nMessage> i18nMessages) {
        super(i18nMessages);
    }

    public List<I18nMessage> getI18nMessages() {
        return (List)super.getSource();
    }
}

