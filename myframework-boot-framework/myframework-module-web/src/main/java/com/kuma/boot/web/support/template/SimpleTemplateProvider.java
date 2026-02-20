/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.template;

public class SimpleTemplateProvider
extends TemplateProvider {
    public Object g(String key) {
        return this.getattr(key);
    }

    public void s(String key, Object value) {
        this.setattr(key, value);
    }

    public SimpleTemplateProvider s2(String key, Object value) {
        this.setattr(key, value);
        return this;
    }

    public Object w(boolean istrue, Object trueObj, Object falseObj) {
        return this.where(istrue, trueObj, falseObj);
    }

    public String p(Object o) {
        return this.print(o);
    }
}

