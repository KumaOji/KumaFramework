/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContext
 */
package com.kuma.boot.web.laytpl.js;

import org.springframework.context.ApplicationContext;

public class JsContext {
    private final ApplicationContext applicationContext;

    public JsContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Object use(String beanName) {
        return this.applicationContext.getBean(beanName);
    }

    public ApplicationContext getContext() {
        return this.applicationContext;
    }
}

