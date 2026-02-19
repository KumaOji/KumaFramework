/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.web.gracefulresponse;

import com.kuma.boot.web.gracefulresponse.api.ExceptionAliasFor;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionAliasRegister {
    private Logger logger = LoggerFactory.getLogger(ExceptionAliasRegister.class);
    private ConcurrentHashMap<Class<? extends Throwable>, ExceptionAliasFor> aliasForMap = new ConcurrentHashMap();

    public ExceptionAliasRegister doRegisterExceptionAlias(Class<? extends Throwable> throwableClass) {
        Class<? extends Throwable>[] classes;
        ExceptionAliasFor exceptionAliasFor = throwableClass.getAnnotation(ExceptionAliasFor.class);
        if (exceptionAliasFor == null) {
            this.logger.error("\u6ce8\u518c\u4e86\u672a\u52a0ExceptionAliasFor\u7684\u5f02\u5e38,throwableClass={}", throwableClass);
            throw new RuntimeException();
        }
        for (Class<? extends Throwable> c : classes = exceptionAliasFor.aliasFor()) {
            this.aliasForMap.put(c, exceptionAliasFor);
        }
        return this;
    }

    public ExceptionAliasFor getExceptionAliasFor(Class<? extends Throwable> tClazz) {
        return this.aliasForMap.get(tClazz);
    }
}

