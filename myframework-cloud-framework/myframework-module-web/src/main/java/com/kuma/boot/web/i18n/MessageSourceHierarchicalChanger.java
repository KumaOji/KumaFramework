/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  org.springframework.context.HierarchicalMessageSource
 *  org.springframework.context.MessageSource
 */
package com.kuma.boot.web.i18n;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;

public class MessageSourceHierarchicalChanger {
    @Resource(name="messageSource")
    private MessageSource messageSource;
    @Resource(name="dynamicMessageSource")
    private DynamicMessageSource dynamicMessageSource;

    @PostConstruct
    public void changeMessageSourceParent() {
        if (this.messageSource instanceof HierarchicalMessageSource) {
            HierarchicalMessageSource hierarchicalMessageSource = (HierarchicalMessageSource)this.messageSource;
            MessageSource parentMessageSource = hierarchicalMessageSource.getParentMessageSource();
            this.dynamicMessageSource.setParentMessageSource(parentMessageSource);
            hierarchicalMessageSource.setParentMessageSource((MessageSource)this.dynamicMessageSource);
        } else {
            this.dynamicMessageSource.setParentMessageSource(this.messageSource);
        }
    }
}

