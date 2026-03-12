//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.graphic.renderer;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCategory;
import com.kuma.boot.captcha.support.graphic.definition.AbstractPngGraphicRenderer;
import java.time.Duration;

public class SpecCaptchaRenderer extends AbstractPngGraphicRenderer {
    public SpecCaptchaRenderer(RedisRepository redisRepository, String cacheName) {
        super(redisRepository, cacheName);
    }

    public SpecCaptchaRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        super(redisRepository, cacheName, expire);
    }

    public String getCategory() {
        return CaptchaCategory.SPEC.getConstant();
    }

    protected String[] getDrawCharacters() {
        return this.getCharCharacters();
    }
}
