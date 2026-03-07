package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.entity.ReadyItem;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.boot.security.spring.access.expression.RoleConstants;
import com.kuma.cloud.blog.domain.vo.ReadyQueryVO;
import com.kuma.cloud.blog.domain.vo.ReadyVO;
import com.kuma.cloud.blog.service.ReadyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "待办事项管理")
@RestController
@RequestMapping("/ready")
@RequiredArgsConstructor
public class ReadyController {

    private final ReadyService readyService;

    @Operation(summary = "创建待办")
    @PostMapping
    @Authorize(RoleConstants.ADMIN)
    public Result<Long> create(@RequestBody ReadyItem item) {
        return Result.success(readyService.createReady(item));
    }

    @Operation(summary = "更新待办")
    @PutMapping("/{id}")
    @Authorize(RoleConstants.ADMIN)
    public Result<Boolean> update(@PathVariable Long id, @RequestBody ReadyItem item) {
        item.setId(id);
        return Result.success(readyService.updateReady(item));
    }

    @Operation(summary = "切换状态")
    @PutMapping("/{id}/toggle")
    @Authorize(RoleConstants.ADMIN)
    public Result<Boolean> toggle(@PathVariable Long id) {
        return Result.success(readyService.toggleStatus(id));
    }

    @Operation(summary = "删除待办（逻辑）")
    @DeleteMapping("/{id}")
    @Authorize(RoleConstants.ADMIN)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(readyService.deleteReady(id));
    }

    @Operation(summary = "物理删除待办")
    @DeleteMapping("/{id}/physical")
    @Authorize(RoleConstants.ADMIN)
    public Result<Boolean> deletePhysical(@PathVariable Long id) {
        return Result.success(readyService.deleteReadyPhysical(id));
    }

    @Operation(summary = "查询待办详情")
    @GetMapping("/{id}")
    public Result<ReadyVO> getById(@PathVariable Long id) {
        return Result.success(readyService.getReadyById(id));
    }

    @Operation(summary = "分页查询待办列表")
    @GetMapping("/list")
    public Result<IPage<ReadyVO>> list(PageQuery pageQuery, ReadyQueryVO queryVO) {
        return Result.success(readyService.getReadyList(pageQuery, queryVO));
    }
}
