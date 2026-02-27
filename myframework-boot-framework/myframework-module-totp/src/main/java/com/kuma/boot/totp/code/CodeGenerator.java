/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.code;

import com.kuma.boot.totp.exceptions.CodeGenerationException;

public interface CodeGenerator {
    public String generate(String var1, long var2) throws CodeGenerationException;
}

