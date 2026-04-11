package com.kuma.boot.data.elasticsearch.es.strategy.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.kuma.boot.data.elasticsearch.es.utils.ElasticUtil;
import com.kuma.boot.data.elasticsearch.es.utils.EsMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TermStrategy implements SearchStrategy {
   @Autowired
   private ElasticUtil elasticUtil;

   public TermStrategy() {
   }

   public String getModel() {
      return "term";
   }

   public Query searchHandle(EsMatch match) {
      return this.elasticUtil.termQuery(match);
   }
}
