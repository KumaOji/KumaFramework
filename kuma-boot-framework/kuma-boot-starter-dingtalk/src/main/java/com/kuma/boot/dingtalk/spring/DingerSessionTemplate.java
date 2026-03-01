/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 */
package com.kuma.boot.dingtalk.spring;

import com.kuma.boot.dingtalk.session.DingerSession;
import com.kuma.boot.dingtalk.session.DingerSessionFactory;
import com.kuma.boot.dingtalk.session.SessionConfiguration;
import org.springframework.beans.factory.DisposableBean;

public class DingerSessionTemplate
implements DingerSession,
DisposableBean {
    private final DingerSessionFactory dingerSessionFactory;
    private final DingerSession dingerSession;
    private final SessionConfiguration sessionConfiguration;

    public DingerSessionTemplate(DingerSessionFactory dingerSessionFactory) {
        this.dingerSessionFactory = dingerSessionFactory;
        this.dingerSession = dingerSessionFactory.dingerSession();
        this.sessionConfiguration = dingerSessionFactory.getConfiguration();
    }

    @Override
    public <T> T getDinger(Class<T> type) {
        return this.dingerSession.getDinger(type);
    }

    @Override
    public SessionConfiguration configuration() {
        return this.sessionConfiguration;
    }

    public void destroy() {
    }

    public DingerSessionFactory getDingerSessionFactory() {
        return this.dingerSessionFactory;
    }
}

