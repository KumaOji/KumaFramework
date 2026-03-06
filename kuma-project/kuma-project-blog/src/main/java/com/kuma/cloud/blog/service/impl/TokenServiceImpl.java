package com.kuma.cloud.blog.service.impl;

import com.kuma.cloud.blog.domain.vo.LoginResponse;
import com.kuma.cloud.blog.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private static final String TOKEN_PREFIX = "blog:token:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveToken(String token, LoginResponse loginResponse, long expireSeconds) {
        String tokenKey = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(tokenKey, loginResponse, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public LoginResponse getLoginResponseByToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        Object value = redisTemplate.opsForValue().get(tokenKey);
        if (value instanceof LoginResponse lr) return lr;
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
    }

    @Override
    public void deleteToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        redisTemplate.delete(tokenKey);
    }

    @Override
    public void refreshToken(String token, long expireSeconds) {
        LoginResponse lr = getLoginResponseByToken(token);
        if (lr != null) {
            saveToken(token, lr, expireSeconds);
        }
    }
}
