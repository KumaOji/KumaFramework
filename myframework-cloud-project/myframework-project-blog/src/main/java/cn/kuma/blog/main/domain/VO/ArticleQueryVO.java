package cn.kuma.blog.main.domain.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章查询条件视图对象
 *
 * @author Kuma
 * @version 1.0
 */
@Schema(description = "文章查询条件")
@Data
public class ArticleQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章标题（模糊查询）
     */
    @Schema(description = "文章标题（模糊查询）", example = "Java")
    private String title;

    /**
     * 文章分类ID
     */
    @Schema(description = "文章分类ID", example = "1")
    private Long categoryId;

    /**
     * 作者ID
     */
    @Schema(description = "作者ID", example = "1")
    private Long authorId;

    /**
     * 文章状态：0-草稿，1-已发布，2-已删除
     */
    @Schema(description = "文章状态：0-草稿，1-已发布，2-已删除", example = "1")
    private Integer status;

    /**
     * 是否置顶：0-否，1-是
     */
    @Schema(description = "是否置顶：0-否，1-是", example = "0")
    private Integer isTop;

    /**
     * 是否推荐：0-否，1-是
     */
    @Schema(description = "是否推荐：0-否，1-是", example = "0")
    private Integer isRecommend;

    /**
     * 标签（模糊查询）
     */
    @Schema(description = "标签（模糊查询）", example = "Java")
    private String tag;
}

