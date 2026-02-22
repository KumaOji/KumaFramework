/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.authentication.InternalAuthenticationServiceException
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UserDetailsService
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.authentication;

import com.kuma.boot.security.spring.authentication.login.extension.mfa.exception.MfaAuthenticationException;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.totp.MfaAuthenticationManager;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.userdetails.MfaUserDetails;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

public class MfaAuthenticationProvider
implements AuthenticationProvider {
    private final MfaAuthenticationManager mfaAuthenticationManager;
    private final UserDetailsService userDetailsService;

    public MfaAuthenticationProvider(UserDetailsService userDetailsService, MfaAuthenticationManager mfaAuthenticationManager) {
        Assert.notNull((Object)userDetailsService, (String)"userDetailsService cannot be null");
        Assert.notNull((Object)mfaAuthenticationManager, (String)"mfaAuthenticationManager cannot be null");
        this.userDetailsService = userDetailsService;
        this.mfaAuthenticationManager = mfaAuthenticationManager;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MfaAuthenticationToken mfaAuthenticationToken = (MfaAuthenticationToken)authentication;
        String username = mfaAuthenticationToken.getPrincipal().toString();
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        }
        if (userDetails instanceof MfaUserDetails) {
            MfaUserDetails mfaUserDetails = (MfaUserDetails)userDetails;
            if (mfaUserDetails.isEnableMfa() && !this.mfaAuthenticationManager.validCode(mfaUserDetails.getSecret(), mfaAuthenticationToken.getCredentials())) {
                throw new MfaAuthenticationException("Code verification failed", null);
            }
            return new MfaAuthenticationToken(username, mfaAuthenticationToken.getCredentials(), true);
        }
        throw new MfaAuthenticationException("MfaUserDetails must be an instance of UserDetails", null);
    }

    public boolean supports(Class<?> authentication) {
        return MfaAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

