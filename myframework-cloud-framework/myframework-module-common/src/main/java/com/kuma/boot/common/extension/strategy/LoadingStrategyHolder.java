/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.kuma.boot.common.extension.strategy;

import com.kuma.boot.common.extension.strategy.LoadingStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.ArrayUtils;

public class LoadingStrategyHolder {
    public static volatile LoadingStrategy[] strategies = LoadingStrategyHolder.loadLoadingStrategies();

    public static void setLoadingStrategies(LoadingStrategy ... strategies) {
        if (ArrayUtils.isNotEmpty((Object[])strategies)) {
            LoadingStrategyHolder.strategies = strategies;
        }
    }

    public static LoadingStrategy[] loadLoadingStrategies() {
        return (LoadingStrategy[])StreamSupport.stream(ServiceLoader.load(LoadingStrategy.class).spliterator(), false).sorted().toArray(LoadingStrategy[]::new);
    }

    public static List<LoadingStrategy> getLoadingStrategies() {
        return Arrays.asList(strategies);
    }
}

