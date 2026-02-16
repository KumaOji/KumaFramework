package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.main.domain.VO.CategoryVO;
import cn.kuma.blog.main.domain.entity.Category;
import cn.kuma.blog.main.mapper.CategoryMapper;
import cn.kuma.blog.main.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章分类服务实现
 *
 * @author Kuma
 * @version 1.0
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryVO> getCategoryList() {
        List<Category> list = categoryMapper.selectList(
                new QueryWrapper<Category>().orderByAsc("sort_order", "id"));
        Map<Long, Category> idMap = list.stream().collect(Collectors.toMap(Category::getId, c -> c));
        return list.stream()
                .map(c -> CategoryVO.builder()
                        .id(c.getId())
                        .code(c.getCode())
                        .name(c.getName())
                        .parentId(c.getParentId())
                        .level(c.getLevel())
                        .icon(c.getIcon())
                        .fullPath(buildFullPath(c, idMap))
                        .build())
                .collect(Collectors.toList());
    }

    private String buildFullPath(Category c, Map<Long, Category> idMap) {
        if (c.getParentId() == null) {
            return c.getName();
        }
        Category parent = idMap.get(c.getParentId());
        if (parent == null) {
            return c.getName();
        }
        return buildFullPath(parent, idMap) + "/" + c.getName();
    }

    @Override
    public Category getById(Long id) {
        return id == null ? null : categoryMapper.selectById(id);
    }

    @Override
    public List<Category> getChildren(Long parentId) {
        QueryWrapper<Category> qw = new QueryWrapper<Category>()
                .eq("parent_id", parentId)
                .orderByAsc("sort_order", "id");
        return categoryMapper.selectList(qw);
    }

    @Override
    public List<Long> getSelfAndDescendantIds(Long categoryId) {
        if (categoryId == null) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        collectSelfAndDescendants(categoryId, ids);
        return ids;
    }

    private void collectSelfAndDescendants(Long id, List<Long> ids) {
        ids.add(id);
        for (Category child : getChildren(id)) {
            collectSelfAndDescendants(child.getId(), ids);
        }
    }
}
