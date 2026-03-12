//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.hutool.renderer;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.AbstractGraphicRenderer;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCategory;
import java.time.Duration;

public class ShearCaptchaRenderer extends AbstractGraphicRenderer {
    public ShearCaptchaRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public ShearCaptchaRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    public Metadata draw() {
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(this.getWidth(), this.getHeight(), this.getLength(), 4);
        shearCaptcha.setFont(this.getFont());
        Metadata metadata = new Metadata();
        metadata.setGraphicImageBase64(shearCaptcha.getImageBase64Data());
        metadata.setCharacters(shearCaptcha.getCode());
        return metadata;
    }

    public String getCategory() {
        return CaptchaCategory.HUTOOL_SHEAR.getConstant();
    }
}
