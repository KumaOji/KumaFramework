/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.service;

import com.kuma.boot.captcha.model.Captcha;
import java.util.Properties;

public interface CaptchaService {
    public void init(Properties var1);

    public Captcha get(Captcha var1);

    public Captcha check(Captcha var1);

    public Captcha verification(Captcha var1);

    public String captchaType();

    public void destroy(Properties var1);
}

