package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.vo.CategoryVO;
import java.util.List;

public interface CategoryService {
    List<CategoryVO> getCategoryList();
    CategoryVO getCategoryById(Long id);
    List<Long> getSelfAndDescendantIds(Long categoryId);
}
