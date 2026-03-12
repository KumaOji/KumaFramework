package com.kuma.boot.captcha.support.core.definition;

import cn.hutool.core.img.ImgUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.exception.CaptchaHasExpiredException;
import com.kuma.boot.captcha.support.core.exception.CaptchaIsEmptyException;
import com.kuma.boot.captcha.support.core.exception.CaptchaMismatchException;
import com.kuma.boot.captcha.support.core.properties.CaptchaProperties;
import com.kuma.boot.captcha.support.core.provider.ResourceProvider;
import org.apache.commons.lang3.ObjectUtils;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
        return resourceProvider;
    }

    protected CaptchaProperties getCaptchaProperties() {
        return getResourceProvider().getCaptchaProperties();
    }

    protected String getBase64ImagePrefix() {
        return BASE64_PNG_IMAGE_PREFIX;
    }

    protected String toBase64(BufferedImage bufferedImage) {
        return getBase64ImagePrefix() + ImgUtil.toBase64(bufferedImage, "png");
    }

    public Duration getExpire() {
        return expire;
    }

    public void setExpire(Duration expire) {
        this.expire = expire;
    }

    @Override
    public boolean check(String key, Object value) {
        if (ObjectUtils.isEmpty(value)) {
            throw new CaptchaIsEmptyException("Parameter stamp value is null");
        }
        Object storedStamp = get(key);
        if (ObjectUtils.isEmpty(storedStamp)) {
            throw new CaptchaHasExpiredException("Stamp is invalid!");
        }
        if (ObjectUtils.notEqual(storedStamp, value)) {
            throw new CaptchaMismatchException("Stamp mismatch!");
        }
        return true;
    }

    @Override
    public Object get(String key) {
        return getCache().get(key);
    }

    @Override
    public void delete(String key) {
        getCache().del(new String[]{key});
    }

    @Override
    public void put(String key, Object value, long expireAfterWrite, TimeUnit timeUnit) {
        getCache().setExpire(key, value, expireAfterWrite, timeUnit);
    }

    public RedisRepository getCache() {
        return redisRepository;
    }

    public void setRedisRepository(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }
}
