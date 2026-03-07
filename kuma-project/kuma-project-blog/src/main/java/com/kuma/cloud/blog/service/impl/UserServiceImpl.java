package com.kuma.cloud.blog.service.impl;

import com.kuma.boot.security.spring.utils.SecurityUtils;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.mapper.UserMapper;
import com.kuma.cloud.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;
        rawPassword = rawPassword.trim();
        encodedPassword = encodedPassword.trim();
        return SecurityUtils.validatePass(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return SecurityUtils.getPasswordEncoder().encode(rawPassword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        if (user.getPassword() != null) {
            user.setPassword(encodePassword(user.getPassword()));
        }
        if (user.getStatus() == null) user.setStatus(1);
        if (user.getIsAdmin() == null) user.setIsAdmin(0);
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}
