//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.processor;

import com.kuma.boot.captcha.support.core.definition.Renderer;
import com.kuma.boot.captcha.support.core.definition.enums.CaptchaCategory;
import com.kuma.boot.captcha.support.core.dto.Captcha;
import com.kuma.boot.captcha.support.core.dto.Verification;
import com.kuma.boot.captcha.support.core.exception.CaptchaCategoryIsIncorrectException;
import com.kuma.boot.captcha.support.core.exception.CaptchaHandlerNotExistException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CaptchaRendererFactory {
    @Autowired
    private final Map<String, Renderer> handlers = new ConcurrentHashMap(8);

    public Renderer getRenderer(String category) {
        CaptchaCategory captchaCategory = CaptchaCategory.getCaptchaCategory(category);
        if (ObjectUtils.isEmpty(captchaCategory)) {
            throw new CaptchaCategoryIsIncorrectException("Captcha category is incorrect.");
        } else {
            Renderer renderer = (Renderer)this.handlers.get(captchaCategory.getConstant());
            if (ObjectUtils.isEmpty(renderer)) {
                throw new CaptchaHandlerNotExistException();
            } else {
                return renderer;
            }
        }
    }

    public Captcha getCaptcha(String identity, String category) {
        Renderer renderer = this.getRenderer(category);
        return renderer.getCapcha(identity);
    }

    public boolean verify(Verification verification) {
        Renderer renderer = this.getRenderer(verification.getCategory());
        return renderer.verify(verification);
    }
}
