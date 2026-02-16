package cn.kuma.blog.main.mapper;

import cn.kuma.blog.framework.mybatisplus.mapper.ExtendMapper;
import cn.kuma.blog.main.domain.entity.Article;
import cn.kuma.blog.main.domain.VO.CategoryArticleCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章Mapper接口
 *
 * @author Kuma
 * @version 1.0
 */
@Mapper
public interface ArticleMapper extends ExtendMapper<Article> {

    /**
     * 根据ID删除文章（逻辑删除）
     *
     * @param id 文章ID
     * @return 影响行数
     */
    int deleteByIdLogic(@Param("id") Long id);

    /**
     * 根据ID物理删除文章
     *
     * @param id 文章ID
     * @return 影响行数
     */
    int deleteByIdPhysical(@Param("id") Long id);

    /**
     * 增加阅读量
     *
     * @param id 文章ID
     * @return 影响行数
     */
    int incrementViewCount(@Param("id") Long id);

    /**
     * 增加点赞数
     *
     * @param id 文章ID
     * @return 影响行数
     */
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 增加评论数
     *
     * @param id 文章ID
     * @return 影响行数
     */
    int incrementCommentCount(@Param("id") Long id);

    /**
     * 按分类统计文章数（排除已删除 status=2）
     *
     * @return 每个分类的 (categoryId, count)，按 count 降序
     */
    List<CategoryArticleCountVO> selectCategoryArticleCounts();
}

