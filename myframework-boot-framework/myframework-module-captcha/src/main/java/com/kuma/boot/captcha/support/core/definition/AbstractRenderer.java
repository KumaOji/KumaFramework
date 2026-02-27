/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.img.ImgUtil
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  org.apache.commons.lang3.ObjectUtils
 */
package com.kuma.boot.captcha.support.core.definition;

import cn.hutool.core.img.ImgUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.properties.CaptchaProperties;
import com.kuma.boot.captcha.support.core.provider.ResourceProvider;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ObjectUtils;

public abstract class AbstractRenderer
implements Renderer {
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
        return BASE64_PNG_IMAGE_PREFIX;
    }

    protected String toBase64(BufferedImage bufferedImage) {
        String image = ImgUtil.toBase64((Image)bufferedImage, (String)"png");
        return this.getBase64ImagePrefix() + image;
    }

    @Override
    public Duration getExpire() {
        return this.expire;
    }

    public void setExpire(Duration expire) {
        this.expire = expire;
    }

    @Override
    public boolean check(String key, Object value) {
        if (ObjectUtils.isEmpty((Object)value)) {
            throw new RuntimeException("Parameter Stamp value is null");
        }
        Object storedStamp = this.get(key);
        if (ObjectUtils.isEmpty((Object)storedStamp)) {
            throw new RuntimeException("Stamp is invalid!");
        }
        if (ObjectUtils.notEqual((Object)storedStamp, (Object)value)) {
            throw new RuntimeException("Stamp is mismathch!");
        }
        return true;
    }

    @Override
    public Object get(String key) {
        return this.getCache().get(key);
    }

    @Override
    public void delete(String key) {
        this.getCache().del(new String[]{key});
    }

    @Override
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

