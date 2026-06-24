package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.entity.Project;
import com.kuma.cloud.blog.domain.query.ProjectQuery;
import com.kuma.cloud.blog.domain.vo.ProjectVO;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "项目管理")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "创建项目")
    @PostMapping
    @Authorize(BlogPermissions.PROJECT_CREATE)
    public Result<Long> create(@RequestBody Project project) {
        return Result.success(projectService.createProject(project));
    }

    @Operation(summary = "更新项目")
    @PutMapping("/{id}")
    @Authorize(BlogPermissions.PROJECT_UPDATE)
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        return Result.success(projectService.updateProject(project));
    }

    @Operation(summary = "删除项目（逻辑）")
    @DeleteMapping("/{id}")
    @Authorize(BlogPermissions.PROJECT_DELETE)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(projectService.deleteProject(id));
    }

    @Operation(summary = "物理删除项目")
    @DeleteMapping("/{id}/physical")
    @Authorize(BlogPermissions.PROJECT_DELETE)
    public Result<Boolean> deletePhysical(@PathVariable Long id) {
        return Result.success(projectService.deleteProjectPhysical(id));
    }

    @Operation(summary = "查询项目详情")
    @GetMapping("/{id}")
    public Result<ProjectVO> getById(@PathVariable Long id) {
        return Result.success(projectService.getProjectById(id));
    }

    @Operation(summary = "分页查询项目列表")
    @GetMapping("/list")
    public Result<IPage<ProjectVO>> list(
            @Parameter(description = "当前页") @RequestParam(required = false) Integer currentPage,
            @Parameter(description = "每页条数") @RequestParam(required = false) Integer pageSize,
            PageQuery pageQuery,
            ProjectQuery queryVO) {
        int current = currentPage != null ? currentPage : (pageQuery != null && pageQuery.getCurrentPage() != null ? pageQuery.getCurrentPage() : 1);
        int size = pageSize != null ? pageSize : (pageQuery != null && pageQuery.getPageSize() != null ? pageQuery.getPageSize() : 10);
        if (pageQuery == null) pageQuery = new PageQuery();
        pageQuery.setCurrentPage(current);
        pageQuery.setPageSize(size);
        return Result.success(projectService.getProjectList(pageQuery, queryVO));
    }

    @Operation(summary = "增加浏览量")
    @PostMapping("/{id}/view")
    public Result<Boolean> incrementView(@PathVariable Long id) {
        return Result.success(projectService.incrementViewCount(id));
    }

    @Operation(summary = "设置重点展示")
    @PostMapping("/{id}/featured")
    @Authorize(BlogPermissions.PROJECT_UPDATE)
    public Result<Boolean> setFeatured(@PathVariable Long id,
                                       @Parameter(description = "true=展示 false=取消") @RequestParam boolean featured) {
        return Result.success(projectService.setFeatured(id, featured));
    }
}
