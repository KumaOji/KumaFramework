/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.authentication.login.extension.mfa.handler;

import org.springframework.http.HttpStatus;

/**
 * @author: ReLive27
 * @since: 2023/1/16 19:16
 */
public class MfaAuthenticationResponse {
    private String message;
    private String mfa;
    private String qrCode;
    private String token;
    private int responseCode;
    private boolean authenticated;

    public MfaAuthenticationResponse(
            String message,
            String mfa,
            String qrCode,
            String token,
            int responseCode,
            boolean authenticated) {
        this.message = message;
        this.mfa = mfa;
        this.qrCode = qrCode;
        this.token = token;
        this.responseCode = responseCode;
        this.authenticated = authenticated;
    }

    public static MfaAuthenticationResponse unauthenticated(
            String message, String mfa, HttpStatus status, String qrCode) {
        return new MfaAuthenticationResponse(message, mfa, qrCode, null, status.value(), false);
    }

    public static MfaAuthenticationResponse authenticated(String mfa, String token) {
        return new MfaAuthenticationResponse(
                "SUCCESS", mfa, null, token, HttpStatus.OK.value(), true);
    }

    public String getMessage() {
        return message;
    }

    public String getMfa() {
        return mfa;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getToken() {
        return token;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
