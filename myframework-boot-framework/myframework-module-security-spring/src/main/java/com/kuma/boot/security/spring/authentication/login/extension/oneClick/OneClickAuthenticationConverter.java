/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.http.HttpServletRequest
 *  org.dromara.hutool.core.text.StrUtil
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.web.bind.ServletRequestBindingException
 *  org.springframework.web.bind.ServletRequestUtils
 *  org.springframework.web.context.request.ServletWebRequest
 */
package com.kuma.boot.security.spring.authentication.login.extension.oneClick;

import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.OneClickLoginService;
import com.kuma.boot.security.spring.enums.ErrorCodeEnum;
import com.kuma.boot.security.spring.exception.Auth2Exception;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

public class OneClickAuthenticationConverter
implements Converter<HttpServletRequest, OneClickLoginAuthenticationToken> {
    private String tokenParamName = "accessToken";
    private List<String> otherParamNames = new ArrayList<String>();
    private OneClickLoginService oneClickLoginService;

    public OneClickLoginAuthenticationToken convert(HttpServletRequest request) {
        String accessToken = this.obtainAccessToken(request);
        if (StrUtil.isEmpty((CharSequence)accessToken)) {
            throw new Auth2Exception(ErrorCodeEnum.ACCESS_TOKEN_NOT_EMPTY, this.tokenParamName);
        }
        accessToken = accessToken.trim();
        Map<String, String> otherParamMap = this.getOtherParamMap(this.otherParamNames, request);
        String mobile = this.oneClickLoginService.callback(accessToken, otherParamMap);
        return OneClickLoginAuthenticationToken.unauthenticated(mobile, otherParamMap);
    }

    protected String obtainAccessToken(HttpServletRequest request) {
        ServletWebRequest servletWebRequest = new ServletWebRequest(request, null);
        return servletWebRequest.getParameter(this.tokenParamName);
    }

    public void setOtherParamNames(List<String> otherParamNames) {
        this.otherParamNames = otherParamNames;
    }

    public void setTokenParamName(String tokenParamName) {
        this.tokenParamName = tokenParamName;
    }

    public void setOneClickLoginService(OneClickLoginService oneClickLoginService) {
        this.oneClickLoginService = oneClickLoginService;
    }

    @Nullable
    protected Map<String, String> getOtherParamMap(@NonNull List<String> otherParamNames, @NonNull HttpServletRequest request) {
        if (otherParamNames.isEmpty()) {
            return null;
        }
        HashMap<String, String> otherMap = new HashMap<String, String>(otherParamNames.size());
        otherParamNames.forEach(name -> {
            block2: {
                try {
                    String value = ServletRequestUtils.getStringParameter((ServletRequest)request, (String)name);
                    otherMap.put((String)name, value);
                }
                catch (ServletRequestBindingException e) {
                    String headerValue = request.getHeader(name);
                    if (!Objects.nonNull(headerValue)) break block2;
                    otherMap.put((String)name, headerValue);
                }
            }
        });
        return otherMap;
    }
}

