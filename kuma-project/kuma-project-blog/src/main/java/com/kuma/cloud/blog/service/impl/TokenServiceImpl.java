package com.kuma.cloud.blog.service.impl;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.cloud.blog.domain.vo.LoginResponse;
import com.kuma.cloud.blog.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private static final String TOKEN_PREFIX = "blog:token:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveToken(String token, LoginResponse loginResponse, long expireSeconds) {
        String tokenKey = TOKEN_PREFIX + token;
        String json = JacksonUtils.toJSONString(loginResponse);
        stringRedisTemplate.opsForValue().set(tokenKey, json, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public LoginResponse getLoginResponseByToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        String json = stringRedisTemplate.opsForValue().get(tokenKey);
        if (json == null || json.isBlank()) return null;
        try {
            return JacksonUtils.readValue(json, LoginResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean validateToken(String token) {
        // GET is cheaper than EXISTS: one round-trip instead of two, and caller often needs the value anyway
        return getLoginResponseByToken(token) != null;
    }

    @Override
    public void deleteToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        stringRedisTemplate.delete(tokenKey);
    }

    @Override
    public void refreshToken(String token, long expireSeconds) {
        // Just extend the TTL — no need to deserialize and re-serialize the value
        stringRedisTemplate.expire(TOKEN_PREFIX + token, expireSeconds, java.util.concurrent.TimeUnit.SECONDS);
    }
}
