/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.factory;

import com.kuma.boot.common.extension.Adaptive;
import com.kuma.boot.common.extension.ExtensionFactory;
import com.kuma.boot.common.extension.ExtensionLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Adaptive
public class AdaptiveExtensionFactory
implements ExtensionFactory {
    private final List<ExtensionFactory> factories;

    public AdaptiveExtensionFactory() {
        ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
        ArrayList<ExtensionFactory> list = new ArrayList<ExtensionFactory>();
        for (String name : loader.getSupportedExtensions()) {
            list.add(loader.getExtension(name));
        }
        this.factories = Collections.unmodifiableList(list);
    }

    @Override
    public <T> T getExtension(Class<T> type, String name) {
        for (ExtensionFactory factory : this.factories) {
            T extension = factory.getExtension(type, name);
            if (extension == null) continue;
            return extension;
        }
        return null;
    }
}

