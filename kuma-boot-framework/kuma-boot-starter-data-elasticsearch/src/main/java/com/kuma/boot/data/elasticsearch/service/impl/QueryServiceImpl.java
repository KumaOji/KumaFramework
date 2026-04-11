package com.kuma.boot.data.elasticsearch.service.impl;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.data.elasticsearch.model.LogicDelDto;
import com.kuma.boot.data.elasticsearch.model.SearchDto;
import com.kuma.boot.data.elasticsearch.service.IAggregationService;
import com.kuma.boot.data.elasticsearch.service.IQueryService;
import com.kuma.boot.data.elasticsearch.service.ISearchService;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class QueryServiceImpl implements IQueryService {
   @Autowired(
      required = false
   )
   private ISearchService searchService;
   @Resource
   private IAggregationService aggregationService;

   public QueryServiceImpl() {
   }

   public PageResult<String> strQuery(String indexName, SearchDto searchDto) throws IOException {
      return this.strQuery(indexName, searchDto, (LogicDelDto)null);
   }

   public PageResult<String> strQuery(String indexName, SearchDto searchDto, LogicDelDto logicDelDto) throws IOException {
      this.setLogicDelQueryStr(searchDto, logicDelDto);
      return this.searchService.strQuery(indexName, searchDto);
   }

   private void setLogicDelQueryStr(SearchDto searchDto, LogicDelDto logicDelDto) {
      if (logicDelDto != null && StrUtil.isNotEmpty(logicDelDto.getLogicDelField()) && StrUtil.isNotEmpty(logicDelDto.getLogicNotDelValue())) {
         String queryStr = searchDto.getQueryStr();
         String var10000 = logicDelDto.getLogicDelField();
         String logicStr = var10000 + ":" + logicDelDto.getLogicNotDelValue();
         String result;
         if (StrUtil.isNotEmpty(queryStr)) {
            result = "(" + queryStr + ") AND " + logicStr;
         } else {
            result = logicStr;
         }

         searchDto.setQueryStr(result);
      }

   }

   public Map<String, Object> requestStatAgg(String indexName, String routing) throws IOException {
      return this.aggregationService.requestStatAgg(indexName, routing);
   }
}
