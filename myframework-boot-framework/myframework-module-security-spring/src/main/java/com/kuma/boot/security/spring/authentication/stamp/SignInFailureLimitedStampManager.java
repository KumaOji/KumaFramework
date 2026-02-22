/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.captcha.support.core.definition.domain.Metadata
 *  com.kuma.boot.captcha.support.core.dto.Captcha
 *  com.kuma.boot.captcha.support.core.dto.Verification
 *  org.apache.commons.lang3.ObjectUtils
 *  org.dromara.hutool.crypto.SecureUtil
 *  org.springframework.beans.factory.InitializingBean
 */
package com.kuma.boot.security.spring.authentication.stamp;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.dto.Captcha;
import com.kuma.boot.captcha.support.core.dto.Verification;
import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import java.time.Duration;
import org.apache.commons.lang3.ObjectUtils;
import org.dromara.hutool.crypto.SecureUtil;
import org.springframework.beans.factory.InitializingBean;

public class SignInFailureLimitedStampManager
extends AbstractCountStampManager
implements InitializingBean {
    private final OAuth2AuthenticationProperties authenticationProperties;

    public SignInFailureLimitedStampManager(RedisRepository redisRepository, OAuth2AuthenticationProperties authenticationProperties) {
        super(redisRepository, "cache:token:sign_in:failure_limited:");
        this.authenticationProperties = authenticationProperties;
    }

    public SignInFailureLimitedStampManager(RedisRepository redisRepository, Duration expire, OAuth2AuthenticationProperties authenticationProperties) {
        super(redisRepository, "cache:token:sign_in:failure_limited:", expire);
        this.authenticationProperties = authenticationProperties;
    }

    public Long nextStamp(String key) {
        return 1L;
    }

    public void afterPropertiesSet() throws Exception {
        super.setExpire(this.authenticationProperties.getSignInFailureLimited().getExpire());
    }

    public OAuth2AuthenticationProperties getAuthenticationProperties() {
        return this.authenticationProperties;
    }

    public SignInErrorStatus errorStatus(String username) {
        int maxTimes = this.authenticationProperties.getSignInFailureLimited().getMaxTimes();
        Long storedTimes = (Long)this.get(SecureUtil.md5((String)username));
        int errorTimes = 0;
        if (ObjectUtils.isNotEmpty((Object)storedTimes)) {
            errorTimes = storedTimes.intValue();
        }
        int remainTimes = maxTimes;
        if (errorTimes != 0) {
            remainTimes = maxTimes - errorTimes;
        }
        boolean isLocked = false;
        if (errorTimes == maxTimes) {
            isLocked = true;
        }
        SignInErrorStatus status = new SignInErrorStatus();
        status.setErrorTimes(errorTimes);
        status.setRemainTimes(remainTimes);
        status.setLocked(isLocked);
        return status;
    }

    public Metadata draw() {
        return null;
    }

    public Captcha getCapcha(String key) {
        return null;
    }

    public boolean verify(Verification verification) {
        return false;
    }

    public String getCategory() {
        return null;
    }
}

