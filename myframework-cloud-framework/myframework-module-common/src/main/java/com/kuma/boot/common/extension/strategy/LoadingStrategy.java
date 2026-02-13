/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.strategy;

import com.kuma.boot.common.extension.strategy.Prioritized;

public interface LoadingStrategy
extends Prioritized {
    public String directory();

    default public boolean preferExtensionClassLoader() {
        return false;
    }

    default public String[] excludedPackages() {
        return null;
    }

    default public boolean overridden() {
        return false;
    }
}

