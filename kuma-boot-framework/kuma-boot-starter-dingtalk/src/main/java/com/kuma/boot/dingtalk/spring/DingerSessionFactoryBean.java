/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.kuma.boot.dingtalk.spring;

import com.kuma.boot.dingtalk.session.DefaultDingerSessionFactory;
import com.kuma.boot.dingtalk.session.DingerSessionFactory;
import com.kuma.boot.dingtalk.session.SessionConfiguration;
import java.io.IOException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class DingerSessionFactoryBean
implements FactoryBean<DingerSessionFactory>,
InitializingBean {
    private DingerSessionFactory dingerSessionFactory;
    private SessionConfiguration sessionConfiguration;

    public DingerSessionFactory getObject() throws Exception {
        if (this.dingerSessionFactory == null) {
            this.afterPropertiesSet();
        }
        return this.dingerSessionFactory;
    }

    public Class<?> getObjectType() {
        return this.dingerSessionFactory == null ? DingerSessionFactory.class : this.dingerSessionFactory.getClass();
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        this.dingerSessionFactory = this.buildDingerSessionFactory();
    }

    protected DingerSessionFactory buildDingerSessionFactory() throws IOException {
        return new DefaultDingerSessionFactory(this.sessionConfiguration);
    }

    public void setConfiguration(SessionConfiguration sessionConfiguration) {
        this.sessionConfiguration = sessionConfiguration;
    }
}

