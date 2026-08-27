package com.kuma.cloud.uaa.service.impl;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.totp.code.CodeVerifier;
import com.kuma.boot.totp.exceptions.QrGenerationException;
import com.kuma.boot.totp.qr.QrData;
import com.kuma.boot.totp.qr.QrDataFactory;
import com.kuma.boot.totp.qr.QrGenerator;
import com.kuma.boot.totp.secret.SecretGenerator;
import com.kuma.cloud.uaa.config.UaaProperties;
import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.domain.vo.MfaBindVO;
import com.kuma.cloud.uaa.service.MfaService;
import com.kuma.cloud.uaa.service.UaaUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {

    private static final String PENDING_SECRET_KEY_PREFIX = "uaa:mfa:pending:";

    private final UaaUserService userService;
    private final SecretGenerator secretGenerator;
    private final CodeVerifier codeVerifier;
    private final QrDataFactory qrDataFactory;
    private final QrGenerator qrGenerator;
    private final RedisRepository redisRepository;
    private final UaaProperties properties;

    @Override
    public MfaBindVO startBind(String username) {
        UaaUser user = userService.requireByUsername(username);
        if (user.isMfaEnabled()) {
            throw new BusinessException("已绑定二次校验，请先解绑");
        }

        String secret = secretGenerator.generate();
        redisRepository.setEx(
                pendingKey(username), secret, properties.getMfa().getBindTtl());

        QrData qrData = qrDataFactory.newBuilder()
                .label(username)
                .secret(secret)
                .issuer(properties.getMfa().getIssuer())
                .build();

        MfaBindVO vo = new MfaBindVO();
        vo.setSecret(secret);
        vo.setOtpAuthUri(qrData.getUri());
        vo.setQrImage(renderQrImage(qrData));
        return vo;
    }

    @Override
    public void confirmBind(String username, String code) {
        UaaUser user = userService.requireByUsername(username);
        Object pending = redisRepository.get(pendingKey(username));
        if (pending == null) {
            throw new BusinessException("绑定会话已过期，请重新获取二维码");
        }
        String secret = pending.toString();
        if (!codeVerifier.isValidCode(secret, code)) {
            throw new BusinessException("动态码不正确");
        }

        userService.updateMfa(user.getId(), secret, true);
        redisRepository.del(pendingKey(username));
        log.info("用户 {} 已开启 TOTP 二次校验", username);
    }

    @Override
    public void unbind(String username, String code) {
        UaaUser user = userService.requireByUsername(username);
        if (!user.isMfaEnabled()) {
            throw new BusinessException("未绑定二次校验");
        }
        if (!codeVerifier.isValidCode(user.getMfaSecret(), code)) {
            throw new BusinessException("动态码不正确");
        }
        userService.updateMfa(user.getId(), null, false);
        log.info("用户 {} 已关闭 TOTP 二次校验", username);
    }

    @Override
    public boolean verify(UaaUser user, String code) {
        if (user == null || !user.isMfaEnabled()) {
            return true;
        }
        return StringUtils.hasText(code)
                && StringUtils.hasText(user.getMfaSecret())
                && codeVerifier.isValidCode(user.getMfaSecret(), code);
    }

    private String renderQrImage(QrData qrData) {
        try {
            byte[] image = qrGenerator.generate(qrData);
            return "data:" + qrGenerator.getImageMimeType() + ";base64,"
                    + Base64.getEncoder().encodeToString(image);
        } catch (QrGenerationException exception) {
            throw new BusinessException("二维码生成失败: " + exception.getMessage());
        }
    }

    private String pendingKey(String username) {
        return PENDING_SECRET_KEY_PREFIX + username;
    }
}
