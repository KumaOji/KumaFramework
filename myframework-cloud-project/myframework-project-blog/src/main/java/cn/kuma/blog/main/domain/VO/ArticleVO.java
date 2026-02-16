package cn.kuma.blog.main.domain.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章视图对象
 *
 * @author Kuma
 * @version 1.0
 */
@Schema(description = "文章信息")
@Data
public class ArticleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章长度
     */
    private String contentSize;

    /**
     * 文章封面图片URL
     */
    private String coverImage;

    /**
     * 文章分类ID
     */
    private Long categoryId;

    /**
     * 文章分类名称
     */
    private String categoryName;

    /**
     * 文章标签（多个标签用逗号分隔）
     */
    private String tags;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 文章状态：0-草稿，1-已发布，2-已删除
     */
    private Integer status;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;

    /**
     * 是否推荐：0-否，1-是
     */
    private Integer isRecommend;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
}

