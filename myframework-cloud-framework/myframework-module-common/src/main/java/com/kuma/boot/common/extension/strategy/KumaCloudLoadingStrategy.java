/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.strategy;

import com.kuma.boot.common.extension.strategy.LoadingStrategy;

public class KumaCloudLoadingStrategy implements LoadingStrategy {
    public static final String META_INF = "META-INF/kmc/";

    @Override
    public String directory() {
        return META_INF;
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}

