/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.util.MultiValueMap
 */
package com.kuma.boot.security.spring.authentication.login.extension.face;

import com.kuma.boot.security.spring.utils.ExtensionLoginUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.MultiValueMap;

public class FaceAuthenticationConverter
implements Converter<HttpServletRequest, FaceAuthenticationToken> {
    private String imgBase64Parameter = "imgBase64";

    public FaceAuthenticationToken convert(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = ExtensionLoginUtils.getParameters(request);
        ExtensionLoginUtils.checkRequiredParameter(parameters, this.imgBase64Parameter);
        String imgBase64 = request.getParameter(this.imgBase64Parameter);
        return new FaceAuthenticationToken(imgBase64);
    }

    public String getImgBase64Parameter() {
        return this.imgBase64Parameter;
    }

    public void setImgBase64Parameter(String imgBase64Parameter) {
        this.imgBase64Parameter = imgBase64Parameter;
    }
}

