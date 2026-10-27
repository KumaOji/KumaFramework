package com.kuma.cloud.uaa.controller;

import com.kuma.boot.auditlog.annotation.AuditLog;
import com.kuma.boot.auditlog.enums.OperateType;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.uaa.domain.dto.MfaVerifyDTO;
import com.kuma.cloud.uaa.domain.vo.MfaBindVO;
import com.kuma.cloud.uaa.service.MfaService;
import com.kuma.cloud.uaa.service.UaaUserService;
import com.kuma.cloud.uaa.config.UaaProperties;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@Tag(name = "二次校验（TOTP）")
@RestController
@RequestMapping("/api/me/mfa")
@RequiredArgsConstructor
public class MfaController {

    private final MfaService mfaService;
    private final UaaUserService userService;
    private final UaaProperties properties;
    private final RedisRepository redisRepository;

    @Operation(summary = "查询当前用户的二次验证绑定状态，不返回密钥")
    @GetMapping("/status")
    public Result<Map<String, Boolean>> status(Principal principal) {
        String username = requireName(principal);
        var user = userService.requireByUsername(username);
        return Result.success(Map.of(
                "featureEnabled", properties.getMfa().isEnabled(),
                "enabled", user.isMfaEnabled(),
                "secretBound", user.isMfaEnabled()
                        || redisRepository.get("uaa:mfa:pending:" + username) != null));
    }

    @Operation(summary = "获取绑定二维码，密钥在确认前不会落库")
    @GetMapping("/bind")
    @AuditLog(module = "uaa.mfa", type = OperateType.UPDATE, description = "startBind",
            recordParams = false, recordResult = false)
    public Result<MfaBindVO> startBind(Principal principal) {
        return Result.success(mfaService.startBind(requireName(principal)));
    }

    @Operation(summary = "输入动态码确认绑定")
    @PostMapping("/bind")
    @AuditLog(module = "uaa.mfa", type = OperateType.UPDATE, description = "confirmBind",
            recordParams = false, recordResult = false)
    public Result<String> confirmBind(Principal principal, @Valid @RequestBody MfaVerifyDTO dto) {
        mfaService.confirmBind(requireName(principal), dto.getCode());
        return Result.success("二次校验已开启");
    }

    @Operation(summary = "输入动态码解除绑定")
    @PostMapping("/unbind")
    @AuditLog(module = "uaa.mfa", type = OperateType.UPDATE, description = "unbind",
            recordParams = false, recordResult = false)
    public Result<String> unbind(Principal principal, @Valid @RequestBody MfaVerifyDTO dto) {
        mfaService.unbind(requireName(principal), dto.getCode());
        return Result.success("二次校验已关闭");
    }

    private String requireName(Principal principal) {
        if (principal == null) {
            throw new BusinessException("未登录");
        }
        return principal.getName();
    }
}
