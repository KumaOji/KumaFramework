/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.code;

public enum HashingAlgorithm {
    SHA1("HmacSHA1", "SHA1"),
    SHA256("HmacSHA256", "SHA256"),
    SHA512("HmacSHA512", "SHA512");

    private final String hmacAlgorithm;
    private final String friendlyName;

    private HashingAlgorithm(String hmacAlgorithm, String friendlyName) {
        this.hmacAlgorithm = hmacAlgorithm;
        this.friendlyName = friendlyName;
    }

    public String getHmacAlgorithm() {
        return this.hmacAlgorithm;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }
}

