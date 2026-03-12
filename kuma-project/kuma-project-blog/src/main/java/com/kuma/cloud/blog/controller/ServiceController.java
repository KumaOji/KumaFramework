package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 服务管理接口（管理 LibreTranslate 等外部服务进程）
 *
 * @author Kuma
 */
@Tag(name = "服务管理")
@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @Operation(summary = "启动 LibreTranslate 翻译服务")
    @PostMapping("/libretranslate/start")
    @Authorize(BlogPermissions.SERVICE_MANAGE)
    public Result<Map<String, Object>> startLibreTranslate() {
        return Result.success(serviceService.startLibreTranslate());
    }

    @Operation(summary = "停止 LibreTranslate 翻译服务")
    @PostMapping("/libretranslate/stop")
    @Authorize(BlogPermissions.SERVICE_MANAGE)
    public Result<String> stopLibreTranslate() {
        return Result.success(serviceService.stopLibreTranslate());
    }

    @Operation(summary = "查询 LibreTranslate 服务状态")
    @GetMapping("/libretranslate/status")
    @Authorize(BlogPermissions.SERVICE_MANAGE)
    public Result<Map<String, Object>> getLibreTranslateStatus() {
        return Result.success(serviceService.getLibreTranslateStatus());
    }
}
