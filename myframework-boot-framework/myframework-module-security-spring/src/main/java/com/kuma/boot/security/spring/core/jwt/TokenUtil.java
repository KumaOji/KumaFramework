/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.jsonwebtoken.Claims
 *  org.dromara.hutool.core.convert.ConvertUtil
 */
package com.kuma.boot.security.spring.core.jwt;

import com.kuma.boot.security.spring.core.jwt.model.AuthInfo;
import com.kuma.boot.security.spring.core.jwt.model.JwtUserInfo;
import com.kuma.boot.security.spring.core.jwt.model.Token;
import com.kuma.boot.security.spring.core.jwt.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.HashMap;
import org.dromara.hutool.core.convert.ConvertUtil;

public class TokenUtil {
    private final JwtProperties jwtProperties;

    public TokenUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public AuthInfo createAuthInfo(JwtUserInfo userInfo, Long expireMillis) {
        if (expireMillis == null || expireMillis <= 0L) {
            expireMillis = this.jwtProperties.getExpire();
        }
        HashMap<String, String> param = new HashMap<String, String>(16);
        param.put("token_type", "token");
        param.put("userid", ConvertUtil.toStr((Object)userInfo.getUserId(), (String)"0"));
        param.put("account", userInfo.getAccount());
        param.put("name", userInfo.getName());
        Token token = JwtUtil.createJwt(param, expireMillis);
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAccount(userInfo.getAccount());
        authInfo.setName(userInfo.getName());
        authInfo.setUserId(userInfo.getUserId());
        authInfo.setTokenType("token");
        authInfo.setToken(token.getToken());
        authInfo.setExpire(token.getExpire());
        authInfo.setExpiration(token.getExpiration());
        authInfo.setRefreshToken(this.createRefreshToken(userInfo).getToken());
        authInfo.setExpireMillis(expireMillis);
        return authInfo;
    }

    private Token createRefreshToken(JwtUserInfo userInfo) {
        HashMap<String, String> param = new HashMap<String, String>(16);
        param.put("token_type", "refresh_token");
        param.put("userid", ConvertUtil.toStr((Object)userInfo.getUserId(), (String)"0"));
        return JwtUtil.createJwt(param, this.jwtProperties.getRefreshExpire());
    }

    public AuthInfo getAuthInfo(String token) {
        Claims claims = JwtUtil.getClaims(token, this.jwtProperties.getAllowedClockSkewSeconds());
        String tokenType = ConvertUtil.toStr((Object)claims.get((Object)"token_type"));
        Long userId = ConvertUtil.toLong((Object)claims.get((Object)"userid"));
        String account = ConvertUtil.toStr((Object)claims.get((Object)"account"));
        String name = ConvertUtil.toStr((Object)claims.get((Object)"name"));
        Date expiration = claims.getExpiration();
        return new AuthInfo().setToken(token).setExpire(expiration != null ? expiration.getTime() : 0L).setTokenType(tokenType).setUserId(userId).setAccount(account).setName(name);
    }

    public AuthInfo parseRefreshToken(String token) {
        Claims claims = JwtUtil.parseJwt(token, this.jwtProperties.getAllowedClockSkewSeconds());
        String tokenType = ConvertUtil.toStr((Object)claims.get((Object)"token_type"));
        Long userId = ConvertUtil.toLong((Object)claims.get((Object)"userid"));
        Date expiration = claims.getExpiration();
        return new AuthInfo().setToken(token).setExpire(expiration != null ? expiration.getTime() : 0L).setTokenType(tokenType).setUserId(userId);
    }
}

