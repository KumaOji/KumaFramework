//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.service;

public interface CaptchaCacheService {
    void set(String key, String value, long expiresInSeconds);

    boolean exists(String key);

    void delete(String key);

    String get(String key);

    String type();

    default Long increment(String key, long val) {
        return 0L;
    }
}
