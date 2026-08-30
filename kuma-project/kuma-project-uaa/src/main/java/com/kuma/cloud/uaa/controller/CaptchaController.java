package com.kuma.cloud.uaa.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.ratelimit.ratelimitredis.RateLimiter;
import com.kuma.boot.security.spring.annotation.NotAuth;
import com.kuma.boot.web.gracefulresponse.api.ExcludeFromGracefulResponse;
import com.kuma.cloud.uaa.domain.vo.CaptchaVO;
import com.kuma.cloud.uaa.support.LoginCaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Tag(name = "登录验证码")
@RestController
@RequiredArgsConstructor
public class CaptchaController {

    private final LoginCaptchaService captchaService;

    @NotAuth
    @ExcludeFromGracefulResponse
    @Operation(summary = "获取图形验证码，返回 captchaKey 与 base64 图片")
    @RateLimiter(value = "uaa:captcha", max = 30, ttl = 1, timeUnit = TimeUnit.MINUTES)
    @GetMapping("/captcha")
    public Result<CaptchaVO> captcha() {
        return Result.success(captchaService.issue());
    }
}
