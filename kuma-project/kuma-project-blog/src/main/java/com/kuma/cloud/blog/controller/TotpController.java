package com.kuma.cloud.blog.controller;

import com.kuma.boot.auditlog.annotation.AuditLog;
import com.kuma.boot.auditlog.enums.OperateType;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.dto.TotpVerifyDTO;
import com.kuma.cloud.blog.domain.vo.TotpFeatureVO;
import com.kuma.cloud.blog.domain.vo.TotpSetupVO;
import com.kuma.cloud.blog.domain.vo.TotpStatusVO;
import com.kuma.cloud.blog.integration.uaa.UaaAuthSettingsService;
import com.kuma.cloud.blog.integration.uaa.UaaMfaClient;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final UaaMfaClient mfaClient;
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
    public Result<TotpStatusVO> status(HttpServletRequest request) {
        uaaAuthSettingsService.requireMfaEnabled();
        return Result.success(mfaClient.status(request));
    }

    @Operation(summary = "生成绑定二维码")
    @PostMapping("/setup")
    @AuditLog(module = "blog.mfa", type = OperateType.UPDATE, description = "setup",
            recordParams = false, recordResult = false)
    public Result<TotpSetupVO> setup(HttpServletRequest request) {
        uaaAuthSettingsService.requireMfaEnabled();
        return Result.success(mfaClient.setup(request));
    }

    @Operation(summary = "验证动态码并启用 TOTP")
    @PostMapping("/enable")
    @AuditLog(module = "blog.mfa", type = OperateType.UPDATE, description = "enable",
            recordParams = false, recordResult = false)
    public Result<String> enable(
            HttpServletRequest request, @Valid @RequestBody TotpVerifyDTO dto) {
        uaaAuthSettingsService.requireMfaEnabled();
        mfaClient.enable(request, dto.getCode());
        return Result.success("TOTP 已启用");
    }

    @Operation(summary = "验证动态码并关闭 TOTP")
    @PostMapping("/disable")
    @AuditLog(module = "blog.mfa", type = OperateType.UPDATE, description = "disable",
            recordParams = false, recordResult = false)
    public Result<String> disable(
            HttpServletRequest request, @Valid @RequestBody TotpVerifyDTO dto) {
        uaaAuthSettingsService.requireMfaEnabled();
        mfaClient.disable(request, dto.getCode());
        return Result.success("TOTP 已关闭");
    }

}
