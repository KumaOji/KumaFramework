package com.kuma.cloud.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kuma.cloud.blog.domain.entity.Article;
import com.kuma.cloud.blog.domain.vo.CategoryArticleCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    int deleteByIdLogic(@Param("id") Long id);

    int deleteByIdPhysical(@Param("id") Long id);

    int incrementViewCount(@Param("id") Long id);

    int incrementLikeCount(@Param("id") Long id);

    int incrementCommentCount(@Param("id") Long id);

    List<CategoryArticleCountVO> selectCategoryArticleCounts();
}
