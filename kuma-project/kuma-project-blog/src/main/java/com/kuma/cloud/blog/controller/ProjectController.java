package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.dto.ProjectSaveDTO;
import com.kuma.cloud.blog.domain.entity.Project;
import com.kuma.cloud.blog.domain.query.ProjectQuery;
import com.kuma.cloud.blog.domain.vo.ProjectVO;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "项目管理")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "创建项目")
    @PostMapping
    @Authorize(BlogPermissions.PROJECT_CREATE)
    public Result<Long> create(@Valid @RequestBody ProjectSaveDTO dto) {
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);
        return Result.success(projectService.createProject(project));
    }

    @Operation(summary = "更新项目")
    @PutMapping("/{id}")
    @Authorize(BlogPermissions.PROJECT_UPDATE)
    public Result<Boolean> update(@PathVariable Long id, @Valid @RequestBody ProjectSaveDTO dto) {
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);
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
    public Result<IPage<ProjectVO>> list(PageQuery pageQuery, ProjectQuery query) {
        if (pageQuery.getCurrentPage() == null) pageQuery.setCurrentPage(1);
        if (pageQuery.getPageSize() == null) pageQuery.setPageSize(10);
        return Result.success(projectService.getProjectList(pageQuery, query));
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
