/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.security.web.authentication.WebAuthenticationDetails
 */
package com.kuma.boot.security.spring.authentication.login.form;

import com.kuma.boot.security.spring.utils.SymmetricUtils;
import com.kuma.boot.security.spring.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class FormLoginWebAuthenticationDetails
extends WebAuthenticationDetails {
    private final Boolean closed;
    private final String parameterName;
    private final String category;
    private String code = null;
    private String identity = null;

    public FormLoginWebAuthenticationDetails(String remoteAddress, String sessionId, Boolean closed, String parameterName, String category, String code, String identity) {
        super(remoteAddress, sessionId);
        this.closed = closed;
        this.parameterName = parameterName;
        this.category = category;
        this.code = code;
        this.identity = identity;
    }

    public FormLoginWebAuthenticationDetails(HttpServletRequest request, boolean closed, String parameterName, String category) {
        super(request);
        this.closed = closed;
        this.parameterName = parameterName;
        this.category = category;
        this.init(request);
    }

    private void init(HttpServletRequest request) {
        String encryptedCode = request.getParameter(this.parameterName);
        String key = request.getParameter("symmetric");
        HttpSession session = WebUtils.getSession(request);
        this.identity = session.getId();
        if (StringUtils.isNotBlank((CharSequence)key) && StringUtils.isNotBlank((CharSequence)encryptedCode)) {
            byte[] byteKey = SymmetricUtils.getDecryptedSymmetricKey(key);
            this.code = SymmetricUtils.decrypt(encryptedCode, byteKey);
        }
    }

    public Boolean getClosed() {
        return this.closed;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    public String getCategory() {
        return this.category;
    }

    public String getCode() {
        return this.code;
    }

    public String getIdentity() {
        return this.identity;
    }
}

