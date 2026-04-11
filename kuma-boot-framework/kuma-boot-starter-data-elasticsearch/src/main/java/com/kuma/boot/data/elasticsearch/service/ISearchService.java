package com.kuma.boot.data.elasticsearch.service;

import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.data.elasticsearch.model.SearchDto;
import java.io.IOException;

public interface ISearchService {
   PageResult<String> strQuery(String indexName, SearchDto searchDto) throws IOException;
}
