/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash;

public enum HashType {
    MD2("MD2"),
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    SHA384("SHA-384"),
    SHA512("SHA-512");

    private final String code;

    private HashType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public String toString() {
        return "HashType{code='" + this.code + "'}";
    }
}

