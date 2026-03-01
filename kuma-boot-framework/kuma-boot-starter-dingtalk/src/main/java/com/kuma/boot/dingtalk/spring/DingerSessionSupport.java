/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.spring;

import com.kuma.boot.dingtalk.session.DingerSession;
import com.kuma.boot.dingtalk.session.DingerSessionFactory;

public abstract class DingerSessionSupport {
    private DingerSessionTemplate dingerSessionTemplate;

    public void setDingerSessionFactory(DingerSessionFactory dingerSessionFactory) {
        if (this.dingerSessionTemplate == null || dingerSessionFactory != this.dingerSessionTemplate.getDingerSessionFactory()) {
            this.dingerSessionTemplate = this.createDingerSessionTemplate(dingerSessionFactory);
        }
    }

    public DingerSession getDingerSession() {
        return this.dingerSessionTemplate;
    }

    protected DingerSessionTemplate createDingerSessionTemplate(DingerSessionFactory dingerSessionFactory) {
        return new DingerSessionTemplate(dingerSessionFactory);
    }
}

