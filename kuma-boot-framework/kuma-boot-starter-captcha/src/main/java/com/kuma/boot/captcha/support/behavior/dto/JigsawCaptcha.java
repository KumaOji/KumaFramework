//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.behavior.dto;

import com.kuma.boot.captcha.support.core.dto.Captcha;

public class JigsawCaptcha extends Captcha {
    private String originalImageBase64;
    private String sliderImageBase64;

    public String getOriginalImageBase64() {
        return this.originalImageBase64;
    }

    public void setOriginalImageBase64(String originalImageBase64) {
        this.originalImageBase64 = originalImageBase64;
    }

    public String getSliderImageBase64() {
        return this.sliderImageBase64;
    }

    public void setSliderImageBase64(String sliderImageBase64) {
        this.sliderImageBase64 = sliderImageBase64;
    }
}
