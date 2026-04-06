package com.kuma.cloud.blog.service.impl;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import com.kuma.boot.totp.exceptions.QrGenerationException;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.mapper.UserMapper;
import com.kuma.cloud.blog.service.UserService;
import com.kuma.boot.totp.code.DefaultCodeGenerator;
import com.kuma.boot.totp.code.DefaultCodeVerifier;
import com.kuma.boot.totp.code.HashingAlgorithm;
import com.kuma.boot.totp.qr.QrData;
import com.kuma.boot.totp.qr.QrDataFactory;
import com.kuma.boot.totp.qr.ZxingPngQrGenerator;
import com.kuma.boot.totp.secret.DefaultSecretGenerator;
import com.kuma.boot.totp.time.SystemTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.kuma.boot.totp.util.Utils.getDataUriForImage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String setupTotp(Long userId, String issuer) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        String secret = new DefaultSecretGenerator(64).generate();
        User update = new User();
        update.setId(userId);
        update.setTotpSecret(secret);
        update.setTotpEnabled(0);
        update.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(update);

        try {
            QrDataFactory factory = new QrDataFactory(HashingAlgorithm.SHA256, 6, 30);
            QrData data = factory.newBuilder()
                    .label(user.getUsername())
                    .secret(secret)
                    .issuer(issuer)
                    .build();
            ZxingPngQrGenerator generator = new ZxingPngQrGenerator();
            return getDataUriForImage(generator.generate(data), generator.getImageMimeType());
        } catch (QrGenerationException e) {
            throw new BusinessException("二维码生成失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableTotp(Long userId, String code) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (user.getTotpSecret() == null) throw new BusinessException("请先调用 setup 接口生成二维码");
        if (!verifyTotp(user.getTotpSecret(), code)) throw new BusinessException("动态码错误");

        User update = new User();
        update.setId(userId);
        update.setTotpEnabled(1);
        update.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableTotp(Long userId, String code) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (user.getTotpEnabled() == null || user.getTotpEnabled() != 1) throw new BusinessException("TOTP 未启用");
        if (!verifyTotp(user.getTotpSecret(), code)) throw new BusinessException("动态码错误");

        User update = new User();
        update.setId(userId);
        update.setTotpEnabled(0);
        update.setTotpSecret(null);
        update.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(update);
    }

    @Override
    public boolean verifyTotp(String secret, String code) {
        if (secret == null || code == null) return false;
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(
                new DefaultCodeGenerator(HashingAlgorithm.SHA256, 6),
                new SystemTimeProvider());
        return verifier.isValidCode(secret, code);
    }
}
