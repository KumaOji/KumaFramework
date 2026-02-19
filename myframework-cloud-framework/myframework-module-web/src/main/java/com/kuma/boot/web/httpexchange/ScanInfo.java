/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.httpexchange;

import java.util.LinkedHashSet;

final class ScanInfo {
    public final LinkedHashSet<String> basePackages = new LinkedHashSet();
    public final LinkedHashSet<Class<?>> clients = new LinkedHashSet();

    ScanInfo() {
    }

    public void clear() {
        this.basePackages.clear();
        this.clients.clear();
    }
}

