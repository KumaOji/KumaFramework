package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.common.model.domain.UserDetail;
import cn.kuma.blog.framework.util.JwtUtil;
import cn.kuma.blog.main.service.TokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * 仅基于 JWT 的 Token 服务实现（无 Redis 时使用）
 */
@Service
@ConditionalOnMissingBean(TokenService.class)
public class JwtOnlyTokenServiceImpl implements TokenService {

    @Override
    public void saveToken(String token, UserDetail userDetail, long expireSeconds) {
        // JWT 自包含，无需持久化
    }

    @Override
    public UserDetail getUserDetailByToken(String token) {
        if (JwtUtil.validateToken(token)) {
            return JwtUtil.parseToken(token);
        }
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        return JwtUtil.validateToken(token);
    }

    @Override
    public void deleteToken(String token) {
        // JWT 无状态，无法服务端失效
    }

    @Override
    public void refreshToken(String token, long expireSeconds) {
        // 无操作
    }

    @Override
    public void deleteTokensByUserId(String userId) {
        // 无操作
    }

}
