package com.kuma.cloud.uaa.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.annotation.NotAuth;
import com.kuma.cloud.uaa.config.UaaProperties;
import com.kuma.cloud.uaa.domain.vo.AuthSettingsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "公开认证配置")
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicAuthSettingsController {

    private final UaaProperties properties;

    @NotAuth
    @Operation(summary = "获取登录相关全局开关（图形验证码、TOTP 等）")
    @GetMapping("/auth-settings")
    public Result<AuthSettingsVO> authSettings() {
        AuthSettingsVO vo = new AuthSettingsVO();
        vo.setCaptchaEnabled(properties.getCaptcha().isEnabled());
        vo.setMfaEnabled(properties.getMfa().isEnabled());
        vo.setMfaIssuer(properties.getMfa().getIssuer());
        return Result.success(vo);
    }
}
