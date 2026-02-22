/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 *  jakarta.validation.constraints.NotBlank
 */
package com.kuma.boot.security.spring.authentication.stamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(title="\u767b\u5f55\u9519\u8bef\u63d0\u793a\u4fe1\u606f")
public class SignInErrorPrompt {
    @NotBlank(message="\u767b\u5f55\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a")
    @Schema(name="\u767b\u5f55\u7528\u6237\u540d", title="\u5fc5\u987b\u662f\u6709\u6548\u7684\u7528\u6237\u540d")
    private @NotBlank(message="\u767b\u5f55\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a") String username;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

