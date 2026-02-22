/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.handler;

import org.springframework.http.HttpStatus;

public class MfaAuthenticationResponse {
    private String message;
    private String mfa;
    private String qrCode;
    private String token;
    private int responseCode;
    private boolean authenticated;

    public MfaAuthenticationResponse(String message, String mfa, String qrCode, String token, int responseCode, boolean authenticated) {
        this.message = message;
        this.mfa = mfa;
        this.qrCode = qrCode;
        this.token = token;
        this.responseCode = responseCode;
        this.authenticated = authenticated;
    }

    public static MfaAuthenticationResponse unauthenticated(String message, String mfa, HttpStatus status, String qrCode) {
        return new MfaAuthenticationResponse(message, mfa, qrCode, null, status.value(), false);
    }

    public static MfaAuthenticationResponse authenticated(String mfa, String token) {
        return new MfaAuthenticationResponse("SUCCESS", mfa, null, token, HttpStatus.OK.value(), true);
    }

    public String getMessage() {
        return this.message;
    }

    public String getMfa() {
        return this.mfa;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public String getToken() {
        return this.token;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }
}

