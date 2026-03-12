package com.kuma.boot.captcha.support.core.processor;

import com.kuma.boot.captcha.support.core.definition.Renderer;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCategory;
import com.kuma.boot.captcha.support.core.dto.Captcha;
import com.kuma.boot.captcha.support.core.dto.Verification;
import com.kuma.boot.captcha.support.core.exception.CaptchaCategoryIsIncorrectException;
import com.kuma.boot.captcha.support.core.exception.CaptchaHandlerNotExistException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CaptchaRendererFactory {

    private Map<String, Renderer> handlers;

    @Autowired
    public void setHandlers(Map<String, Renderer> handlers) {
        this.handlers = handlers;
    }

    public Renderer getRenderer(String category) {
        CaptchaCategory captchaCategory = CaptchaCategory.getCaptchaCategory(category);
        if (ObjectUtils.isEmpty(captchaCategory)) {
            throw new CaptchaCategoryIsIncorrectException("Captcha category is incorrect.");
        }
        Renderer renderer = handlers.get(captchaCategory.getConstant());
        if (ObjectUtils.isEmpty(renderer)) {
            throw new CaptchaHandlerNotExistException();
        }
        return renderer;
    }

    public Captcha getCaptcha(String identity, String category) {
        return getRenderer(category).getCapcha(identity);
    }

    public boolean verify(Verification verification) {
        return getRenderer(verification.getCategory()).verify(verification);
    }
}
