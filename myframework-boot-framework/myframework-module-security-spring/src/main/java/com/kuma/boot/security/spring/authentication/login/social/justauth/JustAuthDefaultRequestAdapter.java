/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.security.justauth.justauth.AuthTokenPo
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  com.xkcoding.http.exception.SimpleHttpException
 *  me.zhyd.oauth.cache.AuthStateCache
 *  me.zhyd.oauth.config.AuthConfig
 *  me.zhyd.oauth.config.AuthSource
 *  me.zhyd.oauth.enums.AuthResponseStatus
 *  me.zhyd.oauth.exception.AuthException
 *  me.zhyd.oauth.model.AuthCallback
 *  me.zhyd.oauth.model.AuthResponse
 *  me.zhyd.oauth.model.AuthToken
 *  me.zhyd.oauth.model.AuthUser
 *  me.zhyd.oauth.request.AuthDefaultRequest
 *  me.zhyd.oauth.utils.AuthChecker
 *  me.zhyd.oauth.utils.StringUtils
 *  me.zhyd.oauth.utils.UuidUtils
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.justauth.justauth.AuthTokenPo;
import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import com.xkcoding.http.exception.SimpleHttpException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.AuthChecker;
import me.zhyd.oauth.utils.UuidUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class JustAuthDefaultRequestAdapter
extends AuthDefaultRequest
implements Auth2DefaultRequest {
    private final String providerId;
    private AuthDefaultRequest authDefaultRequest;

    public JustAuthDefaultRequestAdapter(AuthConfig config, AuthSource source, AuthStateCache authStateCache) {
        super(config, source, authStateCache);
        String providerId = JustAuthRequestHolder.getProviderId(source);
        if (!StringUtils.hasText((String)providerId)) {
            throw new RuntimeException("AuthSource \u5fc5\u987b\u662f me.zhyd.oauth.config.AuthDefaultSource \u6216 top.dcenter.ums.security.core.oauth.justauth.source.AuthCustomizeSource \u5b50\u7c7b");
        }
        this.providerId = providerId;
    }

    public void setAuthDefaultRequest(AuthDefaultRequest authDefaultRequest) {
        this.authDefaultRequest = authDefaultRequest;
    }

    public String getRealState(String state) {
        if (me.zhyd.oauth.utils.StringUtils.isEmpty((String)state)) {
            state = UuidUtils.getUUID();
        }
        this.authStateCache.cache(state, state);
        return state;
    }

    public AuthResponse login(AuthCallback authCallback) {
        try {
            AuthChecker.checkCode((AuthSource)this.source, (AuthCallback)authCallback);
            if (!this.config.isIgnoreCheckState()) {
                AuthChecker.checkState((String)authCallback.getState(), (AuthSource)this.source, (AuthStateCache)this.authStateCache);
            }
            AuthToken authToken = this.getAccessToken(authCallback);
            AuthUser user = this.getUserInfo(authToken);
            return AuthResponse.builder().code(AuthResponseStatus.SUCCESS.getCode()).data((Object)user).build();
        }
        catch (Exception e) {
            LogUtils.error((String)("Failed to login with oauth authorization. error: " + e.getMessage()), (Object[])new Object[]{e});
            return Auth2DefaultRequest.responseError((Exception)e);
        }
    }

    public AuthTokenPo refreshToken(AuthTokenPo authToken) throws SimpleHttpException, AuthException {
        if (this.authDefaultRequest == null) {
            throw new RuntimeException("AuthDefaultRequest \u4e0d\u80fd\u4e3a null \u503c, \u5fc5\u987b\u901a\u8fc7\u65b9\u6cd5 setAuthDefaultRequest(AuthDefaultRequest) \u8bbe\u7f6e");
        }
        AuthResponse authResponse = this.authDefaultRequest.refresh((AuthToken)authToken);
        return Auth2DefaultRequest.getAuthTokenPo((Integer)this.config.getHttpConfig().getTimeout(), (Long)authToken.getId(), (AuthResponse)authResponse);
    }

    public AuthSource getAuthSource() {
        return this.source;
    }

    public AuthStateCache getAuthStateCache() {
        return this.authStateCache;
    }

    public AuthToken getAccessToken(AuthCallback authCallback) throws SimpleHttpException {
        try {
            Method method = this.getMethod("getAccessToken", AuthCallback.class);
            Object result = method.invoke((Object)this.authDefaultRequest, authCallback);
            return (AuthToken)result;
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            String errMsg = e.getMessage();
            if (e instanceof InvocationTargetException) {
                InvocationTargetException invocationTargetException = (InvocationTargetException)e;
                errMsg = invocationTargetException.getTargetException().getMessage();
            }
            String msg = "\u4ece\u7b2c\u4e09\u65b9\u83b7\u53d6 accessToken \u65f6\u65b9\u6cd5\u8c03\u7528\u5f02\u5e38: " + errMsg;
            throw new SimpleHttpException(msg, (Throwable)e);
        }
    }

    @Nullable
    public AuthUser getUserInfo(AuthToken authToken) throws SimpleHttpException {
        try {
            Method method = this.getMethod("getUserInfo", AuthToken.class);
            Object result = method.invoke((Object)this.authDefaultRequest, authToken);
            return (AuthUser)result;
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            String errMsg = e.getMessage();
            if (e instanceof InvocationTargetException) {
                InvocationTargetException invocationTargetException = (InvocationTargetException)e;
                errMsg = invocationTargetException.getTargetException().getMessage();
            }
            String msg = "\u4ece\u7b2c\u4e09\u65b9\u83b7\u53d6\u7528\u6237\u4fe1\u606f\u65f6\u65b9\u6cd5\u8c03\u7528\u5f02\u5e38: " + errMsg;
            throw new SimpleHttpException(msg, (Throwable)e);
        }
    }

    public String getProviderId() {
        return this.providerId;
    }

    public String authorize(String state) {
        if (this.authDefaultRequest == null) {
            throw new RuntimeException("AuthDefaultRequest \u4e0d\u80fd\u4e3a null \u503c, \u5fc5\u987b\u901a\u8fc7\u65b9\u6cd5 setAuthDefaultRequest(AuthDefaultRequest) \u8bbe\u7f6e");
        }
        return this.authDefaultRequest.authorize(state);
    }

    private Method getMethod(@NonNull String methodName, Class<?> ... parameterTypes) throws NoSuchMethodException {
        Method method = this.authDefaultRequest.getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method;
    }
}

