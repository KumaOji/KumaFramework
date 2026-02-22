/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.util.MultiValueMap
 */
package com.kuma.boot.security.spring.authentication.login.extension.fingerprint;

import com.kuma.boot.security.spring.utils.ExtensionLoginUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.MultiValueMap;

public class FingerprintAuthenticationConverter
implements Converter<HttpServletRequest, FingerprintAuthenticationToken> {
    private String usernameParameter = "name";
    private String fingerPrintParameter = "fingerPrint";

    public FingerprintAuthenticationToken convert(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = ExtensionLoginUtils.getParameters(request);
        ExtensionLoginUtils.checkRequiredParameter(parameters, this.usernameParameter);
        ExtensionLoginUtils.checkRequiredParameter(parameters, this.fingerPrintParameter);
        String username = request.getParameter(this.usernameParameter);
        String fingerPrint = request.getParameter(this.fingerPrintParameter);
        return FingerprintAuthenticationToken.unauthenticated(username, fingerPrint);
    }

    public String getUsernameParameter() {
        return this.usernameParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getFingerPrintParameter() {
        return this.fingerPrintParameter;
    }

    public void setFingerPrintParameter(String fingerPrintParameter) {
        this.fingerPrintParameter = fingerPrintParameter;
    }
}

