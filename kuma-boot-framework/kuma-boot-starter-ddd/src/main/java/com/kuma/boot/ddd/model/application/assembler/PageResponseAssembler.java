//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.application.assembler;

import com.kuma.boot.ddd.model.application.dto.PageResponse;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageResponseAssembler {
   public PageResponseAssembler() {
   }

   public static <T, R> PageResponse<R> toPageResp(PageResponse<T> pageResp, Function<T, R> convertor) {
      List<R> resultList = Collections.emptyList();
      if (null != pageResp.getData() && !pageResp.getData().isEmpty()) {
         resultList = (List)pageResp.getData().stream().map(convertor).collect(Collectors.toList());
      }

      return PageResponse.of(resultList, pageResp.getTotalCount(), pageResp.getPageSize(), pageResp.getPageIndex());
   }
}
