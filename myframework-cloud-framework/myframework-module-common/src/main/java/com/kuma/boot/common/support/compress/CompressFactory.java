/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.compress;

import com.kuma.boot.common.extension.ExtensionLoader;
import com.kuma.boot.common.support.compress.Compress;
import java.util.HashMap;
import java.util.Map;

public enum CompressFactory {
    CF;

    Map<String, Compress> compressMap = new HashMap<String, Compress>();

    private CompressFactory() {
        ExtensionLoader<Compress> extensionLoader = ExtensionLoader.getExtensionLoader(Compress.class);
        this.compressMap = extensionLoader.getExtensionMap();
    }

    public Compress getExtension(String name) {
        return this.compressMap.get(name);
    }

    public Map<String, Compress> getCompressMap() {
        return this.compressMap;
    }
}

