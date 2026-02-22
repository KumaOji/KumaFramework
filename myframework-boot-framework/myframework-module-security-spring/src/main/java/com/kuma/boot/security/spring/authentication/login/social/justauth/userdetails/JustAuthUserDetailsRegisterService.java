/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.web.context.request.ServletWebRequest
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails;

import com.kuma.boot.security.spring.enums.ErrorCodeEnum;
import com.kuma.boot.security.spring.exception.RegisterUserFailureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.ServletWebRequest;

public interface JustAuthUserDetailsRegisterService {
    default public UserDetails registerUser(String mobile) throws RegisterUserFailureException {
        throw new RegisterUserFailureException(ErrorCodeEnum.USER_REGISTER_FAILURE, null);
    }

    default public UserDetails registerUser(ServletWebRequest request) throws RegisterUserFailureException {
        throw new RegisterUserFailureException(ErrorCodeEnum.USER_REGISTER_FAILURE, null);
    }
}

