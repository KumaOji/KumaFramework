/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.AuthenticationException
 */
package com.kuma.boot.security.spring.exception;

import org.springframework.security.core.AuthenticationException;

public class SocialCredentialsParameterBindingFailedException
extends AuthenticationException {
    public SocialCredentialsParameterBindingFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SocialCredentialsParameterBindingFailedException(String msg) {
        super(msg);
    }
}

