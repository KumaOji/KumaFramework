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

package com.kuma.boot.security.spring.authentication.login.form.sms;

import com.kuma.boot.security.spring.authentication.login.form.sms.service.FormSmsService;
import com.kuma.boot.security.spring.authentication.login.form.sms.service.FormSmsUserDetailsService;
import java.util.Collection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * 手机号码+短信 登录
 */
public class FormSmsLoginAuthenticationProvider
        implements AuthenticationProvider, InitializingBean, MessageSourceAware {

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final FormSmsUserDetailsService formSmsUserDetailsService;
    private final FormSmsService formSmsService;
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public FormSmsLoginAuthenticationProvider(
            FormSmsUserDetailsService formSmsUserDetailsService, FormSmsService formSmsService) {
        this.formSmsUserDetailsService = formSmsUserDetailsService;
        this.formSmsService = formSmsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        Assert.isInstanceOf(
                FormSmsLoginAuthenticationToken.class,
                authentication,
                () ->
                        messages.getMessage(
                                "CaptchaAuthenticationProvider.onlySupports",
                                "Only CaptchaAuthenticationToken is supported"));

        FormSmsLoginAuthenticationToken unAuthenticationToken =
                (FormSmsLoginAuthenticationToken) authentication;

        String phone = unAuthenticationToken.getName();
        String rawCode = (String) unAuthenticationToken.getCredentials();
        String type = unAuthenticationToken.getType();

        // 验证码校验
        if (formSmsService.verifyCaptcha(phone, rawCode)) {
            UserDetails userDetails = formSmsUserDetailsService.loadUserByPhone(phone, type);
            // TODO 此处省略对UserDetails 的可用性 是否过期  是否锁定 是否失效的检验  建议根据实际情况添加  或者在 UserDetailsService
            // 的实现中处理
            return createSuccessAuthentication(authentication, userDetails);
        } else {
            throw new BadCredentialsException("captcha is not matched");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FormSmsLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(formSmsUserDetailsService, "phoneUserDetailsService must not be null");
        Assert.notNull(formSmsService, "phoneService must not be null");
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    /**
     * 认证成功将非授信凭据转为授信凭据. 封装用户信息 角色信息。
     *
     * @param authentication the authentication
     * @param user           the user
     * @return the authentication
     */
    protected Authentication createSuccessAuthentication(
            Authentication authentication, UserDetails user) {

        Collection<? extends GrantedAuthority> authorities =
                authoritiesMapper.mapAuthorities(user.getAuthorities());

        String type = "";
        String captcha = "";
        if (authentication instanceof FormSmsLoginAuthenticationToken accountAuthenticationToken) {
            type = accountAuthenticationToken.getType();
            captcha = (String) accountAuthenticationToken.getCredentials();
        }

        FormSmsLoginAuthenticationToken authenticationToken =
                new FormSmsLoginAuthenticationToken(user, captcha, type, authorities);
        authenticationToken.setDetails(authentication.getDetails());

        return authenticationToken;
    }
}
