package cn.kuma.blog.main.service;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.main.domain.VO.ArticleQueryVO;
import cn.kuma.blog.main.domain.VO.ArticleVO;
import cn.kuma.blog.main.domain.VO.CategoryArticleCountVO;
import cn.kuma.blog.main.domain.VO.CategoryVO;
import cn.kuma.blog.main.domain.entity.Article;

import java.util.List;



/**
 * 文章服务接口
 *
 * @author Kuma
 * @version 1.0
 */
public interface ArticleService {

    /**
     * 创建文章
     *
     * @param article 文章实体
     * @return 文章ID
     */
    Long createArticle(Article article);

    /**
     * 更新文章
     *
     * @param article 文章实体
     * @return 是否成功
     */
    boolean updateArticle(Article article);

    /**
     * 根据ID删除文章（逻辑删除）
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean deleteArticle(Long id);

    /**
     * 根据ID物理删除文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean deleteArticlePhysical(Long id);

    /**
     * 根据ID查询文章
     *
     * @param id 文章ID
     * @return 文章视图对象
     */
    ArticleVO getArticleById(Long id);

    /**
     * 分页查询文章列表
     *
     * @param pageParam 分页参数
     * @param queryVO 查询条件
     * @return 分页结果
     */
    PageResult<ArticleVO> getArticleList(PageParam pageParam, ArticleQueryVO queryVO);

    /**
     * 增加阅读量
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean incrementViewCount(Long id);

    /**
     * 增加点赞数
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean incrementLikeCount(Long id);

    /**
     * 增加评论数
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean incrementCommentCount(Long id);

    /**
     * 发布文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean publishArticle(Long id);

    /**
     * 取消发布文章（转为草稿）
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean unpublishArticle(Long id);

    /**
     * 按分类统计文章数（排除已删除）
     *
     * @return 各分类的文章数列表，含 categoryId、categoryName、count
     */
    List<CategoryArticleCountVO> getCategoryArticleCounts();

    /**
     * 获取所有分类列表
     *
     * @return 分类列表，包含 id、name、parentId、code、fullPath
     */
    List<CategoryVO> getCategoryList();
}

