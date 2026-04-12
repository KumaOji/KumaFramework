package com.kuma.boot.ddd.model.infrastructure;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.ddd.model.application.dto.PageQuery;
import com.kuma.boot.ddd.model.application.dto.PageResponse;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface MpPageConvertor {
   static Page toPage(PageQuery pageQuery) {
      Integer pageSize = 0 < pageQuery.getPageSize() ? pageQuery.getPageSize() : -1;
      Page page = Page.of((long)pageQuery.getPageIndex(), (long)pageSize);
      if (StringUtils.isNotBlank(pageQuery.getOrderBy())) {
         OrderItem orderItem = "ASC".equals(pageQuery.getOrderDirection()) ? OrderItem.asc(pageQuery.getOrderBy()) : OrderItem.desc(pageQuery.getOrderBy());
         page.addOrder(new OrderItem[]{orderItem});
      }

      return page;
   }

   static PageResponse toPageResponse(IPage pageResult) {
      return PageResponse.of(pageResult.getRecords(), Long.valueOf(pageResult.getTotal()).intValue(), Long.valueOf(pageResult.getCurrent()).intValue(), Long.valueOf(pageResult.getSize()).intValue());
   }

   static PageResponse toPageResponse(IPage pageResult, Function convertor) {
      List<R> resultList = Collections.emptyList();
      if (CollectionUtils.isNotEmpty(pageResult.getRecords())) {
         resultList = (List)pageResult.getRecords().stream().map(convertor).collect(Collectors.toList());
      }

      return PageResponse.of(resultList, Long.valueOf(pageResult.getTotal()).intValue(), Long.valueOf(pageResult.getCurrent()).intValue(), Long.valueOf(pageResult.getSize()).intValue());
   }
}
