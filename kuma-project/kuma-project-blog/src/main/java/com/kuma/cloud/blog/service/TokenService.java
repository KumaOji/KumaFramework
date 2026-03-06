package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.vo.LoginResponse;

public interface TokenService {
    void saveToken(String token, LoginResponse loginResponse, long expireSeconds);
    LoginResponse getLoginResponseByToken(String token);
    boolean validateToken(String token);
    void deleteToken(String token);
    void refreshToken(String token, long expireSeconds);
}
