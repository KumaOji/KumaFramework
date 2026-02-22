/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.captcha.support.core.dto.Verification
 *  com.kuma.boot.captcha.support.core.exception.CaptchaHasExpiredException
 *  com.kuma.boot.captcha.support.core.exception.CaptchaIsEmptyException
 *  com.kuma.boot.captcha.support.core.exception.CaptchaMismatchException
 *  com.kuma.boot.captcha.support.core.exception.CaptchaParameterIllegalException
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 *  org.springframework.security.authentication.dao.DaoAuthenticationProvider
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.crypto.password.PasswordEncoder
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public class FormCaptchaLoginAuthenticationProvider
extends DaoAuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(FormCaptchaLoginAuthenticationProvider.class);
    private FormCaptchaCheckService formCaptchaCheckService;
    private FormCaptchaUserDetailsService formCaptchaUserDetailsService;

    public FormCaptchaLoginAuthenticationProvider(FormCaptchaCheckService formCaptchaCheckService, FormCaptchaUserDetailsService formCaptchaUserDetailsService) {
        this.formCaptchaCheckService = formCaptchaCheckService;
        this.formCaptchaUserDetailsService = formCaptchaUserDetailsService;
    }

    public FormCaptchaLoginAuthenticationProvider(PasswordEncoder passwordEncoder, FormCaptchaCheckService formCaptchaCheckService, FormCaptchaUserDetailsService formCaptchaUserDetailsService) {
        super(passwordEncoder);
        this.formCaptchaCheckService = formCaptchaCheckService;
        this.formCaptchaUserDetailsService = formCaptchaUserDetailsService;
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        FormLoginWebAuthenticationDetails formLoginWebAuthenticationDetails;
        Object details = authentication.getDetails();
        if (ObjectUtils.isNotEmpty((Object)details) && details instanceof FormLoginWebAuthenticationDetails && !(formLoginWebAuthenticationDetails = (FormLoginWebAuthenticationDetails)((Object)details)).getClosed().booleanValue()) {
            String code = formLoginWebAuthenticationDetails.getCode();
            String category = formLoginWebAuthenticationDetails.getCategory();
            String identity = formLoginWebAuthenticationDetails.getIdentity();
            if (StringUtils.isBlank((CharSequence)code)) {
                throw new OAuth2CaptchaIsEmptyException("Captcha is empty.");
            }
            try {
                Verification verification = new Verification();
                verification.setCharacters(code);
                verification.setCategory(category);
                verification.setIdentity(identity);
                this.formCaptchaCheckService.verifyCaptcha(code);
                this.formCaptchaUserDetailsService.loadUserByUsername(identity, category);
            }
            catch (CaptchaParameterIllegalException e) {
                throw new OAuth2CaptchaArgumentIllegalException("Captcha argument is illegal!");
            }
            catch (CaptchaHasExpiredException e) {
                throw new OAuth2CaptchaHasExpiredException("Captcha is expired!");
            }
            catch (CaptchaMismatchException e) {
                throw new OAuth2CaptchaMismatchException("Captcha is mismatch!");
            }
            catch (CaptchaIsEmptyException e) {
                throw new OAuth2CaptchaIsEmptyException("Captcha is empty!");
            }
        }
        super.additionalAuthenticationChecks(userDetails, authentication);
    }

    public boolean supports(Class<?> authentication) {
        boolean supports = FormCaptchaLoginAuthenticationToken.class.isAssignableFrom(authentication);
        log.info("Form Login Authentication is supports! [{}]", (Object)supports);
        return supports;
    }

    public FormCaptchaCheckService getFormCaptchaCheckService() {
        return this.formCaptchaCheckService;
    }

    public void setFormCaptchaCheckService(FormCaptchaCheckService formCaptchaCheckService) {
        this.formCaptchaCheckService = formCaptchaCheckService;
    }

    public FormCaptchaUserDetailsService getFormCaptchaUserDetailsService() {
        return this.formCaptchaUserDetailsService;
    }

    public void setFormCaptchaUserDetailsService(FormCaptchaUserDetailsService formCaptchaUserDetailsService) {
        this.formCaptchaUserDetailsService = formCaptchaUserDetailsService;
    }
}

