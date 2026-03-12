//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.definition;

import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.dto.Captcha;
import com.kuma.boot.captcha.support.core.dto.Verification;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ObjectUtils;

public interface Renderer {
    Metadata draw();

    Captcha getCapcha(String key);

    boolean verify(Verification verification);

    String getCategory();

    default Object create(String key) {
        return this.create(key, this.getExpire());
    }

    default Object create(String key, Duration expire) {
        return this.create(key, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    default Object create(String key, long expireAfterWrite, TimeUnit timeUnit) {
        Object value = this.nextStamp(key);
        this.put(key, value, expireAfterWrite, timeUnit);
        return value;
    }

    void put(String key, Object value, long expireAfterWrite, TimeUnit timeUnit);

    Object nextStamp(String key);

    Duration getExpire();

    Object get(String key);

    void delete(String key);

    default boolean containKey(String key) {
        Object value = this.get(key);
        return ObjectUtils.isNotEmpty(value);
    }

    boolean check(String key, Object value);

    default void put(String key, Object value, Duration expire) {
        this.put(key, value, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    default void put(String key, Object value) {
        this.put(key, value, this.getExpire());
    }
}
