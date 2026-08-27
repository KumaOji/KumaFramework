package com.kuma.cloud.uaa.support;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.captcha.SpecCaptcha;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.cloud.uaa.config.UaaProperties;
import com.kuma.cloud.uaa.domain.vo.CaptchaVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

/**
 * 登录图形验证码，复用 kuma-boot-starter-captcha 的 {@link SpecCaptcha} 渲染，
 * 答案存 Redis 而不依赖 Session，便于 UAA 水平扩容。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginCaptchaService {

    private static final String KEY_PREFIX = "uaa:captcha:";

    private final RedisRepository redisRepository;
    private final UaaProperties properties;

    public CaptchaVO issue() {
        UaaProperties.Captcha config = properties.getCaptcha();
        SpecCaptcha captcha =
                new SpecCaptcha(config.getWidth(), config.getHeight(), config.getLength());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (!captcha.out(output)) {
            throw new BusinessException("验证码生成失败");
        }

        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        redisRepository.setEx(
                KEY_PREFIX + captchaKey, captcha.text().toLowerCase(), config.getTtl());

        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaKey(captchaKey);
        vo.setImage("data:image/png;base64," + Base64.getEncoder().encodeToString(output.toByteArray()));
        vo.setExpiresInSeconds(config.getTtl().toSeconds());
        return vo;
    }

    /**
     * 一次性校验：无论成功与否都立即删除，避免同一验证码被重复使用。
     */
    public boolean verify(String captchaKey, String captchaCode) {
        if (!properties.getCaptcha().isEnabled()) {
            return true;
        }
        if (!StringUtils.hasText(captchaKey) || !StringUtils.hasText(captchaCode)) {
            return false;
        }

        String redisKey = KEY_PREFIX + captchaKey;
        Object expected = redisRepository.get(redisKey);
        redisRepository.del(redisKey);
        return expected != null && expected.toString().equalsIgnoreCase(captchaCode.trim());
    }
}
