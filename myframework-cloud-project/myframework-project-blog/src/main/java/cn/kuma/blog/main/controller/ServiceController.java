package cn.kuma.blog.main.controller;


import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.main.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 服务管理控制器
 * 用于管理各种外部服务，包括 LibreTranslate 等服务
 *
 * @author Kuma
 * @version 1.0
 */
@Tag(name = "服务管理", description = "外部服务相关的控制接口，包括启动、停止、状态查询等功能")
@RestController
@RequestMapping("/service")
@PreAuthorize("hasRole('ADMIN')")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    /**
     * 启动 LibreTranslate 服务
     *
     * @return 启动结果
     */
    @Operation(summary = "启动LibreTranslate服务", description = "异步启动LibreTranslate服务")
    @PostMapping("/libretranslate/start")
    public ApiResult<Map<String, Object>> startLibreTranslate() {
        try {
            Map<String, Object> result = serviceService.startLibreTranslate();
            if (result.containsKey("running") && (Boolean) result.get("running")) {
                return ApiResult.ok(result, "LibreTranslate服务已在运行");
            }
            return ApiResult.ok(result, "LibreTranslate服务启动命令已提交");
        } catch (Exception e) {
            return ApiResult.failed(500, "启动服务失败: " + e.getMessage());
        }
    }

    /**
     * 停止 LibreTranslate 服务
     *
     * @return 停止结果
     */
    @Operation(summary = "停止LibreTranslate服务", description = "停止正在运行的LibreTranslate服务")
    @PostMapping("/libretranslate/stop")
    public ApiResult<String> stopLibreTranslate() {
        try {
            String result = serviceService.stopLibreTranslate();
            if ("服务未运行".equals(result)) {
                return ApiResult.ok(result, "LibreTranslate服务未运行");
            }
            return ApiResult.ok(result, "LibreTranslate" + result);
        } catch (Exception e) {
            return ApiResult.failed(500, "停止服务失败: " + e.getMessage());
        }
    }

    /**
     * 查询 LibreTranslate 服务状态
     *
     * @return 服务状态
     */
    @Operation(summary = "查询LibreTranslate服务状态", description = "查询LibreTranslate服务的运行状态")
    @GetMapping("/libretranslate/status")
    public ApiResult<Map<String, Object>> getLibreTranslateStatus() {
        try {
            Map<String, Object> status = serviceService.getLibreTranslateStatus();
            boolean running = (Boolean) status.get("running");
            String message = running ? "LibreTranslate服务正在运行" : "LibreTranslate服务未运行";
            return ApiResult.ok(status, message);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询服务状态失败: " + e.getMessage());
        }
    }

    /**
     * 启动 MediaCrawler 服务
     *
     * @return 启动结果
     */
    @Operation(summary = "启动MediaCrawler服务", description = "异步启动MediaCrawler服务")
    @PostMapping("/mediacrawler/start")
    public ApiResult<Map<String, Object>> startMediaCrawler() {
        try {
            Map<String, Object> result = serviceService.startMediaCrawler();
            if (result.containsKey("running") && (Boolean) result.get("running")) {
                return ApiResult.ok(result, "MediaCrawler服务已在运行");
            }
            return ApiResult.ok(result, "MediaCrawler服务启动命令已提交");
        } catch (Exception e) {
            return ApiResult.failed(500, "启动服务失败: " + e.getMessage());
        }
    }

    /**
     * 停止 MediaCrawler 服务
     *
     * @return 停止结果
     */
    @Operation(summary = "停止MediaCrawler服务", description = "停止正在运行的MediaCrawler服务")
    @PostMapping("/mediacrawler/stop")
    public ApiResult<String> stopMediaCrawler() {
        try {
            String result = serviceService.stopMediaCrawler();
            if ("服务未运行".equals(result)) {
                return ApiResult.ok(result, "MediaCrawler服务未运行");
            }
            return ApiResult.ok(result, "MediaCrawler" + result);
        } catch (Exception e) {
            return ApiResult.failed(500, "停止服务失败: " + e.getMessage());
        }
    }

    /**
     * 查询 MediaCrawler 服务状态
     *
     * @return 服务状态
     */
    @Operation(summary = "查询MediaCrawler服务状态", description = "查询MediaCrawler服务的运行状态")
    @GetMapping("/mediacrawler/status")
    public ApiResult<Map<String, Object>> getMediaCrawlerStatus() {
        try {
            Map<String, Object> status = serviceService.getMediaCrawlerStatus();
            boolean running = (Boolean) status.get("running");
            String message = running ? "MediaCrawler服务正在运行" : "MediaCrawler服务未运行";
            return ApiResult.ok(status, message);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询服务状态失败: " + e.getMessage());
        }
    }
}
