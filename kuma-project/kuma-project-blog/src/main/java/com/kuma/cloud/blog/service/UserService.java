package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.entity.User;

public interface UserService {
    User getByUsername(String username);
    User getById(Long id);
    boolean checkPassword(String rawPassword, String encodedPassword);
    String encodePassword(String rawPassword);
    Long createUser(User user);
    void updateLastLoginTime(Long userId);

    /** 生成 TOTP secret，保存到 DB（尚未启用），返回 QR 码 data URI */
    String setupTotp(Long userId, String issuer);

    /** 验证 code 正确后正式启用 TOTP */
    void enableTotp(Long userId, String code);

    /** 关闭 TOTP（需先验证当前 code） */
    void disableTotp(Long userId, String code);

    /** 验证 TOTP 动态码 */
    boolean verifyTotp(String secret, String code);

    /** 根据微信 openId 查找用户，不存在则自动注册 */
    User findOrCreateByWechat(String openId, String nickname, String avatar);
}
