package com.kuma.boot.data.elasticsearch.es.strategy.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.kuma.boot.data.elasticsearch.es.utils.EsMatch;

public interface SearchStrategy {
   String getModel();

   Query searchHandle(EsMatch match);
}
