package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.User;

public interface UserService {
    User getByUsername(String username);
    User getById(Long id);

    /**
     * 分页查询用户（管理界面选人）。
     *
     * @param keyword 模糊匹配 username / nickname / email，可为空
     * @param status  按状态过滤（0=禁用 1=正常），可为空
     */
    IPage<User> pageUsers(PageQuery pageQuery, String keyword, Integer status);
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
}
