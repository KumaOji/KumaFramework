/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.qr;

import com.kuma.boot.totp.code.HashingAlgorithm;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class QrData {
    private final String type;
    private final String label;
    private final String secret;
    private final String issuer;
    private final String algorithm;
    private final int digits;
    private final int period;

    private QrData(String type, String label, String secret, String issuer, String algorithm, int digits, int period) {
        this.type = type;
        this.label = label;
        this.secret = secret;
        this.issuer = issuer;
        this.algorithm = algorithm;
        this.digits = digits;
        this.period = period;
    }

    public String getType() {
        return this.type;
    }

    public String getLabel() {
        return this.label;
    }

    public String getSecret() {
        return this.secret;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public int getDigits() {
        return this.digits;
    }

    public int getPeriod() {
        return this.period;
    }

    public String getUri() {
        return "otpauth://" + this.uriEncode(this.type) + "/" + this.uriEncode(this.label) + "?secret=" + this.uriEncode(this.secret) + "&issuer=" + this.uriEncode(this.issuer) + "&algorithm=" + this.uriEncode(this.algorithm) + "&digits=" + this.digits + "&period=" + this.period;
    }

    private String uriEncode(String text) {
        if (text == null) {
            return "";
        }
        try {
            return URLEncoder.encode(text, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Could not URI encode QrData.");
        }
    }

    public static class Builder {
        private String label;
        private String secret;
        private String issuer;
        private HashingAlgorithm algorithm = HashingAlgorithm.SHA1;
        private int digits = 6;
        private int period = 30;

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder algorithm(HashingAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder digits(int digits) {
            this.digits = digits;
            return this;
        }

        public Builder period(int period) {
            this.period = period;
            return this;
        }

        public QrData build() {
            return new QrData("totp", this.label, this.secret, this.issuer, this.algorithm.getFriendlyName(), this.digits, this.period);
        }
    }
}

