/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.service.impl;

import com.kuma.boot.captcha.service.CaptchaCacheService;
import com.kuma.boot.captcha.service.CaptchaService;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

public class CaptchaServiceFactory {
    public static volatile Map<String, CaptchaService> instances = new HashMap<String, CaptchaService>();
    public static volatile Map<String, CaptchaCacheService> cacheService = new HashMap<String, CaptchaCacheService>();

    public static CaptchaService getInstance(Properties config) {
        String captchaType = config.getProperty("captcha.type", "default");
        CaptchaService ret = instances.get(captchaType);
        if (ret == null) {
            throw new RuntimeException("unsupported-[captcha.type]=" + captchaType);
        }
        ret.init(config);
        return ret;
    }

    public static CaptchaCacheService getCache(String cacheType) {
        return cacheService.get(cacheType);
    }

    static {
        ServiceLoader<CaptchaCacheService> cacheServices = ServiceLoader.load(CaptchaCacheService.class);
        for (CaptchaCacheService item : cacheServices) {
            cacheService.put(item.type(), item);
        }
        ServiceLoader<CaptchaService> services = ServiceLoader.load(CaptchaService.class);
        for (CaptchaService item : services) {
            instances.put(item.captchaType(), item);
        }
    }
}

