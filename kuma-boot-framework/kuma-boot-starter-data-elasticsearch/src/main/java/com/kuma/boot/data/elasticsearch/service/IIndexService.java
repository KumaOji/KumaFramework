package com.kuma.boot.data.elasticsearch.service;

import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.data.elasticsearch.model.IndexDto;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface IIndexService {
   boolean create(IndexDto indexDto) throws IOException;

   boolean delete(String indexName) throws IOException;

   PageResult<HashMap<String, String>> list(String queryStr, String indices) throws IOException;

   Map<String, Object> show(String indexName) throws IOException;
}
