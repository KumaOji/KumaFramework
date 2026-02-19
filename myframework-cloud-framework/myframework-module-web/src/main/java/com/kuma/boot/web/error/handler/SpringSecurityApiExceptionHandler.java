/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 *  org.springframework.security.access.AccessDeniedException
 *  org.springframework.security.authentication.AccountExpiredException
 *  org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
 *  org.springframework.security.authentication.AuthenticationServiceException
 *  org.springframework.security.authentication.BadCredentialsException
 *  org.springframework.security.authentication.DisabledException
 *  org.springframework.security.authentication.InsufficientAuthenticationException
 *  org.springframework.security.authentication.LockedException
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 */
package com.kuma.boot.web.error.handler;

import com.kuma.boot.web.error.ApiErrorResponse;
import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SpringSecurityApiExceptionHandler
extends AbstractApiExceptionHandler {
    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_TO_STATUS_MAPPING = new HashMap<Class<? extends Exception>, HttpStatus>();

    public SpringSecurityApiExceptionHandler(ErrorHandlingProperties properties, HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper) {
        super(httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Override
    public boolean canHandle(Throwable exception) {
        return EXCEPTION_TO_STATUS_MAPPING.containsKey(exception.getClass());
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {
        HttpStatus httpStatus = EXCEPTION_TO_STATUS_MAPPING.getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ApiErrorResponse(this.getHttpStatus(exception, httpStatus), this.getErrorCode(exception), this.getErrorMessage(exception));
    }

    static {
        EXCEPTION_TO_STATUS_MAPPING.put(AccessDeniedException.class, HttpStatus.FORBIDDEN);
        EXCEPTION_TO_STATUS_MAPPING.put(AccountExpiredException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_TO_STATUS_MAPPING.put(AuthenticationCredentialsNotFoundException.class, HttpStatus.UNAUTHORIZED);
        EXCEPTION_TO_STATUS_MAPPING.put(AuthenticationServiceException.class, HttpStatus.INTERNAL_SERVER_ERROR);
        EXCEPTION_TO_STATUS_MAPPING.put(BadCredentialsException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_TO_STATUS_MAPPING.put(UsernameNotFoundException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_TO_STATUS_MAPPING.put(InsufficientAuthenticationException.class, HttpStatus.UNAUTHORIZED);
        EXCEPTION_TO_STATUS_MAPPING.put(LockedException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_TO_STATUS_MAPPING.put(DisabledException.class, HttpStatus.BAD_REQUEST);
    }
}

