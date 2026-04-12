package com.kuma.boot.ddd.model.infrastructure;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kuma.boot.ddd.model.application.dto.PageQuery;
import com.kuma.boot.ddd.model.application.dto.PageResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface PageHelperConvertor {
   static void startPage(PageQuery pageQuery) {
      String orderBy = (String)Stream.of(pageQuery.getOrderBy(), pageQuery.getOrderBy()).filter(Objects::nonNull).collect(Collectors.joining(" "));
      PageHelper.startPage(pageQuery.getPageIndex(), pageQuery.getPageSize(), orderBy);
   }

   static PageResponse toPageResponse(PageInfo pageInfo) {
      return PageResponse.of(pageInfo.getList(), Long.valueOf(pageInfo.getTotal()).intValue(), pageInfo.getPageSize(), pageInfo.getPageNum());
   }

   static PageResponse toPageResponse(List listResult) {
      return toPageResponse(PageInfo.of(listResult));
   }

   static PageResponse toPageResponse(PageInfo pageInfo, Function convertor) {
      List<R> resultList = Collections.emptyList();
      if (null != pageInfo.getList() && !pageInfo.getList().isEmpty()) {
         resultList = (List)pageInfo.getList().stream().map(convertor).collect(Collectors.toList());
      }

      return PageResponse.of(resultList, Long.valueOf(pageInfo.getTotal()).intValue(), pageInfo.getPageSize(), pageInfo.getPageNum());
   }

   static PageResponse toPageResponse(List listResult, Function convertor) {
      return toPageResponse(PageInfo.of(listResult), convertor);
   }
}
