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

package com.kuma.boot.security.spring.authentication.login.extension.mfa.configure;

import com.kuma.boot.security.spring.authentication.login.extension.mfa.MfaAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.authentication.MfaAuthenticationProvider;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.totp.DefaultTotpManager;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.totp.MfaAuthenticationManager;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.Assert;

/**
 * @author: ReLive27
 * @since: 2023/1/9 21:32
 */
public class MfaAuthenticationConfigurer
        extends AbstractHttpConfigurer<MfaAuthenticationConfigurer, HttpSecurity> {

    private MfaAuthenticationManager mfaAuthenticationManager;

    public MfaAuthenticationConfigurer mfaAuthenticationManager(
            MfaAuthenticationManager mfaAuthenticationManager) {
        Assert.notNull(mfaAuthenticationManager, "mfaAuthenticationManager can not be null");
        this.mfaAuthenticationManager = mfaAuthenticationManager;
        return this;
    }

    @Override
    public void init(HttpSecurity http){
        if (this.mfaAuthenticationManager == null) {
            this.mfaAuthenticationManager = new DefaultTotpManager();
        }
        ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
        UserDetailsService userDetailsService =
                applicationContext.getBean(UserDetailsService.class);
        http.authenticationProvider(
                this.postProcess(
                        new MfaAuthenticationProvider(
                                userDetailsService, mfaAuthenticationManager)));
        super.init(http);
    }

    @Override
    public void configure(HttpSecurity http)  {
        AuthenticationManager authenticationManager =
                http.getSharedObject(AuthenticationManager.class);
        MfaAuthenticationFilter mfaAuthenticationFilter =
                new MfaAuthenticationFilter(
                        authenticationManager,
                        PathPatternRequestMatcher.withDefaults()
                                .matcher(HttpMethod.POST, "/login"));
        http.addFilterBefore(
                this.postProcess(mfaAuthenticationFilter),
                UsernamePasswordAuthenticationFilter.class);
        super.configure(http);
    }
}
