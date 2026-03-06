package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.entity.Source;
import com.kuma.cloud.blog.service.SourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "资源管理")
@RestController
@RequestMapping("/source")
@RequiredArgsConstructor
public class SourceController {

    private final SourceService sourceService;

    @Operation(summary = "创建资源")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Long> create(@RequestBody Source source) {
        return Result.success(sourceService.createSource(source));
    }

    @Operation(summary = "更新资源")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Source source) {
        source.setId(id);
        return Result.success(sourceService.updateSource(source));
    }

    @Operation(summary = "删除资源")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(sourceService.deleteSource(id));
    }

    @Operation(summary = "查询资源详情")
    @GetMapping("/{id}")
    public Result<Source> getById(@PathVariable Long id) {
        return Result.success(sourceService.getSourceById(id));
    }

    @Operation(summary = "分页查询资源列表")
    @GetMapping("/list")
    public Result<IPage<Source>> list(PageQuery pageQuery) {
        return Result.success(sourceService.getSourceList(pageQuery));
    }

    @Operation(summary = "获取所有资源")
    @GetMapping("/all")
    public Result<List<Source>> all() {
        return Result.success(sourceService.getAllSources());
    }
}
