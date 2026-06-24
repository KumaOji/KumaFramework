package com.kuma.cloud.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.entity.Article;
import com.kuma.cloud.blog.domain.query.ArticleQuery;
import com.kuma.cloud.blog.domain.vo.*;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    @Authorize(BlogPermissions.ARTICLE_CREATE)
    public Result<Long> create(@RequestBody Article article) {
        return Result.success(articleService.createArticle(article));
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    @Authorize(BlogPermissions.ARTICLE_UPDATE)
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        return Result.success(articleService.updateArticle(article));
    }

    @Operation(summary = "删除文章（逻辑）")
    @DeleteMapping("/{id}")
    @Authorize(BlogPermissions.ARTICLE_DELETE)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(articleService.deleteArticle(id));
    }

    @Operation(summary = "物理删除文章")
    @DeleteMapping("/{id}/physical")
    @Authorize(BlogPermissions.ARTICLE_DELETE)
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
    public Result<IPage<ArticleVO>> list(
            @Parameter(description = "当前页") @RequestParam(required = false) Integer currentPage,
            @Parameter(description = "每页条数，支持 pageSize 或 size") @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer size,
            PageQuery pageQuery,
            ArticleQuery queryVO) {
        int current = currentPage != null ? currentPage : (pageQuery != null && pageQuery.getCurrentPage() != null ? pageQuery.getCurrentPage() : 1);
        int pageSizeVal = pageSize != null ? pageSize : (size != null ? size : (pageQuery != null && pageQuery.getPageSize() != null ? pageQuery.getPageSize() : 10));
        if (pageQuery == null) pageQuery = new PageQuery();
        pageQuery.setCurrentPage(current);
        pageQuery.setPageSize(pageSizeVal);
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
    @Authorize(BlogPermissions.ARTICLE_UPDATE)
    public Result<Boolean> publish(@PathVariable Long id) {
        return Result.success(articleService.publishArticle(id));
    }

    @Operation(summary = "取消发布（转为草稿）")
    @PostMapping("/{id}/unpublish")
    @Authorize(BlogPermissions.ARTICLE_UPDATE)
    public Result<Boolean> unpublish(@PathVariable Long id) {
        return Result.success(articleService.unpublishArticle(id));
    }
}
