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

package com.kuma.boot.security.spring.authentication.response.entrypoint;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.servlet.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

/**
 * 安全登录url认证入口点 未登录异常
 *
 * @author kuma
 * @version 2023.07
 * @see LoginUrlAuthenticationEntryPoint
 * @since 2023-07-11 09:42:40
 */
public class RedirectLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    /**
     * @param loginFormUrl URL where the login page can be found. Should either be relative to the
     *                     web-app context path (include a leading {@code /}) or an absolute URL.
     */
    public RedirectLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        // 获取登录表单的地址
        // String loginForm = determineUrlToUseForThisRequest(request, response, authException);
        // if (!UrlUtils.isAbsoluteUrl(loginForm)) {
        //	// 不是绝对路径调用父类方法处理
        //	super.commence(request, response, authException);
        //	return;
        // }
        //
        // StringBuffer requestUrl = request.getRequestURL();
        // if (!ObjectUtils.isEmpty(request.getQueryString())) {
        //	requestUrl.append("?").append(request.getQueryString());
        // }
        //
        // 绝对路径在重定向前添加target参数
        //		String targetParameter = URLEncoder.encode(requestUrl.toString(),
        // StandardCharsets.UTF_8);
        //		String targetUrl = loginForm + "?target=" + targetParameter + "&" +
        // SecurityConstants.NONCE_HEADER_NAME +
        // "=" + request.getSession(Boolean.FALSE).getId();
        //		log.debug("重定向至前后端分离的登录页面：{}", targetUrl);
        //		this.redirectStrategy.sendRedirect(request, response, targetUrl);

        super.commence(request, response, authException);
    }

    @Override
    protected String buildRedirectUrlToLoginPage(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) {

        String urlToLoginPage = super.buildRedirectUrlToLoginPage(request, response, authException);
        Map<String, String> paramMap = JakartaServletUtil.getParamMap(request);

        return urlToLoginPage + "?" + getUrlParamsByMap(paramMap);
    }

    public static String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = substringBeforeLast(s, "&");
        }
        return s;
    }
}
