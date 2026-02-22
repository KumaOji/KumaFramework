/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.security.authentication.AccountExpiredException
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.authentication.BadCredentialsException
 *  org.springframework.security.authentication.CredentialsExpiredException
 *  org.springframework.security.authentication.DisabledException
 *  org.springframework.security.authentication.InternalAuthenticationServiceException
 *  org.springframework.security.authentication.LockedException
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.core.SpringSecurityMessageSource
 *  org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
 *  org.springframework.security.core.authority.mapping.NullAuthoritiesMapper
 *  org.springframework.security.core.userdetails.UserCache
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UserDetailsChecker
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 *  org.springframework.security.core.userdetails.cache.NullUserCache
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.fingerprint;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.authentication.login.extension.fingerprint.service.FingerprintUserDetailsService;
import java.util.Collection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

public class FingerprintAuthenticationProvider
implements AuthenticationProvider,
InitializingBean,
MessageSourceAware {
    private volatile String userNotFoundEncodedPassword;
    private final UserCache userCache = new NullUserCache();
    private final UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks(this);
    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks(this);
    private PasswordEncoder passwordEncoder;
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final FingerprintUserDetailsService fingerprintUserDetailsService;
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public FingerprintAuthenticationProvider(FingerprintUserDetailsService fingerprintUserDetailsService) {
        this.fingerprintUserDetailsService = fingerprintUserDetailsService;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(FingerprintAuthenticationToken.class, (Object)authentication, () -> this.messages.getMessage("FingerprintAuthenticationToken.onlySupports", "Only FingerprintAuthenticationToken is supported"));
        String username = this.determineUsername(authentication);
        boolean cacheWasUsed = true;
        UserDetails user = this.userCache.getUserFromCache(username);
        if (user == null) {
            cacheWasUsed = false;
            try {
                user = this.retrieveUser(username, (FingerprintAuthenticationToken)authentication);
            }
            catch (UsernameNotFoundException ex) {
                LogUtils.error((String)("Failed to find user '" + username + "'"), (Object[])new Object[0]);
                throw new BadCredentialsException("\u7528\u6237\u4e0d\u5b58\u5728");
            }
            Assert.notNull((Object)user, (String)"retrieveUser returned null - a violation of the interface contract");
        }
        try {
            this.preAuthenticationChecks.check(user);
            this.additionalAuthenticationChecks(user, (FingerprintAuthenticationToken)authentication);
        }
        catch (AuthenticationException ex) {
            if (!cacheWasUsed) {
                throw ex;
            }
            cacheWasUsed = false;
            user = this.retrieveUser(username, (FingerprintAuthenticationToken)authentication);
            this.preAuthenticationChecks.check(user);
            this.additionalAuthenticationChecks(user, (FingerprintAuthenticationToken)authentication);
        }
        this.postAuthenticationChecks.check(user);
        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }
        return this.createSuccessAuthentication(user.getUsername(), authentication, user);
    }

    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails userDetails) {
        Collection authorities = this.authoritiesMapper.mapAuthorities(userDetails.getAuthorities());
        String fingerPrint = "";
        if (authentication instanceof FingerprintAuthenticationToken) {
            FingerprintAuthenticationToken fingerprintAuthenticationToken = (FingerprintAuthenticationToken)authentication;
            fingerPrint = fingerprintAuthenticationToken.getFingerPrint();
        }
        FingerprintAuthenticationToken authenticationToken = new FingerprintAuthenticationToken(principal, fingerPrint, authorities);
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails, FingerprintAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            LogUtils.error((String)"Failed to authenticate since no credentials provided", (Object[])new Object[0]);
            throw new BadCredentialsException("\u7528\u6237\u5bc6\u7801\u9519\u8bef");
        }
    }

    protected final UserDetails retrieveUser(String username, FingerprintAuthenticationToken authentication) throws AuthenticationException {
        this.prepareTimingAttackProtection();
        try {
            UserDetails loadedUser = this.fingerprintUserDetailsService.loadUserByFingerprint((String)authentication.getPrincipal());
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException("\u7528\u6237\u4e0d\u5b58\u5728");
            }
            return loadedUser;
        }
        catch (UsernameNotFoundException ex) {
            this.mitigateAgainstTimingAttack(authentication);
            throw ex;
        }
        catch (InternalAuthenticationServiceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), (Throwable)ex);
        }
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode((CharSequence)USER_NOT_FOUND_PASSWORD);
        }
    }

    private void mitigateAgainstTimingAttack(FingerprintAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches((CharSequence)presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    private String determineUsername(Authentication authentication) {
        return authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();
    }

    public boolean supports(Class<?> authentication) {
        return FingerprintAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull((Object)this.fingerprintUserDetailsService, (String)"fingerprintUserDetailsService must not be null");
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        Collection authorities = this.authoritiesMapper.mapAuthorities(user.getAuthorities());
        FingerprintAuthenticationToken authenticationToken = new FingerprintAuthenticationToken(user, null, authorities);
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }

    private class DefaultPreAuthenticationChecks
    implements UserDetailsChecker {
        private DefaultPreAuthenticationChecks(FingerprintAuthenticationProvider fingerprintAuthenticationProvider) {
        }

        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                LogUtils.error((String)"Failed to authenticate since user account is locked", (Object[])new Object[0]);
                throw new LockedException("\u7528\u6237\u5df2\u88ab\u9501\u5b9a");
            }
            if (!user.isEnabled()) {
                LogUtils.error((String)"Failed to authenticate since user account is disabled", (Object[])new Object[0]);
                throw new DisabledException("\u7528\u6237\u672a\u542f\u7528");
            }
            if (!user.isAccountNonExpired()) {
                LogUtils.error((String)"Failed to authenticate since user account has expired", (Object[])new Object[0]);
                throw new AccountExpiredException("\u7528\u6237\u8d26\u53f7\u5df2\u8fc7\u671f");
            }
        }
    }

    private class DefaultPostAuthenticationChecks
    implements UserDetailsChecker {
        private DefaultPostAuthenticationChecks(FingerprintAuthenticationProvider fingerprintAuthenticationProvider) {
        }

        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                LogUtils.error((String)"Failed to authenticate since user account credentials have expired", (Object[])new Object[0]);
                throw new CredentialsExpiredException("\u7528\u6237\u8d26\u53f7\u5df2\u8fc7\u671f");
            }
        }
    }
}

