package com.kuma.boot.ddd.model.application.assembler;

import com.kuma.boot.ddd.model.application.dto.PageResponse;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageResponseAssembler {
   public static PageResponse toPageResp(PageResponse pageResp, Function convertor) {
      List<R> resultList = Collections.emptyList();
      if (null != pageResp.getData() && !pageResp.getData().isEmpty()) {
         resultList = (List)pageResp.getData().stream().map(convertor).collect(Collectors.toList());
      }

      return PageResponse.of(resultList, pageResp.getTotalCount(), pageResp.getPageSize(), pageResp.getPageIndex());
   }
}
