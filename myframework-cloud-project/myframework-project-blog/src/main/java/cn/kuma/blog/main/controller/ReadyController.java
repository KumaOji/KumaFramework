package cn.kuma.blog.main.controller;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.common.model.result.SystemResultCode;
import cn.kuma.blog.main.domain.VO.ReadyQueryVO;
import cn.kuma.blog.main.domain.VO.ReadyVO;
import cn.kuma.blog.main.domain.entity.ReadyItem;
import cn.kuma.blog.main.service.ReadyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 待处理事项接口
 *
 * @author Kuma
 * @version 1.0
 */
@Tag(name = "待处理事项", description = "待处理事项增删改查")
@RestController
@RequestMapping("/ready")
public class ReadyController {

    @Autowired
    private ReadyService readyService;

    @Operation(summary = "创建待处理事项", description = "新增一条待处理事项")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Long> create(
            @Parameter(description = "待处理事项", required = true) @RequestBody ReadyItem item) {
        try {
            Long id = readyService.create(item);
            return ApiResult.ok(id, "创建成功");
        } catch (Exception e) {
            return ApiResult.failed(500, "创建失败: " + e.getMessage());
        }
    }

    @Operation(summary = "切换待处理事项状态", description = "根据ID切换状态：待处理/进行中 <-> 已完成")
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<ReadyVO> toggle(
            @Parameter(description = "主键ID", required = true) @PathVariable("id") Long id) {
        try {
            ReadyVO vo = readyService.toggle(id);
            if (vo != null) {
                return ApiResult.ok(vo, "切换成功");
            }
            return ApiResult.failed(500, "切换失败");
        } catch (Exception e) {
            return ApiResult.failed(500, "切换失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新待处理事项", description = "根据ID更新")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> update(
            @Parameter(description = "主键ID", required = true) @PathVariable("id") Long id,
            @Parameter(description = "待处理事项", required = true) @RequestBody ReadyItem item) {
        try {
            item.setId(id);
            boolean success = readyService.update(item);
            if (success) {
                return ApiResult.ok(true, "更新成功");
            }
            return ApiResult.failed(500, "更新失败");
        } catch (Exception e) {
            return ApiResult.failed(500, "更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "逻辑删除", description = "状态改为已删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> delete(
            @Parameter(description = "主键ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = readyService.delete(id);
            if (success) {
                return ApiResult.ok(true, "删除成功");
            }
            return ApiResult.failed(500, "删除失败");
        } catch (Exception e) {
            return ApiResult.failed(500, "删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "物理删除", description = "从数据库彻底删除")
    @DeleteMapping("/{id}/physical")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deletePhysical(
            @Parameter(description = "主键ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = readyService.deletePhysical(id);
            if (success) {
                return ApiResult.ok(true, "物理删除成功");
            }
            return ApiResult.failed(500, "物理删除失败");
        } catch (Exception e) {
            return ApiResult.failed(500, "物理删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "查询详情", description = "根据ID查询")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<ReadyVO> getById(
            @Parameter(description = "主键ID", required = true) @PathVariable("id") Long id) {
        try {
            ReadyVO vo = readyService.getById(id);
            if (vo != null) {
                return ApiResult.ok(vo);
            }
            return ApiResult.failed(SystemResultCode.NOT_FOUND);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "分页列表", description = "分页查询，支持按标题、状态、优先级筛选，排除已删除")
    @GetMapping("/list")
    public ApiResult<PageResult<ReadyVO>> list(
            @Parameter(description = "分页参数") PageParam pageParam,
            @Parameter(description = "查询条件") ReadyQueryVO queryVO) {
        try {
            PageResult<ReadyVO> result = readyService.list(pageParam, queryVO);
            result.getRecords().forEach(readyVO -> {
                readyVO.setContent(null);
            });
            return ApiResult.ok(result);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询列表失败: " + e.getMessage());
        }
    }
}
