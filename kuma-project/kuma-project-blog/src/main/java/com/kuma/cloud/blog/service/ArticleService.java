package com.kuma.cloud.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Article;
import com.kuma.cloud.blog.domain.vo.*;

import java.util.List;

public interface ArticleService {
    Long createArticle(Article article);
    boolean updateArticle(Article article);
    boolean deleteArticle(Long id);
    boolean deleteArticlePhysical(Long id);
    ArticleVO getArticleById(Long id);
    IPage<ArticleVO> getArticleList(PageQuery pageQuery, ArticleQueryVO queryVO);
    boolean incrementViewCount(Long id);
    boolean incrementLikeCount(Long id);
    boolean incrementCommentCount(Long id);
    boolean publishArticle(Long id);
    boolean unpublishArticle(Long id);
    List<CategoryVO> getCategoryList();
    List<CategoryArticleCountVO> getCategoryArticleCounts();
}
