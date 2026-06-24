package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.vo.LoginVO;

public interface TokenService {
    void saveToken(String token, LoginVO loginResponse, long expireSeconds);
    LoginVO getLoginResponseByToken(String token);
    boolean validateToken(String token);
    void deleteToken(String token);
    void refreshToken(String token, long expireSeconds);
}
