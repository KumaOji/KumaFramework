/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 */
package com.kuma.boot.security.spring.authentication.login.extension.account;

import com.kuma.boot.security.spring.utils.ExtensionLoginUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

public class AccountAuthenticationConverter
implements Converter<HttpServletRequest, AccountAuthenticationToken> {
    private String usernameParameter = "username";
    private String passwordParameter = "password";
    private String typeParameter = "type";

    public AccountAuthenticationToken convert(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = ExtensionLoginUtils.getParameters(request);
        ExtensionLoginUtils.checkRequiredParameter(parameters, this.usernameParameter);
        ExtensionLoginUtils.checkRequiredParameter(parameters, this.passwordParameter);
        ExtensionLoginUtils.checkRequiredParameter(parameters, this.typeParameter);
        String username = request.getParameter(this.usernameParameter);
        String password = request.getParameter(this.passwordParameter);
        String type = request.getParameter(this.typeParameter);
        return AccountAuthenticationToken.unauthenticated(username, password, type);
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText((String)usernameParameter, (String)"Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText((String)passwordParameter, (String)"Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }

    public final String getUsernameParameter() {
        return this.usernameParameter;
    }

    public String getPasswordParameter() {
        return this.passwordParameter;
    }

    public String getTypeParameter() {
        return this.typeParameter;
    }

    public void setTypeParameter(String typeParameter) {
        this.typeParameter = typeParameter;
    }
}

