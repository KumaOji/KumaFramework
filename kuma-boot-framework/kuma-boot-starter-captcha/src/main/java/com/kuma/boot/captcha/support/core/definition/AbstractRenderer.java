//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.definition;

import cn.hutool.core.img.ImgUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.properties.CaptchaProperties;
import com.kuma.boot.captcha.support.core.provider.ResourceProvider;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ObjectUtils;

public abstract class AbstractRenderer implements Renderer {
    protected static final String BASE64_PNG_IMAGE_PREFIX = "data:image/png;base64,";
    protected static final String BASE64_GIF_IMAGE_PREFIX = "data:image/gif;base64,";
    private ResourceProvider resourceProvider;
    private RedisRepository redisRepository;
    private String cacheName;
    private Duration expire;

    public AbstractRenderer(RedisRepository redisRepository, String cacheName) {
        this.redisRepository = redisRepository;
        this.cacheName = cacheName;
        this.expire = Duration.ofMinutes(3L);
    }

    public AbstractRenderer(RedisRepository redisRepository, String cacheName, Duration expire) {
        this.redisRepository = redisRepository;
        this.expire = expire;
        this.cacheName = cacheName;
    }

    public void setResourceProvider(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    public ResourceProvider getResourceProvider() {
        return this.resourceProvider;
    }

    protected CaptchaProperties getCaptchaProperties() {
        return this.getResourceProvider().getCaptchaProperties();
    }

    protected String getBase64ImagePrefix() {
        return "data:image/png;base64,";
    }

    protected String toBase64(BufferedImage bufferedImage) {
        String image = ImgUtil.toBase64(bufferedImage, "png");
        String var10000 = this.getBase64ImagePrefix();
        return var10000 + image;
    }

    public Duration getExpire() {
        return this.expire;
    }

    public void setExpire(Duration expire) {
        this.expire = expire;
    }

    public boolean check(String key, Object value) {
        if (ObjectUtils.isEmpty(value)) {
            throw new RuntimeException("Parameter Stamp value is null");
        } else {
            Object storedStamp = this.get(key);
            if (ObjectUtils.isEmpty(storedStamp)) {
                throw new RuntimeException("Stamp is invalid!");
            } else if (ObjectUtils.notEqual(storedStamp, value)) {
                throw new RuntimeException("Stamp is mismathch!");
            } else {
                return true;
            }
        }
    }

    public Object get(String key) {
        return this.getCache().get(key);
    }

    public void delete(String key) {
        this.getCache().del(new String[]{key});
    }

    public void put(String key, Object value, long expireAfterWrite, TimeUnit timeUnit) {
        this.getCache().setExpire(key, value, expireAfterWrite, timeUnit);
    }

    public RedisRepository getCache() {
        return this.redisRepository;
    }

    public void setRedisRepository(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public String getCacheName() {
        return this.cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }
}
