//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.service.impl;

import com.kuma.boot.captcha.service.CaptchaCacheService;
import com.kuma.boot.captcha.util.CacheUtil;

public class CaptchaCacheServiceMemImpl implements CaptchaCacheService {
    public void set(String key, String value, long expiresInSeconds) {
        CacheUtil.set(key, value, expiresInSeconds);
    }

    public boolean exists(String key) {
        return CacheUtil.exists(key);
    }

    public void delete(String key) {
        CacheUtil.delete(key);
    }

    public String get(String key) {
        return CacheUtil.get(key);
    }

    public Long increment(String key, long val) {
        Long ret = Long.parseLong(CacheUtil.get(key)) + val;
        CacheUtil.set(key, "" + ret, 0L);
        return ret;
    }

    public String type() {
        return "local";
    }
}
