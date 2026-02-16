package cn.kuma.blog.main.service;

import cn.kuma.blog.main.domain.VO.CategoryVO;
import cn.kuma.blog.main.domain.entity.Category;

import java.util.List;

/**
 * 文章分类服务
 *
 * @author Kuma
 * @version 1.0
 */
public interface CategoryService {

    /**
     * 获取所有分类（转成 CategoryVO，含 fullPath）
     */
    List<CategoryVO> getCategoryList();

    /**
     * 根据 ID 查询分类
     */
    Category getById(Long id);

    /**
     * 获取直接子分类
     */
    List<Category> getChildren(Long parentId);

    /**
     * 获取自身及所有后代分类 ID（用于按分类查文章时包含子分类）
     */
    List<Long> getSelfAndDescendantIds(Long categoryId);
}
