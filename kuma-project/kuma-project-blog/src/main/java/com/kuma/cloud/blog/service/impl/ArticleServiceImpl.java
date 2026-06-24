package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Article;
import com.kuma.cloud.blog.domain.entity.Category;
import com.kuma.cloud.blog.domain.query.ArticleQuery;
import com.kuma.cloud.blog.domain.vo.*;
import com.kuma.cloud.blog.mapper.ArticleMapper;
import com.kuma.cloud.blog.service.ArticleService;
import com.kuma.cloud.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final CategoryService categoryService;

    @Override
    public Long createArticle(Article article) {
        LocalDateTime now = LocalDateTime.now();
        article.setCreateTime(now);
        article.setUpdateTime(now);
        if (article.getStatus() != null && article.getStatus() == 1) {
            article.setPublishTime(now);
        }
        if (article.getViewCount() == null) article.setViewCount(0);
        if (article.getLikeCount() == null) article.setLikeCount(0);
        if (article.getCommentCount() == null) article.setCommentCount(0);
        if (article.getIsTop() == null) article.setIsTop(0);
        if (article.getIsRecommend() == null) article.setIsRecommend(0);
        articleMapper.insert(article);
        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(Article article) {
        article.setUpdateTime(LocalDateTime.now());
        if (article.getStatus() != null && article.getStatus() == 1) {
            // Only fetch status field — avoid pulling the large content column
            Integer oldStatus = articleMapper.selectStatusById(article.getId());
            if (oldStatus != null && oldStatus == 0) {
                article.setPublishTime(LocalDateTime.now());
            }
        }
        return articleMapper.updateById(article) > 0;
    }

    @Override
    public boolean deleteArticle(Long id) {
        return articleMapper.deleteByIdLogic(id) > 0;
    }

    @Override
    public boolean deleteArticlePhysical(Long id) {
        return articleMapper.deleteByIdPhysical(id) > 0;
    }

    @Cached(name = "article:detail:", cacheType = CacheType.LOCAL, expire = 600)
    @Override
    public ArticleVO getArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) return null;
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);
        return vo;
    }

    @Override
    public IPage<ArticleVO> getArticleList(PageQuery pageQuery, ArticleQuery queryVO) {
        QueryWrapper<Article> qw = new QueryWrapper<>();
        if (queryVO == null) queryVO = new ArticleQuery();
        qw.ne("status", 2);
        // Exclude content (TEXT/LONGTEXT) from list queries — callers set it null anyway
        qw.select(Article.class, info -> !"content".equals(info.getColumn()));

        if (StringUtils.isNotEmpty(queryVO.getTitle())) {
            qw.like("title", queryVO.getTitle());
        }
        if (queryVO.getCategoryId() != null) {
            List<Long> catIds = categoryService.getSelfAndDescendantIds(queryVO.getCategoryId());
            if (catIds.size() == 1) {
                qw.eq("category_id", queryVO.getCategoryId());
            } else {
                qw.in("category_id", catIds);
            }
        }
        if (queryVO.getAuthorId() != null) qw.eq("author_id", queryVO.getAuthorId());
        if (queryVO.getStatus() != null) qw.eq("status", queryVO.getStatus());
        if (queryVO.getIsTop() != null) qw.eq("is_top", queryVO.getIsTop());
        if (queryVO.getIsRecommend() != null) qw.eq("is_recommend", queryVO.getIsRecommend());
        if (StringUtils.isNotEmpty(queryVO.getTag())) qw.like("tags", queryVO.getTag());

        qw.orderByDesc("is_top").orderByDesc("publish_time").orderByDesc("create_time");

        int current = pageQuery != null && pageQuery.getCurrentPage() != null ? pageQuery.getCurrentPage() : 1;
        int size = pageQuery != null && pageQuery.getPageSize() != null ? pageQuery.getPageSize() : 7;
        IPage<Article> page = articleMapper.selectPage(new Page<>(current, size), qw);

        return page.convert(article -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(article, vo);
            // content was excluded at SQL level; size is stored separately in the column
            vo.setContentSize("0");
            return vo;
        });
    }

    @Override
    public boolean incrementViewCount(Long id) {
        return articleMapper.incrementViewCount(id) > 0;
    }

    @Override
    public boolean incrementLikeCount(Long id) {
        return articleMapper.incrementLikeCount(id) > 0;
    }

    @Override
    public boolean incrementCommentCount(Long id) {
        return articleMapper.incrementCommentCount(id) > 0;
    }

    @Override
    public boolean publishArticle(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(1);
        article.setPublishTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        return articleMapper.updateById(article) > 0;
    }

    @Override
    public boolean unpublishArticle(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(0);
        article.setUpdateTime(LocalDateTime.now());
        return articleMapper.updateById(article) > 0;
    }

    @Override
    public List<CategoryVO> getCategoryList() {
        return categoryService.getCategoryList();
    }

    @Cached(name = "category:counts", cacheType = CacheType.LOCAL, expire = 3600)
    @Override
    public List<CategoryArticleCountVO> getCategoryArticleCounts() {
        // Direct counts from DB (only published articles)
        Map<Long, Long> directCounts = articleMapper.selectCategoryArticleCounts().stream()
                .collect(Collectors.toMap(CategoryArticleCountVO::getCategoryId, CategoryArticleCountVO::getCount));

        // Category tree from cache — no extra DB query
        List<CategoryVO> categories = categoryService.getCategoryList();

        // Build parent→children map: O(N)
        Map<Long, List<Long>> childrenMap = new HashMap<>(categories.size() * 2);
        for (CategoryVO cat : categories) {
            if (cat.getParentId() != null) {
                childrenMap.computeIfAbsent(cat.getParentId(), k -> new ArrayList<>()).add(cat.getId());
            }
        }

        // Memoized DFS accumulation — each node computed exactly once: O(N) total
        Map<Long, Long> memo = new HashMap<>(categories.size() * 2);
        List<CategoryArticleCountVO> result = new ArrayList<>(categories.size());
        for (CategoryVO cat : categories) {
            CategoryArticleCountVO vo = new CategoryArticleCountVO();
            vo.setCategoryId(cat.getId());
            vo.setCategoryName(cat.getName());
            vo.setCount(accumulateCount(cat.getId(), directCounts, childrenMap, memo));
            result.add(vo);
        }
        result.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        return result;
    }

    private long accumulateCount(Long id, Map<Long, Long> direct,
                                 Map<Long, List<Long>> children, Map<Long, Long> memo) {
        Long cached = memo.get(id);
        if (cached != null) return cached;
        long count = direct.getOrDefault(id, 0L);
        List<Long> kids = children.get(id);
        if (kids != null) {
            for (Long kid : kids) {
                count += accumulateCount(kid, direct, children, memo);
            }
        }
        memo.put(id, count);
        return count;
    }
}
