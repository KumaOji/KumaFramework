//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.hutool.renderer;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.GifCaptcha;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.AbstractGraphicRenderer;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCategory;
import java.time.Duration;

public class GifCaptchaRenderer extends AbstractGraphicRenderer {
    public GifCaptchaRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public GifCaptchaRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    public Metadata draw() {
        GifCaptcha gifCaptcha = CaptchaUtil.createGifCaptcha(this.getWidth(), this.getHeight(), this.getLength());
        gifCaptcha.setFont(this.getFont());
        Metadata metadata = new Metadata();
        metadata.setGraphicImageBase64(gifCaptcha.getImageBase64Data());
        metadata.setCharacters(gifCaptcha.getCode());
        return metadata;
    }

    public String getCategory() {
        return CaptchaCategory.HUTOOL_GIF.getConstant();
    }
}
