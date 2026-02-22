/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.zhyd.oauth.model.AuthUser
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.web.context.request.ServletWebRequest
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.service;

import com.kuma.boot.security.spring.enums.ErrorCodeEnum;
import com.kuma.boot.security.spring.exception.RegisterUserFailureException;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.ServletWebRequest;

public interface UserDetailsRegisterService {
    default public UserDetails registerUser(String mobile) throws RegisterUserFailureException {
        throw new RegisterUserFailureException(ErrorCodeEnum.USER_REGISTER_FAILURE, null);
    }

    default public UserDetails registerUser(ServletWebRequest request) throws RegisterUserFailureException {
        throw new RegisterUserFailureException(ErrorCodeEnum.USER_REGISTER_FAILURE, null);
    }

    default public UserDetails registerUser(@NonNull AuthUser authUser, @NonNull String username, @NonNull String defaultAuthority) throws RegisterUserFailureException {
        return this.registerUser(authUser, username, defaultAuthority, null);
    }

    default public UserDetails registerUser(@NonNull AuthUser authUser, @NonNull String username, @NonNull String defaultAuthority, @Nullable String decodeState) throws RegisterUserFailureException {
        throw new RegisterUserFailureException(ErrorCodeEnum.USER_REGISTER_FAILURE, null);
    }
}

