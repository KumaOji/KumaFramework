package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Article;
import com.kuma.cloud.blog.domain.query.ArticleQuery;
import com.kuma.cloud.blog.domain.vo.ArticleVO;
import com.kuma.cloud.blog.domain.vo.CategoryArticleCountVO;
import com.kuma.cloud.blog.domain.vo.CategoryVO;

import java.util.List;

public interface ArticleService {
    Long createArticle(Article article);
    boolean updateArticle(Article article);
    boolean deleteArticle(Long id);
    boolean deleteArticlePhysical(Long id);
    ArticleVO getArticleById(Long id);
    IPage<ArticleVO> getArticleList(PageQuery pageQuery, ArticleQuery queryVO);
    boolean incrementViewCount(Long id);
    boolean incrementLikeCount(Long id);
    boolean incrementCommentCount(Long id);
    boolean publishArticle(Long id);
    boolean unpublishArticle(Long id);
    List<CategoryVO> getCategoryList();
    List<CategoryArticleCountVO> getCategoryArticleCounts();
}
