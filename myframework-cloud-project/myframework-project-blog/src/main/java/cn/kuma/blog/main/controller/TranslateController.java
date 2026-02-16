package cn.kuma.blog.main.controller;

import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.main.service.TranslateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 翻译服务控制器
 * 代理转发请求到内部的 LibreTranslate 服务
 * 外部无法直接访问 LibreTranslate，只能通过此接口
 *
 * @author Kuma
 * @version 1.0
 */
@Tag(name = "翻译服务", description = "翻译相关接口，代理内部 LibreTranslate 服务")
@RestController
@RequestMapping("/translate")
@PreAuthorize("isAuthenticated()")
public class TranslateController {

    @Autowired
    private TranslateService translateService;

    /**
     * 翻译文本
     */
    @Operation(summary = "翻译文本", description = "将文本从源语言翻译为目标语言")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Map<String, Object>> translate(
            @Parameter(description = "要翻译的文本", required = true) @RequestParam("q") String text,
            @Parameter(description = "源语言代码", required = true) @RequestParam("source") String source,
            @Parameter(description = "目标语言代码", required = true) @RequestParam("target") String target) {
        try {
            Map<String, Object> result = translateService.translate(text, source, target);
            return ApiResult.ok(result);
        } catch (Exception e) {
            return ApiResult.failed(500, "翻译失败: " + e.getMessage());
        }
    }

    /**
     * 获取支持的语言列表
     */
    @Operation(summary = "获取支持的语言", description = "获取 LibreTranslate 支持的所有语言列表")
    @GetMapping("/languages")
    public ApiResult<List<Map<String, Object>>> getLanguages() {
        try {
            List<Map<String, Object>> languages = translateService.getLanguages();
            return ApiResult.ok(languages);
        } catch (Exception e) {
            return ApiResult.failed(500, "获取语言列表失败: " + e.getMessage());
        }
    }

    /**
     * 检测文本语言
     */
    @Operation(summary = "检测语言", description = "自动检测文本的语言")
    @PostMapping("/detect")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<List<Map<String, Object>>> detectLanguage(
            @Parameter(description = "要检测的文本", required = true) @RequestParam("q") String text) {
        try {
            List<Map<String, Object>> result = translateService.detectLanguage(text);
            return ApiResult.ok(result);
        } catch (Exception e) {
            return ApiResult.failed(500, "语言检测失败: " + e.getMessage());
        }
    }

    /**
     * 检查翻译服务状态
     */
    @Operation(summary = "检查服务状态", description = "检查 LibreTranslate 服务是否可用")
    @GetMapping("/status")
    public ApiResult<Map<String, Object>> getServiceStatus() {
        boolean available = translateService.isServiceAvailable();
        return ApiResult.ok(Map.of(
                "available", available,
                "message", available ? "翻译服务正常" : "翻译服务不可用"
        ));
    }
}
