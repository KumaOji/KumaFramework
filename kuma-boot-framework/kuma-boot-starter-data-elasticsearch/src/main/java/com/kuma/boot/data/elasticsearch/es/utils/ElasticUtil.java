package com.kuma.boot.data.elasticsearch.es.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.PrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch.cat.indices.IndicesRecord;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.elasticsearch.es.annotation.EsBean;
import com.kuma.boot.data.elasticsearch.es.annotation.EsField;
import com.kuma.boot.data.elasticsearch.es.strategy.property.PropertyStratgy;
import com.kuma.boot.data.elasticsearch.es.strategy.property.PropertyStratgyFactory;
import com.kuma.boot.data.elasticsearch.es.strategy.query.SearchStrategy;
import com.kuma.boot.data.elasticsearch.es.strategy.query.SearchStrategyFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ElasticUtil {
   @Autowired
   private ElasticsearchClient client;
   @Autowired
   private SearchStrategyFactory searchStrategyFactory;
   @Autowired
   private PropertyStratgyFactory propertyStratgyFactory;

   public ElasticUtil() {
   }

   public Query getSearchModel(EsMatch model) {
      SearchStrategy searchStrategy = this.searchStrategyFactory.handleStrategy(model.getMode());
      if (searchStrategy == null) {
         LogUtils.info("\u672a\u627e\u5230\u8be5\u7b56\u7565\uff1a" + model.getMode(), new Object[0]);
         return null;
      } else {
         return searchStrategy.searchHandle(model);
      }
   }

   /** @deprecated */
   @Deprecated
   public void createIndex(String indexName) throws Exception {
      this.client.indices().create((c) -> c.index(indexName));
   }

   public void createIndex(Class<?> clazz) throws Exception {
      EsBean document = (EsBean)clazz.getAnnotation(EsBean.class);
      String index = document.index();

      try {
         if (!this.exists(index)) {
            CreateIndexResponse response = this.client.indices().create((c) -> {
               c.settings((s) -> s.numberOfShards(document.shards()).numberOfReplicas(document.replicas()));
               c.index(index);
               c.mappings(this.createMapping(clazz));
               return c;
            });
            LogUtils.info("\u521b\u5efa\u7d22\u5f15\u6210\u529f:" + response.index(), new Object[0]);
            response.acknowledged();
         } else {
            LogUtils.info("===========\u7d22\u5f15\u5df2\u5b58\u5728===========", new Object[0]);
         }
      } catch (Exception e) {
         LogUtils.error(e);
      }

   }

   public boolean exists(String indexName) throws Exception {
      BooleanResponse response = this.client.indices().exists((e) -> e.index(indexName, new String[0]));
      return response.value();
   }

   public List<String> getIndexList() throws Exception {
      return this.client.cat().indices().indices().stream().map(IndicesRecord::index).toList();
   }

   public boolean removeIndex(String indexName) throws Exception {
      DeleteIndexResponse response = this.client.indices().delete((d) -> d.index(indexName, new String[0]));
      return response.acknowledged();
   }

   public void addDocument(String indexName, Map document) throws Exception {
      this.client.index((i) -> i.index(indexName).id(document.get("ID").toString()).document(document));
   }

   public void addDocument(EsDoc doc) throws Exception {
      this.client.index((i) -> i.index(doc.getIndexName()).id(doc.getId()).document(doc));
   }

   public void updateDocument(String indexName, Map document) throws Exception {
      this.client.update((u) -> u.index(indexName).id(document.get("ID").toString()).doc(document), HashMap.class);
   }

   public HashMap queryById(String indexName, String id) throws Exception {
      GetResponse<HashMap> request = this.client.get((s) -> s.index(indexName).id(id), HashMap.class);
      return (HashMap)request.source();
   }

   public List<Map<String, Object>> queryByField(EsQuery queryDTO) throws Exception {
      SearchRequest request = SearchRequest.of((s) -> {
         s.index(queryDTO.getIndexName(), new String[0]);
         s.query((q) -> q.bool(this.handleCondition(queryDTO.getMatch())));
         s.from(queryDTO.getFrom());
         s.size(queryDTO.getSize());
         if (queryDTO.getColumn() != null && !queryDTO.getColumn().isEmpty()) {
            s.source((ss) -> ss.filter((f) -> f.includes(queryDTO.getColumn())));
         }

         if (queryDTO.getSorts() != null && !queryDTO.getSorts().isEmpty()) {
            s.sort(this.sort(queryDTO.getSorts()));
         }

         s.trackTotalHits((t) -> t.enabled(true));
         return s;
      });
      SearchResponse<HashMap> response = this.client.search(request, HashMap.class);
      long total = response.hits().total().value();
      LogUtils.info("\u67e5\u8be2\u6570\u636e\u8017\u65f6:" + response.took(), new Object[0]);
      List<Hit<HashMap>> hits = response.hits().hits();
      response.hits().maxScore();
      Iterator<Hit<HashMap>> iterator = hits.iterator();
      List<Map<String, Object>> data = new ArrayList();

      while(iterator.hasNext()) {
         Hit<HashMap> decodeBeanHit = (Hit)iterator.next();
         Map<String, Object> map = (Map)decodeBeanHit.source();
         data.add(map);
      }

      return data;
   }

   public long queryCountByFiled(EsQuery dto) throws Exception {
      CountResponse count = this.client.count((c) -> c.index(dto.getIndexName(), new String[0]).query((q) -> q.bool(this.handleCondition(dto.getMatch()))));
      long total = count.count();
      return total;
   }

   public JSONObject queryPageByField(EsQuery dto) throws Exception {
      JSONObject result = new JSONObject();
      result.put("data", this.queryByField(dto));
      result.put("total", this.queryCountByFiled(dto));
      return result;
   }

   public BoolQuery handleCondition(List<EsMatch> match) {
      BoolQuery.Builder bool = new BoolQuery.Builder();
      List<Query> termQueryList = new ArrayList();
      List<Query> matchQueryList = new ArrayList();
      match.forEach((item) -> {
         if (StringUtils.hasLength(item.getWord())) {
            if (!"term".equals(item.getMode()) && !"range".equals(item.getMode())) {
               matchQueryList.add(this.getSearchModel(item));
            } else {
               termQueryList.add(this.getSearchModel(item));
            }
         }

      });
      bool.filter(termQueryList);
      bool.must(matchQueryList);
      return bool.build();
   }

   public Query matchQuery(String word) {
      return MatchQuery.of((m) -> m.query(word))._toQuery();
   }

   public Query termQuery(EsMatch match) {
      return TermQuery.of((t) -> t.field(match.getField()).value(match.getWord()))._toQuery();
   }

   public Query tremsQuery(EsMatch match) {
      return TermsQuery.of((t) -> t.field(match.getField()).terms((w) -> w.value(match.getWords())))._toQuery();
   }

   public Query fuzzyQuery(EsMatch match) {
      return FuzzyQuery.of((f) -> f.field(match.getField()).value(match.getWord()))._toQuery();
   }

   public Query rangeQuery(EsMatch range) {
      return null;
   }

   public Query prefixQuery(EsMatch match) {
      return PrefixQuery.of((p) -> p.field(match.getField()).value(match.getWord()))._toQuery();
   }

   public List<SortOptions> sort(List<EsSort> sorts) {
      List<SortOptions> sortOptions = new ArrayList();
      sorts.forEach((item) -> {
         SortOptions.Builder sb = new SortOptions.Builder();
         sb.field((f) -> {
            f.order(SortOrder.valueOf(item.getSort()));
            f.field(item.getField());
            return f;
         });
         sortOptions.add(sb.build());
      });
      return sortOptions;
   }

   public void bulkIndex(List<Map<String, Object>> data, String indexName) throws Exception {
      List<BulkOperation> list = new ArrayList();
      data.forEach((d) -> list.add(BulkOperation.of((o) -> o.index((i) -> (ObjectBuilder)i.document(d).id(d.get("ID").toString())))));
      this.client.bulk((b) -> b.index(indexName).operations(list));
   }

   public TypeMapping createMapping(Class<?> clazz) {
      try {
         return TypeMapping.of((t) -> t.properties(this.handleProperties(clazz.getDeclaredFields())));
      } catch (Exception e) {
         LogUtils.error(e);
         return null;
      }
   }

   public Map<String, Property> handleProperties(Field[] field) {
      Map<String, Property> map = new HashMap();

      for(Field declaredField : field) {
         declaredField.setAccessible(true);
         if (declaredField.isAnnotationPresent(EsField.class)) {
            EsField esField = (EsField)declaredField.getAnnotation(EsField.class);
            PropertyStratgy propertyStratgy = this.propertyStratgyFactory.handleStrategy(esField.type().getType());
            if (propertyStratgy != null) {
               Property property = propertyStratgy.handleProperty(esField, declaredField, this);
               map.put(declaredField.getName(), property);
            } else {
               LogUtils.info("\u8be5\u7b56\u7565\u672a\u6dfb\u52a0\u5230\u5de5\u5382\uff1a" + esField.type().getType(), new Object[0]);
            }
         }
      }

      return map;
   }
}
