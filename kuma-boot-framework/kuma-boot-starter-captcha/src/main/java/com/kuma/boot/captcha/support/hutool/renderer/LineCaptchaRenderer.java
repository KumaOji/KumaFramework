//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.hutool.renderer;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.AbstractGraphicRenderer;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCategory;
import java.time.Duration;

public class LineCaptchaRenderer extends AbstractGraphicRenderer {
    public LineCaptchaRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public LineCaptchaRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    public Metadata draw() {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(this.getWidth(), this.getHeight(), this.getLength(), 150);
        lineCaptcha.setFont(this.getFont());
        Metadata metadata = new Metadata();
        metadata.setGraphicImageBase64(lineCaptcha.getImageBase64Data());
        metadata.setCharacters(lineCaptcha.getCode());
        return metadata;
    }

    public String getCategory() {
        return CaptchaCategory.HUTOOL_LINE.getConstant();
    }
}
