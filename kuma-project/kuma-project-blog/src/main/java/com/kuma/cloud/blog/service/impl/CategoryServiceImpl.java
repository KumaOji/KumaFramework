package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kuma.cloud.blog.domain.entity.Category;
import com.kuma.cloud.blog.domain.vo.CategoryVO;
import com.kuma.cloud.blog.mapper.CategoryMapper;
import com.kuma.cloud.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryVO> getCategoryList() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        Map<Long, String> nameMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        return categories.stream().map(cat -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(cat, vo);
            vo.setFullPath(buildFullPath(cat, nameMap, categories));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        Category cat = categoryMapper.selectById(id);
        if (cat == null) return null;
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(cat, vo);
        return vo;
    }

    @Override
    public List<Long> getSelfAndDescendantIds(Long categoryId) {
        List<Category> allCategories = categoryMapper.selectList(new LambdaQueryWrapper<>());
        List<Long> result = new ArrayList<>();
        result.add(categoryId);
        collectDescendantIds(categoryId, allCategories, result);
        return result;
    }

    private void collectDescendantIds(Long parentId, List<Category> all, List<Long> result) {
        for (Category cat : all) {
            if (parentId.equals(cat.getParentId())) {
                result.add(cat.getId());
                collectDescendantIds(cat.getId(), all, result);
            }
        }
    }

    private String buildFullPath(Category cat, Map<Long, String> nameMap, List<Category> all) {
        LinkedList<String> parts = new LinkedList<>();
        parts.addFirst(cat.getName());
        Long parentId = cat.getParentId();
        while (parentId != null) {
            String parentName = nameMap.get(parentId);
            if (parentName != null) parts.addFirst(parentName);
            Long finalParentId = parentId;
            parentId = all.stream()
                    .filter(c -> c.getId().equals(finalParentId))
                    .map(Category::getParentId)
                    .findFirst().orElse(null);
        }
        return String.join(" / ", parts);
    }
}
