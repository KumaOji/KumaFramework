package cn.kuma.blog.main.controller;


import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.common.model.result.SystemResultCode;
import cn.kuma.blog.main.domain.VO.ArticleQueryVO;
import cn.kuma.blog.main.domain.VO.ArticleVO;
import cn.kuma.blog.main.domain.VO.CategoryArticleCountVO;
import cn.kuma.blog.main.domain.VO.CategoryVO;
import cn.kuma.blog.main.domain.entity.Article;
import cn.kuma.blog.main.service.ArticleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章控制器
 *
 * @author Kuma
 * @version 1.0
 */
@Tag(name = "文章管理", description = "文章相关的增删改查接口")
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 创建文章
     */
    @Operation(summary = "创建文章", description = "创建一篇新文章")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Long> createArticle(
            @Parameter(description = "文章信息", required = true) @RequestBody Article article) {
        try {
            Long id = articleService.createArticle(article);
            return ApiResult.ok(id, "文章创建成功");
        } catch (Exception e) {
            return ApiResult.failed(500, "文章创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新文章
     */
    @Operation(summary = "更新文章", description = "根据ID更新文章信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> updateArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable("id") Long id,
            @Parameter(description = "文章信息", required = true) @RequestBody Article article) {
        try {
            article.setId(id);
            boolean success = articleService.updateArticle(article);
            if (success) {
                return ApiResult.ok(true, "文章更新成功");
            } else {
                return ApiResult.failed(500, "文章更新失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "文章更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除文章（逻辑删除）
     */
    @Operation(summary = "删除文章", description = "根据ID逻辑删除文章（状态改为已删除）")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deleteArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = articleService.deleteArticle(id);
            if (success) {
                return ApiResult.ok(true, "文章删除成功");
            } else {
                return ApiResult.failed(500, "文章删除失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "文章删除失败: " + e.getMessage());
        }
    }

    /**
     * 物理删除文章
     */
    @Operation(summary = "物理删除文章", description = "根据ID物理删除文章（从数据库中彻底删除）")
    @DeleteMapping("/{id}/physical")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deleteArticlePhysical(
            @Parameter(description = "文章ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = articleService.deleteArticlePhysical(id);
            if (success) {
                return ApiResult.ok(true, "文章物理删除成功");
            } else {
                return ApiResult.failed(500, "文章物理删除失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "文章物理删除失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询文章
     */
    @Operation(summary = "查询文章详情", description = "根据ID查询文章详细信息")
    @GetMapping("/{id}")
    public ApiResult<ArticleVO> getArticleById(
            @Parameter(description = "文章ID", required = true) @PathVariable("id") Long id) {
        try {
            ArticleVO article = articleService.getArticleById(id);
            if (article != null) {
                return ApiResult.ok(article);
            } else {
                return ApiResult.failed(SystemResultCode.NOT_FOUND);
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "查询文章失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询文章列表（支持按分类、标题、状态等筛选）
     */
    @Operation(summary = "分页查询文章列表", description = "根据条件分页查询文章列表。支持按分类 categoryId、标题、状态、标签等筛选；传入 categoryId 即可按分类查找文章。")
    @GetMapping("/list")
    public ApiResult<PageResult<ArticleVO>> getArticleList(
            @Parameter(description = "分页参数") PageParam pageParam,
            @Parameter(description = "查询条件（含 categoryId 按分类查）") ArticleQueryVO queryVO) {
        try {
            PageResult<ArticleVO> pageResult = articleService.getArticleList(pageParam, queryVO);
            return ApiResult.ok(pageResult);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询文章列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有分类列表
     */
    @Operation(summary = "获取分类列表", description = "获取所有文章分类列表，包含 id、name、parentId、code、fullPath")
    @GetMapping("/category/list")
    public ApiResult<List<CategoryVO>> getCategoryList() {
        try {
            List<CategoryVO> list = articleService.getCategoryList();
            return ApiResult.ok(list);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询分类列表失败: " + e.getMessage());
        }
    }

    /**
     * 各分类文章数统计
     */
    @Operation(summary = "各分类文章数", description = "查询每个分类的文章数量（排除已删除），按数量降序")
    @GetMapping("/category/counts")
    public ApiResult<List<CategoryArticleCountVO>> getCategoryArticleCounts() {
        try {
            List<CategoryArticleCountVO> list = articleService.getCategoryArticleCounts();
            return ApiResult.ok(list);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询分类文章数失败: " + e.getMessage());
        }
    }

    /**
     * 根据分类ID分页查询文章列表
     */
    @Operation(summary = "根据分类查询文章", description = "根据分类ID分页查询该分类下的文章列表")
    @GetMapping("/category/{categoryId}")
    public ApiResult<PageResult<ArticleVO>> getArticlesByCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable("categoryId") Long categoryId,
            @Parameter(description = "分页参数") PageParam pageParam) {
        try {
            ArticleQueryVO queryVO = new ArticleQueryVO();
            queryVO.setCategoryId(categoryId);
            PageResult<ArticleVO> pageResult = articleService.getArticleList(pageParam, queryVO);
            return ApiResult.ok(pageResult);
        } catch (Exception e) {
            return ApiResult.failed(500, "查询分类文章失败: " + e.getMessage());
        }
    }

    /**
     * 增加阅读量
     */
    @Operation(summary = "增加阅读量", description = "文章阅读时调用，阅读量+1")
    @PostMapping("/{id}/view")
    public ApiResult<Boolean> incrementViewCount(
            @Parameter(description = "文章ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = articleService.incrementViewCount(id);
            if (success) {
                return ApiResult.ok(true, "阅读量增加成功");
            } else {
                return ApiResult.failed(500, "阅读量增加失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "阅读量增加失败: " + e.getMessage());
        }
    }

    /**
     * 增加点赞数
     */
    @Operation(summary = "增加点赞数", description = "文章点赞时调用，点赞数+1")
    @PostMapping("/{id}/like")
    public ApiResult<Boolean> incrementLikeCount(
            @Parameter(description = "文章ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = articleService.incrementLikeCount(id);
            if (success) {
                return ApiResult.ok(true, "点赞成功");
            } else {
                return ApiResult.failed(500, "点赞失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "点赞失败: " + e.getMessage());
        }
    }

    /**
     * 增加评论数
     */
    @Operation(summary = "增加评论数", description = "文章新增评论时调用，评论数+1")
    @PostMapping("/{id}/comment")
    public ApiResult<Boolean> incrementCommentCount(
            @Parameter(description = "文章ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = articleService.incrementCommentCount(id);
            if (success) {
                return ApiResult.ok(true, "评论数增加成功");
            } else {
                return ApiResult.failed(500, "评论数增加失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "评论数增加失败: " + e.getMessage());
        }
    }

    /**
     * 发布文章
     */
    @Operation(summary = "发布文章", description = "将草稿状态的文章发布为已发布状态")
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> publishArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = articleService.publishArticle(id);
            if (success) {
                return ApiResult.ok(true, "文章发布成功");
            } else {
                return ApiResult.failed(500, "文章发布失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "文章发布失败: " + e.getMessage());
        }
    }

    /**
     * 取消发布文章（转为草稿）
     */
    @Operation(summary = "取消发布文章", description = "将已发布状态的文章转为草稿状态")
    @PostMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> unpublishArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable("id") Long id) {
        try {
            boolean success = articleService.unpublishArticle(id);
            if (success) {
                return ApiResult.ok(true, "文章已转为草稿");
            } else {
                return ApiResult.failed(500, "操作失败");
            }
        } catch (Exception e) {
            return ApiResult.failed(500, "操作失败: " + e.getMessage());
        }
    }


}
