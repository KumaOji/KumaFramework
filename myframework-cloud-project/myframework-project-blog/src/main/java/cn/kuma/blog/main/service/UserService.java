package cn.kuma.blog.main.service;

import cn.kuma.blog.main.domain.entity.User;

/**
 * 用户服务接口
 *
 * @author Kuma
 * @version 1.0
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User getByUsername(String username);

    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean checkPassword(String rawPassword, String encodedPassword);

    /**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    String encodePassword(String rawPassword);

    /**
     * 创建用户
     *
     * @param user 用户实体
     * @return 用户ID
     */
    Long createUser(User user);

    /**
     * 更新最后登录时间
     *
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);
}
