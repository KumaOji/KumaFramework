/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.wrapper;

import java.util.HashSet;
import java.util.Set;

public class WrapperExtensionLoader {
    private Set<Class<?>> cachedWrapperClasses;

    public Set<Class<?>> getCachedWrapperClasses() {
        return this.cachedWrapperClasses;
    }

    public void cacheWrapperClass(Class<?> clazz) {
        if (this.cachedWrapperClasses == null) {
            this.cachedWrapperClasses = new HashSet();
        }
        this.cachedWrapperClasses.add(clazz);
    }
}

