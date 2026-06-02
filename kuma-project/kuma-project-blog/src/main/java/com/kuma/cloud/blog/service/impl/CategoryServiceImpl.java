package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kuma.cloud.blog.domain.entity.Category;
import com.kuma.cloud.blog.domain.vo.CategoryVO;
import com.kuma.cloud.blog.mapper.CategoryMapper;
import com.kuma.cloud.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Cached(name = "category:list", cacheType = CacheType.LOCAL, expire = 3600)
    @Override
    public List<CategoryVO> getCategoryList() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        // O(N) lookup map: id → Category
        Map<Long, Category> idMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, c -> c));
        return categories.stream().map(cat -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(cat, vo);
            vo.setFullPath(buildFullPath(cat, idMap));
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
        return getSelfAndDescendantIds(categoryId, allCategories);
    }

    @Override
    public List<Long> getSelfAndDescendantIds(Long categoryId, List<Category> allCategories) {
        // Build parent→children map once: O(N), then DFS is O(N) total
        Map<Long, List<Long>> childrenMap = buildChildrenMap(allCategories);
        List<Long> result = new ArrayList<>();
        result.add(categoryId);
        collectDescendantIds(categoryId, childrenMap, result);
        return result;
    }

    private static Map<Long, List<Long>> buildChildrenMap(List<Category> all) {
        Map<Long, List<Long>> map = new HashMap<>(all.size() * 2);
        for (Category cat : all) {
            if (cat.getParentId() != null) {
                map.computeIfAbsent(cat.getParentId(), k -> new ArrayList<>()).add(cat.getId());
            }
        }
        return map;
    }

    private void collectDescendantIds(Long parentId, Map<Long, List<Long>> childrenMap, List<Long> result) {
        List<Long> children = childrenMap.get(parentId);
        if (children == null) return;
        for (Long childId : children) {
            result.add(childId);
            collectDescendantIds(childId, childrenMap, result);
        }
    }

    // O(N) full-path build: walk up via id→Category map
    private String buildFullPath(Category cat, Map<Long, Category> idMap) {
        LinkedList<String> parts = new LinkedList<>();
        Category cur = cat;
        while (cur != null) {
            parts.addFirst(cur.getName());
            cur = cur.getParentId() != null ? idMap.get(cur.getParentId()) : null;
        }
        return String.join(" / ", parts);
    }
}
