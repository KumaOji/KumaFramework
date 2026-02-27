/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.support.core.definition.enums;

public enum FontStyle {
    PLAIN(0),
    BOLD(1),
    ITALIC(2);

    private final int mapping;

    private FontStyle(int mapping) {
        this.mapping = mapping;
    }

    public int getMapping() {
        return this.mapping;
    }
}

