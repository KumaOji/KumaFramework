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

    public com.kuma.boot.totp.qr.QrData.Builder newBuilder() {
        return new com.kuma.boot.totp.qr.QrData.Builder()
                .algorithm(defaultAlgorithm)
                .digits(defaultDigits)
                .period(defaultTimePeriod);
    }
}
