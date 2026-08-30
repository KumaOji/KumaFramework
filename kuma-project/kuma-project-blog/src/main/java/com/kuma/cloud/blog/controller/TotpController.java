package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.dto.TotpVerifyDTO;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.domain.vo.TotpFeatureVO;
import com.kuma.cloud.blog.domain.vo.TotpSetupVO;
import com.kuma.cloud.blog.domain.vo.TotpStatusVO;
import com.kuma.cloud.blog.integration.uaa.UaaAuthSettingsService;
import com.kuma.cloud.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TOTP 二次验证")
@RestController
@RequestMapping("/auth/totp")
@RequiredArgsConstructor
public class TotpController {

    private final UserService userService;
    private final UaaAuthSettingsService uaaAuthSettingsService;

    @Operation(summary = "查询 UAA 全局 TOTP 开关（无需登录）")
    @GetMapping("/feature")
    public Result<TotpFeatureVO> feature() {
        TotpFeatureVO vo = new TotpFeatureVO();
        vo.setEnabled(uaaAuthSettingsService.isMfaEnabled());
        return Result.success(vo);
    }

    @Operation(summary = "查询当前用户 TOTP 绑定状态")
    @GetMapping("/status")
    public Result<TotpStatusVO> status(@AuthenticationPrincipal UserDetails principal) {
        uaaAuthSettingsService.requireMfaEnabled();
        User user = requireUser(principal);
        TotpStatusVO vo = new TotpStatusVO();
        vo.setFeatureEnabled(true);
        vo.setEnabled(user.getTotpEnabled() != null && user.getTotpEnabled() == 1);
        vo.setSecretBound(user.getTotpSecret() != null);
        return Result.success(vo);
    }

    @Operation(summary = "生成绑定二维码")
    @PostMapping("/setup")
    public Result<TotpSetupVO> setup(@AuthenticationPrincipal UserDetails principal) {
        uaaAuthSettingsService.requireMfaEnabled();
        User user = requireUser(principal);
        String qrDataUri = userService.setupTotp(user.getId(), uaaAuthSettingsService.mfaIssuer());
        TotpSetupVO vo = new TotpSetupVO();
        vo.setQrDataUri(qrDataUri);
        return Result.success(vo);
    }

    @Operation(summary = "验证动态码并启用 TOTP")
    @PostMapping("/enable")
    public Result<String> enable(
            @AuthenticationPrincipal UserDetails principal, @Valid @RequestBody TotpVerifyDTO dto) {
        uaaAuthSettingsService.requireMfaEnabled();
        User user = requireUser(principal);
        userService.enableTotp(user.getId(), dto.getCode());
        return Result.success("TOTP 已启用");
    }

    @Operation(summary = "验证动态码并关闭 TOTP")
    @PostMapping("/disable")
    public Result<String> disable(
            @AuthenticationPrincipal UserDetails principal, @Valid @RequestBody TotpVerifyDTO dto) {
        uaaAuthSettingsService.requireMfaEnabled();
        User user = requireUser(principal);
        userService.disableTotp(user.getId(), dto.getCode());
        return Result.success("TOTP 已关闭");
    }

    private User requireUser(UserDetails principal) {
        if (principal == null) {
            throw new BusinessException("未登录");
        }
        User user = userService.getByUsername(principal.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }
}
