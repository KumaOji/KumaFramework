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

package com.kuma.boot.security.spring.authentication.login.form.qrcode;

import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.QrcodeAuthenticationToken;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.service.FormQrcodeService;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.service.FormQrcodeUserDetailsService;
import java.util.Collection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 *
 */
public class FormQrcodeAuthenticationProvider
        implements AuthenticationProvider, InitializingBean, MessageSourceAware {

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final FormQrcodeUserDetailsService qrcodeUserDetailsService;
    private final FormQrcodeService qrcodeService;
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public FormQrcodeAuthenticationProvider(
            FormQrcodeService qrcodeService,
            FormQrcodeUserDetailsService qrcodeUserDetailsService) {
        this.qrcodeService = qrcodeService;
        this.qrcodeUserDetailsService = qrcodeUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        Assert.isInstanceOf(
                FormQrcodeAuthenticationToken.class,
                authentication,
                () ->
                        messages.getMessage(
                                "AccountVerificationAuthenticationProvider.onlySupports",
                                "Only AccountVerificationAuthenticationProvider is supported"));

        FormQrcodeAuthenticationToken unAuthenticationToken =
                (FormQrcodeAuthenticationToken) authentication;

        String username = unAuthenticationToken.getName();
        String passowrd = (String) unAuthenticationToken.getCredentials();

        qrcodeService.verifyQrcode("qrocde");

        // 验证码校验
        UserDetails userDetails = qrcodeUserDetailsService.loadUserByPhone(username);
        // 校验密码
        // TODO 此处省略对UserDetails 的可用性 是否过期  是否锁定 是否失效的检验  建议根据实际情况添加  或者在 UserDetailsService 的实现中处理
        return createSuccessAuthentication(authentication, userDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return QrcodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(qrcodeUserDetailsService, "qrcodeUserDetailsService must not be null");
        Assert.notNull(qrcodeService, "qrcodeService must not be null");
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
        QrcodeAuthenticationToken authenticationToken =
                new QrcodeAuthenticationToken(user, null, authorities);
        authenticationToken.setDetails(authentication.getDetails());

        return authenticationToken;
    }

    public GrantedAuthoritiesMapper getAuthoritiesMapper() {
        return authoritiesMapper;
    }

    public FormQrcodeUserDetailsService getQrcodeUserDetailsService() {
        return qrcodeUserDetailsService;
    }

    public FormQrcodeService getQrcodeService() {
        return qrcodeService;
    }

    public MessageSourceAccessor getMessages() {
        return messages;
    }

    public void setMessages(MessageSourceAccessor messages) {
        this.messages = messages;
    }
}
