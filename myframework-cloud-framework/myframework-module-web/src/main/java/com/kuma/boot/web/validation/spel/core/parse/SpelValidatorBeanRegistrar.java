/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.kuma.boot.web.validation.spel.core.parse;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpelValidatorBeanRegistrar
implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        SpelValidatorBeanRegistrar.applicationContext = applicationContext;
    }
}

