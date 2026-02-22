/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.common.JsonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.security.justauth.justauth.ConnectionData
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  jakarta.servlet.http.HttpServletRequest
 *  me.zhyd.oauth.cache.AuthStateCache
 *  me.zhyd.oauth.config.AuthSource
 *  me.zhyd.oauth.model.AuthUser
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.security.authentication.AccountExpiredException
 *  org.springframework.security.authentication.AnonymousAuthenticationToken
 *  org.springframework.security.authentication.AuthenticationProvider
 *  org.springframework.security.authentication.CredentialsExpiredException
 *  org.springframework.security.authentication.DisabledException
 *  org.springframework.security.authentication.InternalAuthenticationServiceException
 *  org.springframework.security.authentication.LockedException
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.AuthenticationException
 *  org.springframework.security.core.SpringSecurityMessageSource
 *  org.springframework.security.core.authority.AuthorityUtils
 *  org.springframework.security.core.context.SecurityContextHolder
 *  org.springframework.security.core.userdetails.UserCache
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UserDetailsChecker
 *  org.springframework.security.core.userdetails.cache.NullUserCache
 *  org.springframework.security.oauth2.core.OAuth2AccessToken
 *  org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth;

import com.kuma.boot.common.utils.common.JsonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.justauth.justauth.ConnectionData;
import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.Auth2UserService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.ConnectionService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.JustAuthUserDetailsService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.TemporaryUser;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.converter.AuthenticationToUserDetailsConverter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class JustAuthLoginAuthenticationProvider
implements AuthenticationProvider {
    private final Auth2UserService userService;
    private final JustAuthUserDetailsService umsUserDetailsService;
    private final ConnectionService connectionService;
    private final ExecutorService updateConnectionTaskExecutor;
    private final Boolean autoSignUp;
    private final String temporaryUserAuthorities;
    private final String temporaryUserPassword;
    private final AuthenticationToUserDetailsConverter authenticationToUserDetailsConverter;
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private UserCache userCache = new NullUserCache();
    private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

    public JustAuthLoginAuthenticationProvider(Auth2UserService userService, ConnectionService connectionService, JustAuthUserDetailsService umsUserDetailsService, ExecutorService updateConnectionTaskExecutor, Boolean autoSignUp, String temporaryUserAuthorities, String temporaryUserPassword, @Autowired(required=false) AuthenticationToUserDetailsConverter authenticationToUserDetailsConverter) {
        Assert.notNull((Object)updateConnectionTaskExecutor, (String)"updateConnectionTaskExecutor cannot be null");
        Assert.notNull((Object)userService, (String)"userService cannot be null");
        Assert.notNull((Object)connectionService, (String)"connectionService cannot be null");
        Assert.notNull((Object)umsUserDetailsService, (String)"umsUserDetailsService cannot be null");
        Assert.notNull((Object)autoSignUp, (String)"autoSignUp cannot be null");
        Assert.notNull((Object)temporaryUserAuthorities, (String)"temporaryUserAuthorities cannot be null");
        Assert.notNull((Object)temporaryUserPassword, (String)"temporaryUserPassword cannot be null");
        this.authenticationToUserDetailsConverter = authenticationToUserDetailsConverter;
        this.updateConnectionTaskExecutor = updateConnectionTaskExecutor;
        this.connectionService = connectionService;
        this.userService = userService;
        this.umsUserDetailsService = umsUserDetailsService;
        this.autoSignUp = autoSignUp;
        this.temporaryUserAuthorities = temporaryUserAuthorities;
        this.temporaryUserPassword = temporaryUserPassword;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails;
        boolean cacheWasUsed;
        Object principal;
        Authentication authenticationToken;
        String providerId;
        Auth2DefaultRequest auth2DefaultRequest;
        JustAuthLoginAuthenticationToken loginToken;
        block20: {
            String userId;
            loginToken = (JustAuthLoginAuthenticationToken)authentication;
            auth2DefaultRequest = loginToken.getAuth2DefaultRequest();
            HttpServletRequest request = loginToken.getRequest();
            String encodeState = request.getParameter("state");
            AuthUser authUser = this.userService.loadUser(auth2DefaultRequest, request);
            String providerUserId = authUser.getUuid();
            providerId = auth2DefaultRequest.getProviderId();
            List<ConnectionData> connectionDataList = this.connectionService.findConnectionByProviderIdAndProviderUserId(providerId, providerUserId);
            authenticationToken = SecurityContextHolder.getContext().getAuthentication();
            principal = null;
            if (authenticationToken != null && authenticationToken.isAuthenticated() && !(authenticationToken instanceof AnonymousAuthenticationToken)) {
                if (authenticationToken instanceof AbstractOAuth2TokenAuthenticationToken) {
                    if (Objects.isNull(this.authenticationToUserDetailsConverter)) {
                        throw new InternalAuthenticationServiceException("AuthenticationToUserDetailsConverter cannot be null");
                    }
                    try {
                        AbstractOAuth2TokenAuthenticationToken auth2AccessTokenAbstractOAuth2TokenAuthenticationToken = (AbstractOAuth2TokenAuthenticationToken)authenticationToken;
                        principal = this.authenticationToUserDetailsConverter.convert((AbstractOAuth2TokenAuthenticationToken<OAuth2AccessToken>)auth2AccessTokenAbstractOAuth2TokenAuthenticationToken);
                    }
                    catch (IllegalArgumentException e) {
                        throw new InternalAuthenticationServiceException("AbstractOAuth2TokenAuthenticationToken convert to UserDetails error", (Throwable)e);
                    }
                } else {
                    principal = authenticationToken.getPrincipal();
                }
            }
            cacheWasUsed = false;
            userDetails = null;
            if (CollectionUtils.isEmpty(connectionDataList)) {
                if (principal == null) {
                    userDetails = this.autoSignUp.booleanValue() ? this.connectionService.signUp(authUser, providerId, encodeState) : TemporaryUser.builder().username(authUser.getUsername() + "_" + providerId + "_" + providerUserId).password("{noop}" + this.temporaryUserPassword).authUser(authUser).encodeState(encodeState).disabled(false).accountExpired(false).accountLocked(false).credentialsExpired(false).authorities(AuthorityUtils.commaSeparatedStringToAuthorityList((String)this.temporaryUserAuthorities)).build();
                    break block20;
                } else {
                    if (principal instanceof UserDetails && !(principal instanceof TemporaryUser)) {
                        this.connectionService.binding((UserDetails)principal, authUser, providerId);
                        return authenticationToken;
                    }
                    throw new InternalAuthenticationServiceException("principal is TemporaryUser or not UserDetails");
                }
            }
            ConnectionData connectionData = null;
            if (principal instanceof UserDetails) {
                userDetails = (UserDetails)principal;
                userId = userDetails.getUsername();
                for (ConnectionData data : connectionDataList) {
                    if (!userId.equals(data.getUserId())) continue;
                    connectionData = data;
                    break;
                }
                if (connectionData == null) {
                    userDetails = null;
                    principal = null;
                }
            }
            if (userDetails == null) {
                connectionData = connectionDataList.get(0);
                userId = connectionData.getUserId();
                userDetails = this.userCache.getUserFromCache(userId);
                cacheWasUsed = true;
                if (userDetails == null) {
                    cacheWasUsed = false;
                    userDetails = this.umsUserDetailsService.loadUserByUserId(userId);
                }
            }
            this.asyncUpdateUserConnectionAndToken(authUser, connectionData);
        }
        Auth2DefaultRequest.removeStateCacheOfSessionCache((AuthStateCache)auth2DefaultRequest.getAuthStateCache(), (AuthSource)auth2DefaultRequest.getAuthSource());
        if (principal != null && !(principal instanceof TemporaryUser)) {
            return authenticationToken;
        }
        try {
            this.preAuthenticationChecks.check(userDetails);
            this.additionalAuthenticationChecks(userDetails, (JustAuthLoginAuthenticationToken)authentication);
        }
        catch (AuthenticationException exception) {
            if (!cacheWasUsed) {
                throw exception;
            }
            cacheWasUsed = false;
            userDetails = this.umsUserDetailsService.loadUserByUserId(userDetails.getUsername());
            this.preAuthenticationChecks.check(userDetails);
            this.additionalAuthenticationChecks(userDetails, (JustAuthLoginAuthenticationToken)authentication);
        }
        this.postAuthenticationChecks.check(userDetails);
        if (!cacheWasUsed) {
            this.userCache.putUserInCache(userDetails);
        }
        JustAuthAuthenticationToken justAuthAuthenticationToken = new JustAuthAuthenticationToken(userDetails, userDetails.getAuthorities(), providerId);
        justAuthAuthenticationToken.setDetails(loginToken.getDetails());
        return justAuthAuthenticationToken;
    }

    private void asyncUpdateUserConnectionAndToken(AuthUser authUser, ConnectionData connectionData) {
        try {
            this.updateConnectionTaskExecutor.execute(() -> {
                try {
                    this.connectionService.updateUserConnectionAndAuthToken(authUser, connectionData);
                }
                catch (Exception e) {
                    String msg = String.format("\u5f02\u6b65\u66f4\u65b0\u7b2c\u4e09\u65b9\u6388\u6743\u767b\u5f55\u7528\u6237\u4fe1\u606f\u4e0e token \u4fe1\u606f\u5931\u8d25: AuthUser=%s, ConnectionData=%s, error=%s", JsonUtils.toJson((Object)authUser), JsonUtils.toJson((Object)connectionData), e.getMessage());
                    LogUtils.error((String)msg, (Object[])new Object[]{e});
                }
            });
        }
        catch (NullPointerException | RejectedExecutionException e) {
            LogUtils.error((String)String.format("\u5f02\u6b65\u66f4\u65b0\u7b2c\u4e09\u65b9\u6388\u6743\u767b\u5f55\u7528\u6237\u4fe1\u606f\u4e0e token \u4fe1\u606f\u5931\u8d25: %s, \u518d\u6b21\u540c\u6b65\u66f4\u65b0", e.getMessage()), (Object[])new Object[]{e});
            try {
                this.connectionService.updateUserConnectionAndAuthToken(authUser, connectionData);
            }
            catch (Exception ex) {
                String msg = String.format("\u540c\u6b65\u66f4\u65b0\u7b2c\u4e09\u65b9\u6388\u6743\u767b\u5f55\u7528\u6237\u4fe1\u606f\u4e0e token \u4fe1\u606f\u5931\u8d25: AuthUser=%s, ConnectionData=%s, error=%s", JsonUtils.toJson((Object)authUser), JsonUtils.toJson((Object)connectionData), e.getMessage());
                LogUtils.error((String)msg, (Object[])new Object[]{e});
            }
        }
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails, JustAuthLoginAuthenticationToken authentication) throws AuthenticationException {
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    protected UserDetailsChecker getPreAuthenticationChecks() {
        return this.preAuthenticationChecks;
    }

    public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
        this.preAuthenticationChecks = preAuthenticationChecks;
    }

    protected UserDetailsChecker getPostAuthenticationChecks() {
        return this.postAuthenticationChecks;
    }

    public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    public boolean supports(Class<?> authentication) {
        return JustAuthLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private class DefaultPreAuthenticationChecks
    implements UserDetailsChecker {
        private DefaultPreAuthenticationChecks() {
        }

        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                LogUtils.debug((String)"User account is locked", (Object[])new Object[0]);
                throw new LockedException(JustAuthLoginAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
            }
            if (!user.isEnabled()) {
                LogUtils.debug((String)"User account is disabled", (Object[])new Object[0]);
                throw new DisabledException(JustAuthLoginAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
            }
            if (!user.isAccountNonExpired()) {
                LogUtils.debug((String)"User account is expired", (Object[])new Object[0]);
                throw new AccountExpiredException(JustAuthLoginAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
            }
        }
    }

    private class DefaultPostAuthenticationChecks
    implements UserDetailsChecker {
        private DefaultPostAuthenticationChecks() {
        }

        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                LogUtils.debug((String)"User account credentials have expired", (Object[])new Object[0]);
                throw new CredentialsExpiredException(JustAuthLoginAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"));
            }
        }
    }
}

