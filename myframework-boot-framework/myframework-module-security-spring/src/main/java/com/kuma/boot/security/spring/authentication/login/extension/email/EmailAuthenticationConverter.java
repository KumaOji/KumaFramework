/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.util.MultiValueMap
 */
package com.kuma.boot.security.spring.authentication.login.extension.email;

import com.kuma.boot.security.spring.utils.ExtensionLoginUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.MultiValueMap;

public class EmailAuthenticationConverter
implements Converter<HttpServletRequest, EmailAuthenticationToken> {
    private String emailParameter = "email";
    private String emailCodeParameter = "emailCode";

    public EmailAuthenticationToken convert(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = ExtensionLoginUtils.getParameters(request);
        ExtensionLoginUtils.checkRequiredParameter(parameters, this.emailParameter);
        ExtensionLoginUtils.checkRequiredParameter(parameters, this.emailCodeParameter);
        String email = request.getParameter(this.emailParameter);
        String emailCode = request.getParameter(this.emailCodeParameter);
        return EmailAuthenticationToken.unauthenticated(email, emailCode);
    }

    public String getEmailParameter() {
        return this.emailParameter;
    }

    public void setEmailParameter(String emailParameter) {
        this.emailParameter = emailParameter;
    }

    public String getEmailCodeParameter() {
        return this.emailCodeParameter;
    }

    public void setEmailCodeParameter(String emailCodeParameter) {
        this.emailCodeParameter = emailCodeParameter;
    }
}

