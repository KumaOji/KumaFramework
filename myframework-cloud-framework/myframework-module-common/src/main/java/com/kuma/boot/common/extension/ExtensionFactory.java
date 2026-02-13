/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension;

import com.kuma.boot.common.extension.SPI;

@SPI
public interface ExtensionFactory {
    public <T> T getExtension(Class<T> var1, String var2);
}

