/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 */
package com.kuma.boot.dingtalk.spring;

import org.springframework.beans.factory.FactoryBean;

public class DingerFactoryBean<T>
extends DingerSessionSupport
implements FactoryBean<T> {
    private Class<T> dingerInterface;

    public DingerFactoryBean() {
    }

    public DingerFactoryBean(Class dingerInterface) {
        this.dingerInterface = dingerInterface;
    }

    public T getObject() throws Exception {
        return this.getDingerSession().getDinger(this.dingerInterface);
    }

    public Class<?> getObjectType() {
        return this.dingerInterface;
    }

    public boolean isSingleton() {
        return true;
    }
}

