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

package com.kuma.boot.security.spring.aot;

import static org.springframework.aot.hint.MemberCategory.ACCESS_DECLARED_FIELDS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;

import com.kuma.boot.security.spring.authentication.filter.ExtensionLoginRefreshTokenFilter;
import com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.face.FaceAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.gestures.GesturesAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.MfaAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.authentication.MfaAuthenticationProvider;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.OneClickLoginAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.QrcodeAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.sms.SmsAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.WechatMiniAppAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.WechatMiniAppPreAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.wechatmp.WechatMpAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.form.captcha.FormCaptchaLoginAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.form.qrcode.FormQrcodeAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.form.sms.FormSmsLoginAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.social.justauth.filter.JsonRequestFilter;
import com.kuma.boot.security.spring.authentication.login.social.justauth.filter.login.Auth2LoginAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.social.justauth.filter.redirect.Auth2DefaultRequestRedirectFilter;
import com.kuma.boot.security.spring.authentication.response.denied.JsonAccessDeniedHandler;
import com.kuma.boot.security.spring.authentication.response.entrypoint.JsonAuthenticationEntryPoint;
import com.kuma.boot.security.spring.authentication.response.failure.JsonExtensionLoginAuthenticationFailureHandler;
import com.kuma.boot.security.spring.authentication.response.success.JsonExtensionLoginAuthenticationSuccessHandler;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthorizationProperties;
import com.kuma.boot.security.spring.autoconfigure.properties.SecurityProperties;
import com.kuma.boot.security.spring.authorization.SecurityAuthorizationManager;
import com.kuma.boot.security.spring.authorization.SecurityMatcherConfigurer;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/** Native-image reflection hints for kuma security filters and configuration properties. */
public class SecuritySpringRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ReflectionHints reflection = hints.reflection();

        for (Class<?> type :
                new Class<?>[] {
                    OAuth2AuthenticationProperties.class,
                    OAuth2AuthorizationProperties.class,
                    SecurityProperties.class,
                    JsonAuthenticationEntryPoint.class,
                    JsonAccessDeniedHandler.class,
                    JsonExtensionLoginAuthenticationSuccessHandler.class,
                    JsonExtensionLoginAuthenticationFailureHandler.class,
                    SecurityAuthorizationManager.class,
                    SecurityMatcherConfigurer.class,
                    ExtensionLoginRefreshTokenFilter.class,
                    MfaAuthenticationFilter.class,
                    MfaAuthenticationProvider.class,
                    AccountAuthenticationFilter.class,
                    CaptchaAuthenticationFilter.class,
                    EmailAuthenticationFilter.class,
                    FaceAuthenticationFilter.class,
                    FingerprintAuthenticationFilter.class,
                    GesturesAuthenticationFilter.class,
                    OneClickLoginAuthenticationFilter.class,
                    QrcodeAuthenticationFilter.class,
                    SmsAuthenticationFilter.class,
                    WechatMiniAppAuthenticationFilter.class,
                    WechatMiniAppPreAuthenticationFilter.class,
                    WechatMpAuthenticationFilter.class,
                    FormCaptchaLoginAuthenticationFilter.class,
                    FormQrcodeAuthenticationFilter.class,
                    FormSmsLoginAuthenticationFilter.class,
                    JsonRequestFilter.class,
                    Auth2LoginAuthenticationFilter.class,
                    Auth2DefaultRequestRedirectFilter.class,
                }) {
            reflection.registerType(
                    type, INVOKE_PUBLIC_METHODS, INVOKE_DECLARED_CONSTRUCTORS, ACCESS_DECLARED_FIELDS);
        }
    }
}
