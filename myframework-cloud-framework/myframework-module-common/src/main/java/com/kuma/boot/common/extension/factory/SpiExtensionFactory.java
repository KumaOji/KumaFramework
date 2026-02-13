/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.factory;

import com.kuma.boot.common.extension.ExtensionFactory;
import com.kuma.boot.common.extension.ExtensionLoader;
import com.kuma.boot.common.extension.SPI;

public class SpiExtensionFactory
implements ExtensionFactory {
    @Override
    public <T> T getExtension(Class<T> type, String name) {
        ExtensionLoader<T> loader;
        if (type.isInterface() && type.isAnnotationPresent(SPI.class) && !(loader = ExtensionLoader.getExtensionLoader(type)).getSupportedExtensions().isEmpty()) {
            return loader.getAdaptiveExtension();
        }
        return null;
    }
}

