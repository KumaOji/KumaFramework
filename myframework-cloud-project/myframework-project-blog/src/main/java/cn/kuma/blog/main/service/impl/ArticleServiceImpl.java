package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.framework.mybatisplus.toolkit.PageUtil;
import cn.kuma.blog.main.domain.VO.ArticleQueryVO;
import cn.kuma.blog.main.domain.VO.ArticleVO;
import cn.kuma.blog.main.domain.VO.CategoryArticleCountVO;
import cn.kuma.blog.main.domain.VO.CategoryVO;
import cn.kuma.blog.main.domain.entity.Article;
import cn.kuma.blog.main.mapper.ArticleMapper;
import cn.kuma.blog.main.service.ArticleService;
import cn.kuma.blog.main.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * @author Kuma
 * @version 1.0
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createArticle(Article article) {
        LocalDateTime now = LocalDateTime.now();
        article.setCreateTime(now);
        article.setUpdateTime(now);

        // 如果状态为已发布，设置发布时间
        if (article.getStatus() != null && article.getStatus() == 1) {
            article.setPublishTime(now);
        }

        // 初始化统计数据
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        if (article.getLikeCount() == null) {
            article.setLikeCount(0);
        }
        if (article.getCommentCount() == null) {
            article.setCommentCount(0);
        }
        if (article.getIsTop() == null) {
            article.setIsTop(0);
        }
        if (article.getIsRecommend() == null) {
            article.setIsRecommend(0);
        }

        articleMapper.insert(article);
        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(Article article) {
        article.setUpdateTime(LocalDateTime.now());

        // 如果状态从草稿变为已发布，设置发布时间
        Article oldArticle = articleMapper.selectById(article.getId());
        if (oldArticle != null && oldArticle.getStatus() != null && oldArticle.getStatus() == 0
            && article.getStatus() != null && article.getStatus() == 1) {
            article.setPublishTime(LocalDateTime.now());
        }

        return articleMapper.updateById(article) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(Long id) {
        return articleMapper.deleteByIdLogic(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticlePhysical(Long id) {
        return articleMapper.deleteByIdPhysical(id) > 0;
    }

    @Override
    public ArticleVO getArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return null;
        }

        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        return articleVO;
    }


    @Override
    public PageResult<ArticleVO> getArticleList(PageParam pageParam, ArticleQueryVO queryVO) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        if (queryVO == null) {
            queryVO = new ArticleQueryVO();
        }

        // 排除已删除的文章
        queryWrapper.ne("status", 2);

        // 标题模糊查询
        Optional.ofNullable(queryVO.getTitle())
                .filter(StringUtils::isNotEmpty)
                .ifPresent(title -> queryWrapper.like("title", title));

        // 分类ID（支持查询父分类及其所有子分类）
        Optional.ofNullable(queryVO.getCategoryId())
                .ifPresent(categoryId -> {
                    List<Long> categoryIds = categoryService.getSelfAndDescendantIds(categoryId);
                    if (categoryIds.size() == 1) {
                        queryWrapper.eq("category_id", categoryId);
                    } else {
                        queryWrapper.in("category_id", categoryIds);
                    }
                });

        // 作者ID
        Optional.ofNullable(queryVO.getAuthorId())
                .ifPresent(authorId -> queryWrapper.eq("author_id", authorId));

        // 状态
        Optional.ofNullable(queryVO.getStatus())
                .ifPresent(status -> queryWrapper.eq("status", status));

        // 是否置顶
        Optional.ofNullable(queryVO.getIsTop())
                .ifPresent(isTop -> queryWrapper.eq("is_top", isTop));

        // 是否推荐
        Optional.ofNullable(queryVO.getIsRecommend())
                .ifPresent(isRecommend -> queryWrapper.eq("is_recommend", isRecommend));

        // 标签模糊查询
        Optional.ofNullable(queryVO.getTag())
                .filter(StringUtils::isNotEmpty)
                .ifPresent(tag -> queryWrapper.like("tags", tag));

        // 排序：置顶优先，然后按发布时间降序，最后按创建时间降序
        queryWrapper.orderByDesc("is_top")
                    .orderByDesc("publish_time")
                    .orderByDesc("create_time");

        // 分页查询
        IPage<Article> articlePage = articleMapper.selectPage(PageUtil.prodPage(pageParam), queryWrapper);

        // 转换为 VO（列表不返回 content，减少体积；列表补充 contentSize；详情请用 getArticleById）
        List<ArticleVO> articleVOList = articlePage.getRecords().stream()
                .map(article -> {
                    ArticleVO articleVO = new ArticleVO();
                    BeanUtils.copyProperties(article, articleVO);
                    String content = article.getContent();
                    articleVO.setContentSize(content == null ? "0" : String.valueOf(content.length()));
                    articleVO.setContent(null);
                    return articleVO;
                })
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<ArticleVO> pageResult = new PageResult<>();
        pageResult.setRecords(articleVOList);
        pageResult.setTotal(articlePage.getTotal());

        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementViewCount(Long id) {
        return articleMapper.incrementViewCount(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementLikeCount(Long id) {
        return articleMapper.incrementLikeCount(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementCommentCount(Long id) {
        return articleMapper.incrementCommentCount(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishArticle(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(1);
        article.setPublishTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        return articleMapper.updateById(article) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unpublishArticle(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(0);
        article.setUpdateTime(LocalDateTime.now());
        return articleMapper.updateById(article) > 0;
    }

    @Override
    public List<CategoryArticleCountVO> getCategoryArticleCounts() {
        List<CategoryArticleCountVO> dbList = articleMapper.selectCategoryArticleCounts();
        java.util.Map<Long, Long> countMap = dbList.stream()
                .collect(Collectors.toMap(
                        CategoryArticleCountVO::getCategoryId,
                        CategoryArticleCountVO::getCount,
                        (a, b) -> a
                ));
        List<CategoryVO> categories = categoryService.getCategoryList();
        List<CategoryArticleCountVO> result = new java.util.ArrayList<>();
        for (CategoryVO cat : categories) {
            List<Long> ids = categoryService.getSelfAndDescendantIds(cat.getId());
            long count = ids.stream().mapToLong(id -> countMap.getOrDefault(id, 0L)).sum();
            CategoryArticleCountVO vo = new CategoryArticleCountVO();
            vo.setCategoryId(cat.getId());
            vo.setCategoryName(cat.getName());
            vo.setCount(count);
            result.add(vo);
        }
        result.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        return result;
    }

    @Override
    public List<CategoryVO> getCategoryList() {
        return categoryService.getCategoryList();
    }
}

