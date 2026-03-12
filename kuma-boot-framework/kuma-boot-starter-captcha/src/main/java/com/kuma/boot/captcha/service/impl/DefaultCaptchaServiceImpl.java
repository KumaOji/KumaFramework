//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.captcha.model.Captcha;
import com.kuma.boot.captcha.model.CaptchaCodeEnum;
import com.kuma.boot.captcha.model.CaptchaException;
import com.kuma.boot.captcha.service.CaptchaService;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Properties;

public class DefaultCaptchaServiceImpl extends AbstractCaptchaService {
    public String captchaType() {
        return "default";
    }

    public void init(Properties config) {
        for(String s : CaptchaServiceFactory.instances.keySet()) {
            if (!this.captchaType().equals(s)) {
                this.getService(s).init(config);
            }
        }

    }

    public void destroy(Properties config) {
        for(String s : CaptchaServiceFactory.instances.keySet()) {
            if (!this.captchaType().equals(s)) {
                this.getService(s).destroy(config);
            }
        }

    }

    private CaptchaService getService(String captchaType) {
        return (CaptchaService)CaptchaServiceFactory.instances.get(captchaType);
    }

    public Captcha get(Captcha captcha) {
        if (captcha == null) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError(new Object[]{"captchaVO"}));
        } else if (StrUtil.isEmpty(captcha.getCaptchaType())) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError(new Object[]{"类型"}));
        } else {
            return this.getService(captcha.getCaptchaType()).get(captcha);
        }
    }

    public Captcha check(Captcha captcha) {
        if (captcha == null) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError(new Object[]{"captchaVO"}));
        } else if (StrUtil.isEmpty(captcha.getCaptchaType())) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError(new Object[]{"二次校验参数"}));
        } else if (StrUtil.isEmpty(captcha.getToken())) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError(new Object[]{"token"}));
        } else {
            return this.getService(captcha.getCaptchaType()).check(captcha);
        }
    }

    public Captcha verification(Captcha captcha) {
        if (captcha == null) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError(new Object[]{"captchaVO"}));
        } else if (StrUtil.isEmpty(captcha.getCaptchaVerification())) {
            throw new CaptchaException(CaptchaCodeEnum.NULL_ERROR.parseError(new Object[]{"二次校验参数"}));
        } else {
            try {
                String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY, captcha.getCaptchaVerification());
                if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
                    throw new CaptchaException(CaptchaCodeEnum.API_CAPTCHA_INVALID);
                } else {
                    CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
                    return captcha;
                }
            } catch (Exception e) {
                LogUtils.error("验证码坐标解析失败", new Object[]{e});
                throw new CaptchaException(e.getMessage());
            }
        }
    }
}
