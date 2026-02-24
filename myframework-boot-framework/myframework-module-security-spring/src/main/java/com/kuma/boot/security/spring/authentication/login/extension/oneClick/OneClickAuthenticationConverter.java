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

package com.kuma.boot.security.spring.authentication.login.extension.oneClick;

import static com.kuma.boot.security.spring.authentication.login.extension.oneClick.OneClickLoginAuthenticationFilter.KMC_SECURITY_EXTENSIONS_ONE_CLICK_LOGIN_TOKEN_KEY;
import static java.util.Objects.nonNull;

import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.OneClickLoginService;
import com.kuma.boot.security.spring.enums.ErrorCodeEnum;
import com.kuma.boot.security.spring.exception.Auth2Exception;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.convert.converter.Converter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 帐户验证转换器
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-06-29 13:07:23
 */
public class OneClickAuthenticationConverter
        implements Converter<HttpServletRequest, OneClickLoginAuthenticationToken> {

    private String tokenParamName = KMC_SECURITY_EXTENSIONS_ONE_CLICK_LOGIN_TOKEN_KEY;

    /**
     * 其他请求参数名称列表(包括请求头名称)
     */
    private List<String> otherParamNames = new ArrayList<>();

    private OneClickLoginService oneClickLoginService;

    @Override
    public OneClickLoginAuthenticationToken convert(HttpServletRequest request) {
        String accessToken = obtainAccessToken(request);
        if (StrUtil.isEmpty(accessToken)) {
            throw new Auth2Exception(ErrorCodeEnum.ACCESS_TOKEN_NOT_EMPTY, this.tokenParamName);
        }

        accessToken = accessToken.trim();
        Map<String, String> otherParamMap = getOtherParamMap(this.otherParamNames, request);
        String mobile = this.oneClickLoginService.callback(accessToken, otherParamMap);

        return OneClickLoginAuthenticationToken.unauthenticated(mobile, otherParamMap);
    }

    /**
     * 获取 access token;
     *
     * @param request so that request attributes can be retrieved
     * @return access token
     */
    protected String obtainAccessToken(HttpServletRequest request) {
        ServletWebRequest servletWebRequest = new ServletWebRequest(request, null);
        return servletWebRequest.getParameter(tokenParamName);
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

    /**
     * 从 request 中获取 otherParamNames 的 paramValue
     *
     * @param otherParamNames 参数名称列表
     * @param request         request
     * @return map(paramName, paramValue)
     */
    @Nullable
    protected Map<String, String> getOtherParamMap(
            @NonNull List<String> otherParamNames, @NonNull HttpServletRequest request) {
        if (otherParamNames.isEmpty()) {
            return null;
        }
        // map(paramName, paramValue)
        final Map<String, String> otherMap = new HashMap<>(otherParamNames.size());
        otherParamNames.forEach(
                name -> {
                    try {
                        String value = ServletRequestUtils.getStringParameter(request, name);
                        otherMap.put(name, value);
                    } catch (ServletRequestBindingException e) {
                        String headerValue = request.getHeader(name);
                        if (nonNull(headerValue)) {
                            otherMap.put(name, headerValue);
                        }
                    }
                });

        return otherMap;
    }
}
