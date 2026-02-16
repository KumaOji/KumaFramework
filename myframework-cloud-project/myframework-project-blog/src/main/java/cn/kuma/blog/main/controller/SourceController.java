package cn.kuma.blog.main.controller;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.common.model.result.SystemResultCode;
import cn.kuma.blog.main.domain.entity.Source;
import cn.kuma.blog.main.service.SourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源管理控制器
 *
 * @author Kuma
 * @version 1.0
 */
@Tag(name = "资源管理", description = "资源相关的增删改查接口，管理资源的名称和位置")
@RestController
@RequestMapping("/source")
@PreAuthorize("hasRole('ADMIN')")
public class SourceController {

    @Autowired
    private SourceService sourceService;

    /**
     * 创建资源
     */
    @Operation(summary = "创建资源", description = "创建一个新资源")
    @PostMapping
    public ApiResult<Long> createSource(
            @Parameter(description = "资源信息", required = true) @RequestBody Source source) {
        try {
            Long id = sourceService.createSource(source);
            return ApiResult.ok(id, "资源创建成功");
        } catch (Exception e) {
            return ApiResult.failed(500, "资源创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新资源
     */
    @Operation(summary = "更新资源", description = "根据ID更新资源信息")
    @PutMapping("/{id}")
    public ApiResult<Boolean> updateSource(
            @Parameter(description = "资源ID", required = true) @PathVariable("id") Long id,
            @Parameter(description = "资源信息", required = true) @RequestBody Source source) {
        try {
            source.setId(id);
            boolean success = sourceService.updateSource(source);
            if (success) {
                return ApiResult.ok(true, "资源更新成功");
            } else {
                return ApiResult.failed(500, "资源更新失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "资源更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除资源
     */
    @Operation(summary = "删除资源", description = "根据ID删除资源")
    @DeleteMapping("/{id}")
    public ApiResult<Boolean> deleteSource(
            @Parameter(description = "资源ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = sourceService.deleteSource(id);
            if (success) {
                return ApiResult.ok(true, "资源删除成功");
            } else {
                return ApiResult.failed(500, "资源删除失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "资源删除失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询资源
     */
    @Operation(summary = "查询资源详情", description = "根据ID查询资源详细信息")
    @GetMapping("/{id}")
    public ApiResult<Source> getSourceById(
            @Parameter(description = "资源ID", required = true) @PathVariable("id") Long id) {
        try {
            Source source = sourceService.getSourceById(id);
            if (source != null) {
                return ApiResult.ok(source);
            } else {
                return ApiResult.failed(SystemResultCode.NOT_FOUND);
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "查询资源失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询资源列表
     */
    @Operation(summary = "分页查询资源列表", description = "根据条件分页查询资源列表，支持按名称模糊查询")
    @GetMapping("/list")
    public ApiResult<PageResult<Source>> getSourceList(
            @Parameter(description = "分页参数") PageParam pageParam,
            @Parameter(description = "资源名称（模糊查询，可选）") @RequestParam(required = false) String name) {
        try {
            PageResult<Source> pageResult = sourceService.getSourceList(pageParam, name);
            return ApiResult.ok(pageResult);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询资源列表失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有资源列表
     */
    @Operation(summary = "查询所有资源", description = "查询所有资源列表（不分页）")
    @GetMapping("/all")
    public ApiResult<List<Source>> getAllSources() {
        try {
            List<Source> list = sourceService.getAllSources();
            return ApiResult.ok(list);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询资源列表失败: " + e.getMessage());
        }
    }
}
