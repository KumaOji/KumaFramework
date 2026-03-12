//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.hutool.renderer;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.AbstractGraphicRenderer;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCategory;
import java.time.Duration;

public class CircleCaptchaRenderer extends AbstractGraphicRenderer {
    public CircleCaptchaRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public CircleCaptchaRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    public Metadata draw() {
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(this.getWidth(), this.getHeight(), this.getLength(), 20);
        circleCaptcha.setFont(this.getFont());
        Metadata metadata = new Metadata();
        metadata.setGraphicImageBase64(circleCaptcha.getImageBase64Data());
        metadata.setCharacters(circleCaptcha.getCode());
        return metadata;
    }

    public String getCategory() {
        return CaptchaCategory.HUTOOL_CIRCLE.getConstant();
    }
}
