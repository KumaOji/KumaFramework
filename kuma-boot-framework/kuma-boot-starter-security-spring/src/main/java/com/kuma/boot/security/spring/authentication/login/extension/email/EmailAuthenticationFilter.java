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

package com.kuma.boot.security.spring.authentication.login.extension.email;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

public class EmailAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String KMC_SECURITY_EXTENSIONS_EMAIL_LOGIN_EMAIL_KEY = "email";
    public static final String KMC_SECURITY_EXTENSIONS_EMAIL_LOGIN_EMAIL_CODE_KEY = "emailCode";
    public static final String KMC_SECURITY_EXTENSIONS_EMAIL_LOGIN_URL = "/login/email";

    /**
     * 是否仅支持post方式
     */
    private boolean postOnly = true;

    private Converter<HttpServletRequest, EmailAuthenticationToken>
            emailAuthenticationTokenConverter;

    private static final PathPatternRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            PathPatternRequestMatcher.withDefaults()
                    .matcher(HttpMethod.POST, KMC_SECURITY_EXTENSIONS_EMAIL_LOGIN_URL);

    /**
     * 对请求进行过滤，只有接口为 /emil-login，请求方式为 POST，才会进入逻辑
     */
    public EmailAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.emailAuthenticationTokenConverter = new com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationConverter();
    }

    public EmailAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.emailAuthenticationTokenConverter = new com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationConverter();
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (this.postOnly && !HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        EmailAuthenticationToken emailAuthenticationToken =
                emailAuthenticationTokenConverter.convert(request);
        // Allow subclasses to set the "details" property
        setDetails(request, emailAuthenticationToken);
        return this.getAuthenticationManager().authenticate(emailAuthenticationToken);

        //        // 需要是 POST 请求
        //        if (postOnly &&  !request.getMethod().equals(HttpMethod.POST.name())) {
        //            throw new AuthenticationServiceException(
        //                    "Authentication method not supported: " + request.getMethod());
        //        }
        //        // 判断请求格式是否 JSON
        //        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
        //            Map<String, String> loginData = new HashMap<>(2);
        //            try {
        //                loginData = JsonMapper.builder().build().readValue(request.getInputStream(),
        // Map.class);
        //            } catch (IOException e) {
        //                throw new InternalAuthenticationServiceException("请求参数异常");
        //            }
        //            // 获得请求参数
        //            String email = loginData.get(emailParameter);
        //            String emailCode = loginData.get(emailCodeParameter);
        //            // 检查验证码
        //            checkEmailCode(emailCode);
        //            if(StringUtils.isEmpty(email)){
        //                throw new AuthenticationServiceException("邮箱不能为空");
        //            }
        //            /**
        //             * 使用请求参数传递的邮箱和验证码，封装为一个未认证 EmailVerificationCodeAuthenticationToken 身份认证对象，
        //             * 然后将该对象交给 AuthenticationManager 进行认证
        //             */
        //            EmailAuthenticationToken authRequest = new EmailAuthenticationToken(email);
        //            setDetails(request, authRequest);
        //            return this.getAuthenticationManager().authenticate(authRequest);
        //        }
        //        return null;
    }

    public void setDetails(HttpServletRequest request, EmailAuthenticationToken token) {
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    private void checkEmailCode(String emailCode) {
        // 实际当中请从 Redis 中获取
        String verifyCode = "123456";
        if (StrUtil.isEmpty(verifyCode)) {
            throw new AuthenticationServiceException("请重新申请验证码!");
        }
        if (!verifyCode.equalsIgnoreCase(emailCode)) {
            throw new AuthenticationServiceException("验证码错误!");
        }
    }

    public boolean isPostOnly() {
        return postOnly;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public Converter<HttpServletRequest, EmailAuthenticationToken>
    getEmailAuthenticationTokenConverter() {
        return emailAuthenticationTokenConverter;
    }

    public void setEmailAuthenticationTokenConverter(
            Converter<HttpServletRequest, EmailAuthenticationToken>
                    emailAuthenticationTokenConverter) {
        this.emailAuthenticationTokenConverter = emailAuthenticationTokenConverter;
    }

    public void setEmailParameter(String emailParameter) {
        if (this.emailAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationConverter emailAuthenticationConverter) {
            emailAuthenticationConverter.setEmailParameter(emailParameter);
        }
    }

    public void setEmailCodeParameter(String emailCodeParameter) {
        if (this.emailAuthenticationTokenConverter
                instanceof com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationConverter emailAuthenticationConverter) {
            emailAuthenticationConverter.setEmailCodeParameter(emailCodeParameter);
        }
    }
}
