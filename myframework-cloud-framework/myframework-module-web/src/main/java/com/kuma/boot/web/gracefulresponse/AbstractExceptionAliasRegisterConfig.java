/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.kuma.boot.web.gracefulresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractExceptionAliasRegisterConfig
implements ApplicationContextAware {
    private Logger logger = LoggerFactory.getLogger(AbstractExceptionAliasRegisterConfig.class);

    protected abstract void registerAlias(ExceptionAliasRegister var1);

    public void setApplicationContext(ApplicationContext applicationContext) {
        try {
            ExceptionAliasRegister aliasRegister = (ExceptionAliasRegister)applicationContext.getBean(ExceptionAliasRegister.class);
            this.registerAlias(aliasRegister);
        }
        catch (Exception e) {
            this.logger.warn("\u672a\u4eceApplicationContext\u4e2d\u83b7\u53d6\u5230ExceptionAliasRegister\u5b9e\u4f8b\uff0c @ExceptionAliasFor\u6ce8\u89e3\u5c06\u65e0\u6548", (Throwable)e);
        }
    }
}

