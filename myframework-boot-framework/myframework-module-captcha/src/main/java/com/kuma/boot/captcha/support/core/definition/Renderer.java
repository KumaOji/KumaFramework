/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 */
package com.kuma.boot.captcha.support.core.definition;

import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.dto.Captcha;
import com.kuma.boot.captcha.support.core.dto.Verification;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ObjectUtils;

public interface Renderer {
    public Metadata draw();

    public Captcha getCapcha(String var1);

    public boolean verify(Verification var1);

    public String getCategory();

    default public Object create(String key) {
        return this.create(key, this.getExpire());
    }

    default public Object create(String key, Duration expire) {
        return this.create(key, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    default public Object create(String key, long expireAfterWrite, TimeUnit timeUnit) {
        Object value = this.nextStamp(key);
        this.put(key, value, expireAfterWrite, timeUnit);
        return value;
    }

    public void put(String var1, Object var2, long var3, TimeUnit var5);

    public Object nextStamp(String var1);

    public Duration getExpire();

    public Object get(String var1);

    public void delete(String var1);

    default public boolean containKey(String key) {
        Object value = this.get(key);
        return ObjectUtils.isNotEmpty((Object)value);
    }

    public boolean check(String var1, Object var2);

    default public void put(String key, Object value, Duration expire) {
        this.put(key, value, expire.toMillis(), TimeUnit.MILLISECONDS);
    }

    default public void put(String key, Object value) {
        this.put(key, value, this.getExpire());
    }
}

