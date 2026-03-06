package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.entity.Article;
import com.kuma.cloud.blog.domain.vo.*;
import com.kuma.cloud.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "文章管理")
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "创建文章")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Long> create(@RequestBody Article article) {
        return Result.success(articleService.createArticle(article));
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        return Result.success(articleService.updateArticle(article));
    }

    @Operation(summary = "删除文章（逻辑）")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(articleService.deleteArticle(id));
    }

    @Operation(summary = "物理删除文章")
    @DeleteMapping("/{id}/physical")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> deletePhysical(@PathVariable Long id) {
        return Result.success(articleService.deleteArticlePhysical(id));
    }

    @Operation(summary = "查询文章详情")
    @GetMapping("/{id}")
    public Result<ArticleVO> getById(@PathVariable Long id) {
        return Result.success(articleService.getArticleById(id));
    }

    @Operation(summary = "分页查询文章列表")
    @GetMapping("/list")
    public Result<IPage<ArticleVO>> list(PageQuery pageQuery, ArticleQueryVO queryVO) {
        return Result.success(articleService.getArticleList(pageQuery, queryVO));
    }

    @Operation(summary = "获取分类列表")
    @GetMapping("/category/list")
    public Result<List<CategoryVO>> categoryList() {
        return Result.success(articleService.getCategoryList());
    }

    @Operation(summary = "各分类文章数统计")
    @GetMapping("/category/counts")
    public Result<List<CategoryArticleCountVO>> categoryCounts() {
        return Result.success(articleService.getCategoryArticleCounts());
    }

    @Operation(summary = "增加阅读量")
    @PostMapping("/{id}/view")
    public Result<Boolean> incrementView(@PathVariable Long id) {
        return Result.success(articleService.incrementViewCount(id));
    }

    @Operation(summary = "增加点赞数")
    @PostMapping("/{id}/like")
    public Result<Boolean> incrementLike(@PathVariable Long id) {
        return Result.success(articleService.incrementLikeCount(id));
    }

    @Operation(summary = "增加评论数")
    @PostMapping("/{id}/comment")
    public Result<Boolean> incrementComment(@PathVariable Long id) {
        return Result.success(articleService.incrementCommentCount(id));
    }

    @Operation(summary = "发布文章")
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> publish(@PathVariable Long id) {
        return Result.success(articleService.publishArticle(id));
    }

    @Operation(summary = "取消发布（转为草稿）")
    @PostMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> unpublish(@PathVariable Long id) {
        return Result.success(articleService.unpublishArticle(id));
    }
}
