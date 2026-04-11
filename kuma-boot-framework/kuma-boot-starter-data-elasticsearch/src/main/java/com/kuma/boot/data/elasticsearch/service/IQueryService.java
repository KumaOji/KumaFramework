package com.kuma.boot.data.elasticsearch.service;

import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.data.elasticsearch.model.LogicDelDto;
import com.kuma.boot.data.elasticsearch.model.SearchDto;
import java.io.IOException;
import java.util.Map;

public interface IQueryService {
   PageResult<String> strQuery(String indexName, SearchDto searchDto) throws IOException;

   PageResult<String> strQuery(String indexName, SearchDto searchDto, LogicDelDto logicDelDto) throws IOException;

   Map<String, Object> requestStatAgg(String indexName, String routing) throws IOException;
}
