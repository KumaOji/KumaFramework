/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.serializer;

import com.kuma.boot.common.extension.ExtensionLoader;
import com.kuma.boot.common.support.serializer.Serializer;
import java.util.HashMap;
import java.util.Map;

public enum SerializerFactory {
    SF;

    Map<String, Serializer> serializerMap = new HashMap<String, Serializer>();

    private SerializerFactory() {
        ExtensionLoader<Serializer> extensionLoader = ExtensionLoader.getExtensionLoader(Serializer.class);
        this.serializerMap = extensionLoader.getExtensionMap();
    }

    public Serializer getExtension(String name) {
        return this.serializerMap.get(name);
    }
}

