/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.qr;

import com.kuma.boot.totp.code.HashingAlgorithm;

public class QrDataFactory {
    private HashingAlgorithm defaultAlgorithm;
    private int defaultDigits;
    private int defaultTimePeriod;

    public QrDataFactory(HashingAlgorithm defaultAlgorithm, int defaultDigits, int defaultTimePeriod) {
        this.defaultAlgorithm = defaultAlgorithm;
        this.defaultDigits = defaultDigits;
        this.defaultTimePeriod = defaultTimePeriod;
    }

    public QrData.Builder newBuilder() {
        return new QrData.Builder().algorithm(this.defaultAlgorithm).digits(this.defaultDigits).period(this.defaultTimePeriod);
    }
}

