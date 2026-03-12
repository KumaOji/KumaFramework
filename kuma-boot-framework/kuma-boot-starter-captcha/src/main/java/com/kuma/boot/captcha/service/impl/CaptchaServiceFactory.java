//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.service.impl;

import com.kuma.boot.captcha.service.CaptchaCacheService;
import com.kuma.boot.captcha.service.CaptchaService;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

public class CaptchaServiceFactory {
    public static volatile Map<String, CaptchaService> instances = new HashMap();
    public static volatile Map<String, CaptchaCacheService> cacheService = new HashMap();

    public static CaptchaService getInstance(Properties config) {
        String captchaType = config.getProperty("captcha.type", "default");
        CaptchaService ret = (CaptchaService)instances.get(captchaType);
        if (ret == null) {
            throw new RuntimeException("unsupported-[captcha.type]=" + captchaType);
        } else {
            ret.init(config);
            return ret;
        }
    }

    public static CaptchaCacheService getCache(String cacheType) {
        return (CaptchaCacheService)cacheService.get(cacheType);
    }

    static {
        for(CaptchaCacheService item : ServiceLoader.load(CaptchaCacheService.class)) {
            cacheService.put(item.type(), item);
        }

        for(CaptchaService item : ServiceLoader.load(CaptchaService.class)) {
            instances.put(item.captchaType(), item);
        }

    }
}
