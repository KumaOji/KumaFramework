/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.captcha.support.core.definition.AbstractRenderer
 *  com.kuma.boot.captcha.support.core.definition.domain.Metadata
 *  com.kuma.boot.captcha.support.core.dto.Captcha
 *  com.kuma.boot.captcha.support.core.dto.Verification
 *  org.dromara.hutool.core.data.id.IdUtil
 *  org.springframework.beans.factory.InitializingBean
 */
package com.kuma.boot.security.spring.authentication.stamp;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.definition.AbstractRenderer;
import com.kuma.boot.captcha.support.core.definition.domain.Metadata;
import com.kuma.boot.captcha.support.core.dto.Captcha;
import com.kuma.boot.captcha.support.core.dto.Verification;
import com.kuma.boot.security.spring.properties.OAuth2AuthenticationProperties;
import java.time.Duration;
import org.dromara.hutool.core.data.id.IdUtil;
import org.springframework.beans.factory.InitializingBean;

public class LockedUserDetailsStampManager
extends AbstractRenderer
implements InitializingBean {
    private final OAuth2AuthenticationProperties authenticationProperties;

    public LockedUserDetailsStampManager(RedisRepository redisRepository, OAuth2AuthenticationProperties authenticationProperties) {
        super(redisRepository, "cache:token:locked:user_details:");
        this.authenticationProperties = authenticationProperties;
    }

    public LockedUserDetailsStampManager(RedisRepository redisRepository, Duration expire, OAuth2AuthenticationProperties authenticationProperties) {
        super(redisRepository, "cache:token:locked:user_details:", expire);
        this.authenticationProperties = authenticationProperties;
    }

    public String nextStamp(String key) {
        return IdUtil.fastSimpleUUID();
    }

    public void afterPropertiesSet() throws Exception {
        super.setExpire(this.authenticationProperties.getSignInFailureLimited().getExpire());
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

