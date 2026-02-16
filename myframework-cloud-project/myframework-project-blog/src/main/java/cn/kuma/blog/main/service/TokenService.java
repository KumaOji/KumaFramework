package cn.kuma.blog.main.service;

import cn.kuma.blog.common.model.domain.UserDetail;

/**
 * Token 服务接口
 * 用于管理 Redis 中的 Token
 *
 * @author Kuma
 * @version 1.0
 */
public interface TokenService {

    /**
     * 存储 Token 到 Redis
     *
     * @param token      Token 字符串
     * @param userDetail 用户信息
     * @param expireSeconds 过期时间（秒）
     */
    void saveToken(String token, UserDetail userDetail, long expireSeconds);

    /**
     * 从 Redis 获取用户信息
     *
     * @param token Token 字符串
     * @return 用户信息，不存在则返回 null
     */
    UserDetail getUserDetailByToken(String token);

    /**
     * 验证 Token 是否存在且有效
     *
     * @param token Token 字符串
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 删除 Token
     *
     * @param token Token 字符串
     */
    void deleteToken(String token);

    /**
     * 刷新 Token 过期时间
     *
     * @param token        Token 字符串
     * @param expireSeconds 新的过期时间（秒）
     */
    void refreshToken(String token, long expireSeconds);

    /**
     * 根据用户ID删除该用户的所有 Token（用于强制下线）
     *
     * @param userId 用户ID
     */
    void deleteTokensByUserId(String userId);
}
