package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.entity.User;

public interface UserService {
    User getByUsername(String username);
    boolean checkPassword(String rawPassword, String encodedPassword);
    String encodePassword(String rawPassword);
    Long createUser(User user);
    void updateLastLoginTime(Long userId);
}
