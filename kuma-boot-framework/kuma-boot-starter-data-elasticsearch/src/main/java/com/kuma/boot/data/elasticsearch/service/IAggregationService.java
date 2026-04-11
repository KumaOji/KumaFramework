package com.kuma.boot.data.elasticsearch.service;

import java.io.IOException;
import java.util.Map;

public interface IAggregationService {
   Map<String, Object> requestStatAgg(String indexName, String routing) throws IOException;
}
