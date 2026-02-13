/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.compile;

import com.kuma.boot.common.extension.SPI;

@SPI(value="javassist")
public interface Compiler {
    public Class<?> compile(String var1, ClassLoader var2);
}

