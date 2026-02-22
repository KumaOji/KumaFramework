/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.common.JsonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.security.justauth.justauth.AuthTokenPo
 *  com.kuma.boot.security.justauth.justauth.ConnectionData
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  com.kuma.boot.security.justauth.justauth.util.JustAuthUtil
 *  me.zhyd.oauth.model.AuthToken
 *  me.zhyd.oauth.model.AuthUser
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.security.authentication.AnonymousAuthenticationToken
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.context.SecurityContextHolder
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.service;

import com.kuma.boot.common.utils.common.JsonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.justauth.justauth.AuthTokenPo;
import com.kuma.boot.security.justauth.justauth.ConnectionData;
import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import com.kuma.boot.security.justauth.justauth.util.JustAuthUtil;
import com.kuma.boot.security.spring.authentication.login.social.justauth.entity.ConnectionDto;
import com.kuma.boot.security.spring.authentication.login.social.justauth.entity.ConnectionKey;
import com.kuma.boot.security.spring.authentication.login.social.justauth.properties.JustAuthProperties;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.UsersConnectionRepository;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.UsersConnectionTokenRepository;
import com.kuma.boot.security.spring.authentication.login.social.justauth.repository.exception.UpdateConnectionException;
import com.kuma.boot.security.spring.enums.ErrorCodeEnum;
import com.kuma.boot.security.spring.exception.RegisterUserFailureException;
import com.kuma.boot.security.spring.exception.UnBindingException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class DefaultConnectionService
implements ConnectionService {
    private final Integer timeout;
    private final UmsUserDetailsService userDetailsService;
    private final String defaultAuthorities;
    private final UsersConnectionRepository usersConnectionRepository;
    private final UsersConnectionTokenRepository usersConnectionTokenRepository;
    private final Auth2StateCoder auth2StateCoder;

    public DefaultConnectionService(UmsUserDetailsService userDetailsService, JustAuthProperties justAuthProperties, UsersConnectionRepository usersConnectionRepository, UsersConnectionTokenRepository usersConnectionTokenRepository, Auth2StateCoder auth2StateCoder) {
        this.userDetailsService = userDetailsService;
        this.defaultAuthorities = justAuthProperties.getDefaultAuthorities();
        this.usersConnectionRepository = usersConnectionRepository;
        this.usersConnectionTokenRepository = usersConnectionTokenRepository;
        this.timeout = justAuthProperties.getProxy().getHttpConfig().getTimeout();
        this.auth2StateCoder = auth2StateCoder;
    }

    @Override
    @NonNull
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRES_NEW)
    public UserDetails signUp(@NonNull AuthUser authUser, @NonNull String providerId, @NonNull String encodeState) throws RegisterUserFailureException {
        String username = authUser.getUsername();
        String[] usernames = this.userDetailsService.generateUsernames(authUser);
        try {
            username = null;
            List<Boolean> existedByUserIds = this.userDetailsService.existedByUsernames(usernames);
            int len = existedByUserIds.size();
            for (int i = 0; i < len; ++i) {
                if (existedByUserIds.get(i).booleanValue()) continue;
                username = usernames[i];
                break;
            }
            if (username == null) {
                throw new RegisterUserFailureException(ErrorCodeEnum.USERNAME_USED, authUser.getUsername());
            }
            String decodeState = this.auth2StateCoder != null ? this.auth2StateCoder.decode(encodeState) : encodeState;
            UserDetails userDetails = this.userDetailsService.registerUser(authUser, username, this.defaultAuthorities, decodeState);
            this.registerConnection(providerId, authUser, userDetails);
            return userDetails;
        }
        catch (Exception e) {
            LogUtils.error((String)String.format("OAuth2\u81ea\u52a8\u6ce8\u518c\u5931\u8d25: error=%s, username=%s, authUser=%s", e.getMessage(), username, JsonUtils.toJson((Object)authUser)), (Object[])new Object[]{e});
            throw new RegisterUserFailureException(ErrorCodeEnum.USER_REGISTER_FAILURE, username);
        }
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    public void updateUserConnectionAndAuthToken(@NonNull AuthUser authUser, @NonNull ConnectionData data) throws UpdateConnectionException {
        ConnectionData connectionData = null;
        try {
            AuthToken token = authUser.getToken();
            AuthTokenPo authToken = JustAuthUtil.getAuthTokenPo((AuthToken)token, (String)data.getProviderId(), (Integer)this.timeout);
            authToken.setId(data.getTokenId());
            Auth2DefaultRequest.expireIn2Timestamp((Integer)this.timeout, (Integer)token.getExpireIn(), (AuthTokenPo)authToken);
            connectionData = JustAuthUtil.getConnectionData((String)data.getProviderId(), (AuthUser)authUser, (String)data.getUserId(), (AuthTokenPo)authToken);
            connectionData.setUserId(data.getUserId());
            connectionData.setTokenId(data.getTokenId());
            this.usersConnectionRepository.updateConnection(connectionData);
            if (Objects.nonNull(this.usersConnectionTokenRepository)) {
                this.usersConnectionTokenRepository.updateAuthToken(authToken);
            }
        }
        catch (Exception e) {
            LogUtils.error((String)("\u66f4\u65b0\u7b2c\u4e09\u65b9\u7528\u6237\u4fe1\u606f\u5f02\u5e38: " + e.getMessage()), (Object[])new Object[0]);
            throw new UpdateConnectionException(ErrorCodeEnum.UPDATE_CONNECTION_DATA_FAILURE, connectionData, e);
        }
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    public void binding(@NonNull UserDetails principal, @NonNull AuthUser authUser, @NonNull String providerId) {
        this.registerConnection(providerId, authUser, principal);
    }

    @Override
    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
    public void unbinding(@NonNull String userId, @NonNull String providerId, @NonNull String providerUserId) {
        boolean isCurrentUserAndValid;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean bl = isCurrentUserAndValid = authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getName().equals(userId);
        if (!isCurrentUserAndValid) {
            LogUtils.warn((String)"\u7528\u6237 {} \u8fdb\u884c\u89e3\u7ed1\u64cd\u4f5c\u65f6, \u7528\u6237\u672a\u767b\u5f55\u6216\u4e0d\u662f\u5f53\u524d\u7528\u6237; userId: {}, providerId: {}, providerUserId: {}", (Object[])new Object[]{authentication.getName(), userId, providerId, providerUserId});
            throw new UnBindingException(ErrorCodeEnum.UN_BINDING_ERROR, userId);
        }
        this.usersConnectionRepository.removeConnection(userId, new ConnectionKey(providerId, providerUserId));
    }

    @Override
    @Nullable
    public List<ConnectionData> findConnectionByProviderIdAndProviderUserId(@NonNull String providerId, @NonNull String providerUserId) {
        return this.usersConnectionRepository.findConnectionByProviderIdAndProviderUserId(providerId, providerUserId);
    }

    @Override
    @NonNull
    public MultiValueMap<String, ConnectionDto> listAllConnections(@NonNull String userId) {
        MultiValueMap<String, ConnectionData> allConnections = this.usersConnectionRepository.findAllConnections(userId);
        Set entrySet = allConnections.entrySet();
        LinkedMultiValueMap connectionMap = new LinkedMultiValueMap(allConnections.size());
        for (Map.Entry entry : entrySet) {
            List connectionDtoList = ((List)entry.getValue()).stream().map(data -> ConnectionDto.builder().tokenId(data.getTokenId()).providerId(data.getProviderId()).providerUserId(data.getProviderUserId()).build()).collect(Collectors.toList());
            connectionMap.put((Object)((String)entry.getKey()), connectionDtoList);
        }
        return connectionMap;
    }

    private void registerConnection(@NonNull String providerId, @NonNull AuthUser authUser, @NonNull UserDetails userDetails) throws RegisterUserFailureException {
        AuthToken token = authUser.getToken();
        AuthTokenPo authToken = JustAuthUtil.getAuthTokenPo((AuthToken)token, (String)providerId, (Integer)this.timeout);
        Auth2DefaultRequest.expireIn2Timestamp((Integer)this.timeout, (Integer)token.getExpireIn(), (AuthTokenPo)authToken);
        try {
            if (Objects.nonNull(this.usersConnectionTokenRepository)) {
                this.usersConnectionTokenRepository.saveAuthToken(authToken);
            }
            this.addConnectionData(providerId, authUser, userDetails.getUsername(), authToken);
        }
        catch (Exception e) {
            if (authToken.getId() == null) {
                try {
                    if (Objects.nonNull(this.usersConnectionTokenRepository)) {
                        this.usersConnectionTokenRepository.saveAuthToken(authToken);
                    }
                    this.addConnectionData(providerId, authUser, userDetails.getUsername(), authToken);
                }
                catch (Exception ex) {
                    String msg = String.format("\u7b2c\u4e09\u65b9\u6388\u6743\u767b\u5f55\u81ea\u52a8\u6ce8\u518c\u65f6: \u672c\u5730\u8d26\u6237\u6ce8\u518c\u6210\u529f, %s, \u6dfb\u52a0\u7b2c\u4e09\u65b9\u6388\u6743\u767b\u5f55\u4fe1\u606f\u5931\u8d25: %s", userDetails, JsonUtils.toJson((Object)authUser));
                    LogUtils.error((String)msg, (Object[])new Object[]{e});
                    throw new RegisterUserFailureException(ErrorCodeEnum.USER_REGISTER_OAUTH2_FAILURE, ex, userDetails.getUsername());
                }
            }
            try {
                this.addConnectionData(providerId, authUser, userDetails.getUsername(), authToken);
            }
            catch (Exception exception) {
                String msg = String.format("\u7b2c\u4e09\u65b9\u6388\u6743\u767b\u5f55\u81ea\u52a8\u6ce8\u518c\u65f6: \u672c\u5730\u8d26\u6237\u6ce8\u518c\u6210\u529f, %s, \u6dfb\u52a0\u7b2c\u4e09\u65b9\u6388\u6743\u767b\u5f55\u4fe1\u606f\u5931\u8d25: %s, \u4f46 AuthToken \u80fd\u6210\u529f\u6267\u884c sql, \u4f46\u5df2\u56de\u6eda: %s", userDetails, authUser.getRawUserInfo(), JsonUtils.toJson((Object)authToken));
                LogUtils.error((String)msg, (Object[])new Object[]{e});
                throw new RegisterUserFailureException(ErrorCodeEnum.USER_REGISTER_OAUTH2_FAILURE, userDetails.getUsername());
            }
        }
    }

    private void addConnectionData(@NonNull String providerId, @NonNull AuthUser authUser, @NonNull String userId, @NonNull AuthTokenPo authToken) {
        ConnectionData connectionData = JustAuthUtil.getConnectionData((String)providerId, (AuthUser)authUser, (String)userId, (AuthTokenPo)authToken);
        this.usersConnectionRepository.addConnection(connectionData);
    }
}

