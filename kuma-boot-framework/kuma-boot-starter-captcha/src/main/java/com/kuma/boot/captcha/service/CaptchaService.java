//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.service;

import com.kuma.boot.captcha.model.Captcha;
import java.util.Properties;

public interface CaptchaService {
    void init(Properties config);

    Captcha get(Captcha captcha);

    Captcha check(Captcha captcha);

    Captcha verification(Captcha captcha);

    String captchaType();

    void destroy(Properties config);
}
