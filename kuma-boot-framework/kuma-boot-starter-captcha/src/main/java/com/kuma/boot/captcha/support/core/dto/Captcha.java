//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public abstract class Captcha {
    @Schema(
            title = "验证码身份"
    )
    private String identity;
    @Schema(
            title = "验证码类别"
    )
    private String category;

    public String getIdentity() {
        return this.identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
