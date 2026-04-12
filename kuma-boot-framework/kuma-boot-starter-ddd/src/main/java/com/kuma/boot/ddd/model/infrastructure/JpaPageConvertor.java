package com.kuma.boot.ddd.model.infrastructure;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.kuma.boot.ddd.model.application.dto.PageQuery;
import com.kuma.boot.ddd.model.application.dto.PageResponse;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public interface JpaPageConvertor {
   static Pageable toPage(PageQuery pageQuery) {
      PageRequest pageRequest = PageRequest.of(Math.max(pageQuery.getPageIndex() - 1, 0), pageQuery.getPageSize());
      if (StringUtils.hasText(pageQuery.getOrderBy())) {
         Sort sort = "ASC".equals(pageQuery.getOrderDirection()) ? Sort.by(new String[]{pageQuery.getOrderBy()}).ascending() : Sort.by(new String[]{pageQuery.getOrderBy()}).descending();
         pageRequest = pageRequest.withSort(sort);
      }

      return pageRequest;
   }

   static PageResponse toPageResponse(Page pageResult) {
      return PageResponse.of(pageResult.getContent(), Long.valueOf(pageResult.getTotalElements()).intValue(), pageResult.getPageable().getPageSize(), pageResult.getPageable().getPageNumber() + 1);
   }

   static PageResponse toPageResponse(Page pageResult, Function convertor) {
      List<R> resultList = Collections.emptyList();
      if (CollectionUtils.isNotEmpty(pageResult.getContent())) {
         resultList = (List)pageResult.getContent().stream().map(convertor).collect(Collectors.toList());
      }

      return PageResponse.of(resultList, Long.valueOf(pageResult.getTotalElements()).intValue(), pageResult.getPageable().getPageSize(), pageResult.getPageable().getPageNumber() + 1);
   }
}
