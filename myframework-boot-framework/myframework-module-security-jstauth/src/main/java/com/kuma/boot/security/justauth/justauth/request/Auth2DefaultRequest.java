/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.justauth.justauth.request;

import com.kuma.boot.security.justauth.justauth.AuthTokenPo;
import com.kuma.boot.security.justauth.justauth.cache.AuthStateSessionCache;
import com.xkcoding.http.config.HttpConfig;
import com.xkcoding.http.exception.SimpleHttpException;
import java.time.Instant;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.StringUtils;
import me.zhyd.oauth.utils.UuidUtils;
import org.springframework.beans.BeanUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * {@link AuthDefaultRequest} 的扩展, 对外曝露接口<br>
 * 1. {@link #authorize(String)},<br>
 * 2. {@link #getAccessToken(AuthCallback)},<br>
 * 3. {@link #getUserInfo(AuthToken)},<br>
 * 4. {@link #getAuthStateCache()},<br>
 * 5. {@link #getAuthSource()},<br>
 * 6. {@link #login(AuthCallback)},<br>
 * 7. {@link #getProviderId()},<br>
 * 8. {@link #refreshToken(AuthTokenPo)},<br>
 * 10. {@link #removeStateCacheOfSessionCache(AuthStateCache, AuthSource)},<br>
 * 11. {@link #generateState()},<br>
 * 12. {@link #getAuthTokenPo(Integer, Long, AuthResponse)},<br>
 * 13. {@link #expireIn2Timestamp(Integer, Integer, AuthTokenPo)},<br>
 * 14. {@link #responseError(Exception)}<br>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/7 20:27
 */
public interface Auth2DefaultRequest {

    /**
     * 统一的登录入口。当通过{@link AuthDefaultRequest#authorize(String)}授权成功后，会跳转到调用方的相关回调方法中
     * 方法的入参可以使用{@code AuthCallback}，{@code AuthCallback}类中封装好了OAuth2授权回调所需要的参数
     *
     * @param authCallback 用于接收回调参数的实体
     * @return AuthResponse
     */
    @SuppressWarnings("rawtypes")
    AuthResponse login(AuthCallback authCallback);

    /**
     * 获取第三方 providerId
     * @return  providerId
     */
    String getProviderId();

    /**
     * 获取 {@link AuthSource}
     * @return  {@link AuthSource}
     */
    AuthSource getAuthSource();

    /**
     * 获取 {@link AuthStateCache}
     * @return  {@link AuthStateCache}
     */
    AuthStateCache getAuthStateCache();

    /**
     * 返回带{@code state}参数的授权url，授权回调时会带上这个{@code state}
     *
     * @param state state 验证授权流程的参数，可以防止csrf
     * @return 返回授权地址
     * @since 1.9.3
     */
    String authorize(String state);

    /**
     * 获取access token
     *
     * @see AuthDefaultRequest #getAccessToken(AuthCallback)
     * @see AuthDefaultRequest#authorize(String)
     * @param authCallback 授权成功后的回调参数
     * @return token
     * @throws SimpleHttpException  http 异常
     */
    AuthToken getAccessToken(AuthCallback authCallback) throws SimpleHttpException;

    /**
     * 使用token换取用户信息
     *
     * @see AuthDefaultRequest #getUserInfo(AuthToken)
     * @param authToken token信息
     * @return 用户信息
     * @throws SimpleHttpException  http 异常
     */
    AuthUser getUserInfo(AuthToken authToken) throws SimpleHttpException;

    /**
     * 刷新access token （续期）
     *
     * @param authToken     登录成功后返回的Token信息
     * @return AuthTokenPo  返回 AuthTokenPo, 获取 AuthTokenPo 失败返回 null
     * @throws SimpleHttpException  http 异常
     * @throws AuthException        不支持 refresh token
     */
    AuthTokenPo refreshToken(AuthTokenPo authToken) throws SimpleHttpException, AuthException;

    /**
     * 生成一个用户 id. 默认使用 {@link UuidUtils#getUUID()}
     * @return  返回一个用户 Id
     */
    default String generateState() {
        return UuidUtils.getUUID();
    }

    /**
     * 移除 stateCache 缓存
     * @param authStateCache    {@link AuthStateCache}
     * @param source            {@link AuthSource}
     */
    static void removeStateCacheOfSessionCache(
            @NonNull AuthStateCache authStateCache, @NonNull AuthSource source) {
        if (authStateCache instanceof AuthStateSessionCache) {
            AuthStateSessionCache stateCache = ((AuthStateSessionCache) authStateCache);
            stateCache.remove(source.getName());
        }
    }

    /**
     * {@link AuthDefaultRequest#refresh(AuthToken)} 返回的 {@link AuthResponse} 转换到 {@link AuthTokenPo}
     * @param timeout       {@link HttpConfig#getTimeout()} 返回的 {@link AuthResponse}, 毫秒
     * @param tokenId       token id
     * @param authResponse  {@link AuthDefaultRequest#refresh(AuthToken)} 返回的 {@link AuthResponse}
     * @return  AuthTokenPo
     */
    @SuppressWarnings("rawtypes")
    @NonNull
    static AuthTokenPo getAuthTokenPo(Integer timeout, Long tokenId, AuthResponse authResponse) {

        if (authResponse.ok()) {
            Object data = authResponse.getData();
            if (data instanceof AuthToken token) {
                AuthTokenPo tokenPo = new AuthTokenPo();
                tokenPo.setId(tokenId);
                BeanUtils.copyProperties(token, tokenPo);

                int expireIn = token.getExpireIn();
                // 有效期转时间戳
                expireIn2Timestamp(timeout, expireIn, tokenPo);

                return tokenPo;
            }
        }

        String msg = authResponse.getMsg();
        String s = String.valueOf(tokenId);

        throw new RuntimeException("refresh Token 刷新失败");
    }

    /**
     * 有效期转时间戳
     * @param timeout   {@link HttpConfig#getTimeout()}, 单位毫秒
     * @param expireIn  有效期
     * @param authToken {@link AuthToken}
     */
    static <T extends AuthTokenPo> void expireIn2Timestamp(
            @NonNull Integer timeout, @Nullable Integer expireIn, @NonNull T authToken) {

        authToken.setExpireTime(expireIn2Timestamp(timeout, expireIn));
    }

    /**
     * 有效期转时间戳
     * @param timeout   {@link HttpConfig#getTimeout()}, 单位毫秒
     * @param expireIn  有效期
     * @return 时间戳
     */
    static long expireIn2Timestamp(@NonNull Integer timeout, @Nullable Integer expireIn) {
        if (expireIn == null || expireIn < 1) {
            // 无过期时间, 默认设置为 -1
            return -1L;
        } else {
            // 转换为到期日期的 EpochMilli, 考虑到网络延迟, 相对于第三方的过期时间, 减去根据用户设置的
            // timeout(HttpConfigProperties.timeout) 时间,
            return Instant.now().plusSeconds(expireIn).minusMillis(timeout).toEpochMilli();
        }
    }

    /**
     * 处理{@link AuthDefaultRequest#login(AuthCallback)} 发生异常的情况，统一响应参数.
     * copy of {@link AuthDefaultRequest}
     * @see AuthDefaultRequest #responseError(Exception)
     * @param e 具体的异常
     * @return AuthResponse
     */
    @SuppressWarnings("rawtypes")
    static AuthResponse responseError(Exception e) {
        int errorCode = AuthResponseStatus.FAILURE.getCode();
        String errorMsg = e.getMessage();
        if (e instanceof AuthException authException) {
            errorCode = authException.getErrorCode();
            if (StringUtils.isNotEmpty(authException.getErrorMsg())) {
                errorMsg = authException.getErrorMsg();
            }
        }
        return AuthResponse.builder().code(errorCode).msg(errorMsg).build();
    }
}
