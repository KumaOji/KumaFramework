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

package com.kuma.boot.security.spring.authentication.login.form.captcha;

import com.kuma.boot.captcha.support.core.dto.Verification;
import com.kuma.boot.captcha.support.core.exception.CaptchaHasExpiredException;
import com.kuma.boot.captcha.support.core.exception.CaptchaIsEmptyException;
import com.kuma.boot.captcha.support.core.exception.CaptchaMismatchException;
import com.kuma.boot.captcha.support.core.exception.CaptchaParameterIllegalException;
import com.kuma.boot.security.spring.authentication.login.form.FormLoginWebAuthenticationDetails;
import com.kuma.boot.security.spring.authentication.login.form.captcha.service.FormCaptchaCheckService;
import com.kuma.boot.security.spring.authentication.login.form.captcha.service.FormCaptchaUserDetailsService;
import com.kuma.boot.security.spring.exception.OAuth2CaptchaArgumentIllegalException;
import com.kuma.boot.security.spring.exception.OAuth2CaptchaHasExpiredException;
import com.kuma.boot.security.spring.exception.OAuth2CaptchaIsEmptyException;
import com.kuma.boot.security.spring.exception.OAuth2CaptchaMismatchException;
import org.apache.commons.lang3.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * <p>OAuth2 (Security) 表单登录 Provider </p>
 * <p>
 * 扩展的OAuth2表单登录Provider，以支持表单登录的验证码
 *
 * @see DaoAuthenticationProvider
 * @since : 2022/4/12 10:21
 */
public class FormCaptchaLoginAuthenticationProvider extends DaoAuthenticationProvider {

    private static final Logger log =
            LoggerFactory.getLogger(FormCaptchaLoginAuthenticationProvider.class);

    private FormCaptchaCheckService formCaptchaCheckService;
    private FormCaptchaUserDetailsService formCaptchaUserDetailsService;

    public FormCaptchaLoginAuthenticationProvider(
            UserDetailsService userDetailsService,
            FormCaptchaCheckService formCaptchaCheckService,
            FormCaptchaUserDetailsService formCaptchaUserDetailsService) {
        super(userDetailsService);
        this.formCaptchaCheckService = formCaptchaCheckService;
        this.formCaptchaUserDetailsService = formCaptchaUserDetailsService;
    }

    public FormCaptchaLoginAuthenticationProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService,
            FormCaptchaCheckService formCaptchaCheckService,
            FormCaptchaUserDetailsService formCaptchaUserDetailsService) {
        super(userDetailsService);
        this.formCaptchaCheckService = formCaptchaCheckService;
        this.formCaptchaUserDetailsService = formCaptchaUserDetailsService;
    }

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        Object details = authentication.getDetails();

        if (ObjectUtils.isNotEmpty(details)
                && details
                instanceof
                FormLoginWebAuthenticationDetails formLoginWebAuthenticationDetails) {
            if (!formLoginWebAuthenticationDetails.getClosed()) {
                String code = formLoginWebAuthenticationDetails.getCode();
                String category = formLoginWebAuthenticationDetails.getCategory();
                String identity = formLoginWebAuthenticationDetails.getIdentity();

                if (StringUtils.isBlank(code)) {
                    throw new OAuth2CaptchaIsEmptyException("Captcha is empty.");
                }

                try {
                    // todo 需要修改
                    Verification verification = new Verification();
                    verification.setCharacters(code);
                    verification.setCategory(category);
                    verification.setIdentity(identity);
                    formCaptchaCheckService.verifyCaptcha(code);

                    formCaptchaUserDetailsService.loadUserByUsername(identity, category);
                } catch (CaptchaParameterIllegalException e) {
                    throw new OAuth2CaptchaArgumentIllegalException("Captcha argument is illegal!");
                } catch (CaptchaHasExpiredException e) {
                    throw new OAuth2CaptchaHasExpiredException("Captcha is expired!");
                } catch (CaptchaMismatchException e) {
                    throw new OAuth2CaptchaMismatchException("Captcha is mismatch!");
                } catch (CaptchaIsEmptyException e) {
                    throw new OAuth2CaptchaIsEmptyException("Captcha is empty!");
                }
            }
        }

        super.additionalAuthenticationChecks(userDetails, authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 返回true后才会执行上面的authenticate方法,这步能确保authentication能正确转换类型
        boolean supports =
                (FormCaptchaLoginAuthenticationToken.class.isAssignableFrom(authentication));
        log.info("Form Login Authentication is supports! [{}]", supports);
        return supports;
    }

    public FormCaptchaCheckService getFormCaptchaCheckService() {
        return formCaptchaCheckService;
    }

    public void setFormCaptchaCheckService(FormCaptchaCheckService formCaptchaCheckService) {
        this.formCaptchaCheckService = formCaptchaCheckService;
    }

    public FormCaptchaUserDetailsService getFormCaptchaUserDetailsService() {
        return formCaptchaUserDetailsService;
    }

    public void setFormCaptchaUserDetailsService(
            FormCaptchaUserDetailsService formCaptchaUserDetailsService) {
        this.formCaptchaUserDetailsService = formCaptchaUserDetailsService;
    }
}
