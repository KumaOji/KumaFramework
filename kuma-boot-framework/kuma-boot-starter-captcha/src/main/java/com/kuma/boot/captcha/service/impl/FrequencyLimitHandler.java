/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 */
package com.kuma.boot.captcha.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.captcha.model.Captcha;
import com.kuma.boot.captcha.model.CaptchaCodeEnum;
import com.kuma.boot.captcha.model.CaptchaException;
import com.kuma.boot.captcha.service.CaptchaCacheService;
import java.util.Objects;
import java.util.Properties;

public interface FrequencyLimitHandler {
    public static final String LIMIT_KEY = "AJ.CAPTCHA.REQ.LIMIT-%s-%s";

    public void validateGet(Captcha var1);

    public void validateCheck(Captcha var1);

    public void validateVerify(Captcha var1);

    public static class DefaultLimitHandler
    implements FrequencyLimitHandler {
        private Properties config;
        private CaptchaCacheService cacheService;

        public DefaultLimitHandler(Properties config, CaptchaCacheService cacheService) {
            this.config = config;
            this.cacheService = cacheService;
        }

        private String getClientCId(Captcha input, String type) {
            return String.format(FrequencyLimitHandler.LIMIT_KEY, type, input.getClientUid());
        }

        @Override
        public void validateGet(Captcha captcha) {
            if (StrUtil.isEmpty((CharSequence)captcha.getClientUid())) {
                throw new CaptchaException("\u5ba2\u6237\u7aef\u8eab\u4efd\u6807\u8bc6\u4e0d\u80fd\u4e3a\u7a7a");
            }
            String lockKey = this.getClientCId(captcha, "LOCK");
            if (Objects.nonNull(this.cacheService.get(lockKey))) {
                throw new CaptchaException(CaptchaCodeEnum.API_REQ_LOCK_GET_ERROR);
            }
            String getKey = this.getClientCId(captcha, "GET");
            String getCnts = this.cacheService.get(getKey);
            if (Objects.isNull(getCnts)) {
                this.cacheService.set(getKey, "1", 60L);
                getCnts = "1";
            }
            this.cacheService.increment(getKey, 1L);
            if (Long.parseLong(getCnts) > Long.parseLong(this.config.getProperty("captcha.req.get.minute.limit", "120"))) {
                throw new CaptchaException(CaptchaCodeEnum.API_REQ_LIMIT_GET_ERROR);
            }
            String failKey = this.getClientCId(captcha, "FAIL");
            String failCnts = this.cacheService.get(failKey);
            if (Objects.isNull(failCnts)) {
                return;
            }
            if (Long.parseLong(failCnts) > Long.parseLong(this.config.getProperty("captcha.req.get.lock.limit", "5"))) {
                this.cacheService.set(lockKey, "1", Long.parseLong(this.config.getProperty("captcha.req.get.lock.seconds", "300")));
                throw new CaptchaException(CaptchaCodeEnum.API_REQ_LOCK_GET_ERROR);
            }
        }

        @Override
        public void validateCheck(Captcha captcha) {
            if (StrUtil.isEmpty((CharSequence)captcha.getClientUid())) {
                throw new CaptchaException("\u5ba2\u6237\u7aef\u8eab\u4efd\u6807\u8bc6\u4e0d\u80fd\u4e3a\u7a7a");
            }
            String key = this.getClientCId(captcha, "CHECK");
            String v = this.cacheService.get(key);
            if (Objects.isNull(v)) {
                this.cacheService.set(key, "1", 60L);
                v = "1";
            }
            this.cacheService.increment(key, 1L);
            if (Long.parseLong(v) > Long.parseLong(this.config.getProperty("captcha.req.check.minute.limit", "600"))) {
                throw new CaptchaException(CaptchaCodeEnum.API_REQ_LIMIT_CHECK_ERROR);
            }
        }

        @Override
        public void validateVerify(Captcha captcha) {
            String key = this.getClientCId(captcha, "VERIFY");
            String v = this.cacheService.get(key);
            if (Objects.isNull(v)) {
                this.cacheService.set(key, "1", 60L);
                v = "1";
            }
            this.cacheService.increment(key, 1L);
            if (Long.parseLong(v) > Long.parseLong(this.config.getProperty("captcha.req.verify.minute.limit", "600"))) {
                throw new CaptchaException(CaptchaCodeEnum.API_REQ_LIMIT_VERIFY_ERROR);
            }
        }
    }
}

