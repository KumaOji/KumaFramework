/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.aot.hint.RuntimeHints
 *  org.springframework.aot.hint.RuntimeHintsRegistrar
 */
package com.kuma.boot.ip2region.autoconfigure;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class Ip2regionRuntimeHintsRegistrar
implements RuntimeHintsRegistrar {
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("ip2region/ip2region.xdb");
    }
}

