/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.session;

public class DefaultDingerSessionFactory
implements DingerSessionFactory {
    private SessionConfiguration sessionConfiguration;

    public DefaultDingerSessionFactory(SessionConfiguration sessionConfiguration) {
        this.sessionConfiguration = sessionConfiguration;
    }

    @Override
    public DingerSession dingerSession() {
        return new DefaultDingerSession(this.sessionConfiguration);
    }

    @Override
    public SessionConfiguration getConfiguration() {
        return this.sessionConfiguration;
    }
}

