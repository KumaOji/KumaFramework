/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.captcha.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.captcha.model.Captcha;
import com.kuma.boot.captcha.model.CaptchaCodeEnum;
import com.kuma.boot.captcha.model.CaptchaException;
import com.kuma.boot.captcha.service.CaptchaService;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Properties;

public class DefaultCaptchaServiceImpl
extends AbstractCaptchaService {
    @Override
    public String captchaType() {
        return "default";
    }

    @Override
    public void init(Properties config) {
        for (String s : CaptchaServiceFactory.instances.keySet()) {
            if (this.captchaType().equals(s)) continue;
            this.getService(s).init(config);
        }
    }

    @Override
    public void destroy(Properties config) {
        for (String s : CaptchaServiceFactory.instances.keySet()) {
            if (this.captchaType().equals(s)) continue;
            this.getService(s).destroy(config);
        }
    }

    private CaptchaService getService(String captchaType) {
        return CaptchaServiceFactory.instances.get(captchaType);
    }

    @Override
    public Captcha get(Captcha captcha) {
        if (captcha == null) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError("captchaVO"));
        }
        if (StrUtil.isEmpty((CharSequence)captcha.getCaptchaType())) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError("\u7c7b\u578b"));
        }
        return this.getService(captcha.getCaptchaType()).get(captcha);
    }

    @Override
    public Captcha check(Captcha captcha) {
        if (captcha == null) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError("captchaVO"));
        }
        if (StrUtil.isEmpty((CharSequence)captcha.getCaptchaType())) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError("\u4e8c\u6b21\u6821\u9a8c\u53c2\u6570"));
        }
        if (StrUtil.isEmpty((CharSequence)captcha.getToken())) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError("token"));
        }
        return this.getService(captcha.getCaptchaType()).check(captcha);
    }

    @Override
    public Captcha verification(Captcha captcha) {
        if (captcha == null) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError("captchaVO"));
        }
        if (StrUtil.isEmpty((CharSequence)captcha.getCaptchaVerification())) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError("\u4e8c\u6b21\u6821\u9a8c\u53c2\u6570"));
        }
        try {
            String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY, captcha.getCaptchaVerification());
            if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
                throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_INVALID);
            }
            CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
        }
        catch (Exception e) {
            LogUtils.error((String)"\u9a8c\u8bc1\u7801\u5750\u6807\u89e3\u6790\u5931\u8d25", (Object[])new Object[]{e});
            throw new CaptchaException(e.getMessage());
        }
        return captcha;
    }
}

