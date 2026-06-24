package com.kuma.boot.data.elasticsearch.kcloud.template;

import cn.hutool.json.JSONUtil;
import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.analysis.Analyzer;
import co.elastic.clients.elasticsearch._types.analysis.TokenFilter;
import co.elastic.clients.elasticsearch._types.mapping.DynamicMapping;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.IndexSettingsAnalysis;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.frameworkset.util.StringUtil;
import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.elasticsearch.kcloud.annotation.Analysis;
import com.kuma.boot.data.elasticsearch.kcloud.annotation.Args;
import com.kuma.boot.data.elasticsearch.kcloud.annotation.Document;
import com.kuma.boot.data.elasticsearch.kcloud.annotation.Filter;
import com.kuma.boot.data.elasticsearch.kcloud.annotation.Index;
import com.kuma.boot.data.elasticsearch.kcloud.annotation.Setting;
import com.kuma.boot.data.elasticsearch.kcloud.annotation.Type;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
   prefix = "kuma.boot.data.elasticsearch",
   name = "enabled",
   havingValue = "true"
)
@ConditionalOnBean({ElasticsearchClient.class, ElasticsearchAsyncClient.class})
public class ElasticsearchTemplate {
   private final ElasticsearchClient elasticsearchClient;
   private final ElasticsearchAsyncClient elasticsearchAsyncClient;

   public ElasticsearchTemplate(ElasticsearchClient elasticsearchClient, ElasticsearchAsyncClient elasticsearchAsyncClient) {
      this.elasticsearchClient = elasticsearchClient;
      this.elasticsearchAsyncClient = elasticsearchAsyncClient;
   }

   public <TDocument> CompletableFuture<Boolean> asyncCreateIndex(String name, String alias, Class<TDocument> clazz) {
      return this.asyncExist(List.of(name)).thenApplyAsync((resp) -> {
         if (resp) {
            LogUtils.error("\u7d22\u5f15\uff1a{} -> \u521b\u5efa\u7d22\u5f15\u5931\u8d25\uff0c\u7d22\u5f15\u5df2\u5b58\u5728", new Object[]{name});
            return Boolean.FALSE;
         } else {
            return Boolean.TRUE;
         }
      }).thenApplyAsync((resp) -> {
         if (resp) {
            Document document = this.convert(name, alias, clazz);
            this.elasticsearchAsyncClient.indices().create(this.getCreateIndexRequest(document)).thenApplyAsync((response) -> {
               if (response.acknowledged()) {
                  LogUtils.info("\u7d22\u5f15\uff1a{} -> \u521b\u5efa\u7d22\u5f15\u6210\u529f", new Object[]{name});
                  return Boolean.TRUE;
               } else {
                  LogUtils.error("\u7d22\u5f15\uff1a{} -> \u521b\u5efa\u7d22\u5f15\u5931\u8d25", new Object[]{name});
                  return Boolean.FALSE;
               }
            });
         }

         return Boolean.FALSE;
      });
   }

   public <TDocument> void createIndex(String name, String alias, Class<TDocument> clazz) throws IOException {
      if (this.exist(List.of(name))) {
         LogUtils.error("\u7d22\u5f15\uff1a{} -> \u521b\u5efa\u7d22\u5f15\u5931\u8d25\uff0c\u7d22\u5f15\u5df2\u5b58\u5728", new Object[]{name});
      } else {
         Document document = this.convert(name, alias, clazz);
         CreateIndexResponse createIndexResponse = this.elasticsearchClient.indices().create(this.getCreateIndexRequest(document));
         boolean acknowledged = createIndexResponse.acknowledged();
         if (acknowledged) {
            LogUtils.info("\u7d22\u5f15\uff1a{} -> \u521b\u5efa\u7d22\u5f15\u6210\u529f", new Object[]{name});
         } else {
            LogUtils.error("\u7d22\u5f15\uff1a{} -> \u521b\u5efa\u7d22\u5f15\u5931\u8d25", new Object[]{name});
         }

      }
   }

   public void deleteIndex(List<String> names) throws IOException {
      if (!this.exist(names)) {
         LogUtils.error("\u7d22\u5f15\uff1a{} -> \u5220\u9664\u7d22\u5f15\u5931\u8d25\uff0c\u7d22\u5f15\u4e0d\u5b58\u5728", new Object[]{StringUtil.collectionToDelimitedString(names, SymbolConstants.COMMA)});
      } else {
         DeleteIndexResponse deleteIndexResponse = this.elasticsearchClient.indices().delete(this.getDeleteIndexRequest(names));
         boolean acknowledged = deleteIndexResponse.acknowledged();
         if (acknowledged) {
            LogUtils.info("\u7d22\u5f15\uff1a{} -> \u5220\u9664\u7d22\u5f15\u6210\u529f", new Object[]{StringUtil.collectionToDelimitedString(names, SymbolConstants.COMMA)});
         } else {
            LogUtils.error("\u7d22\u5f15\uff1a{} -> \u5220\u9664\u7d22\u5f15\u5931\u8d25", new Object[]{StringUtil.collectionToDelimitedString(names, SymbolConstants.COMMA)});
         }

      }
   }

   public Map<String, IndexState> getIndex(List<String> names) throws IOException {
      return this.elasticsearchClient.indices().get(this.getIndexRequest(names)).indices();
   }

   public void createDocument(String index, String id, Object obj) throws IOException {
      IndexResponse response = this.elasticsearchClient.index((idx) -> idx.index(index).refresh(Refresh.True).id(id).document(obj));
      if (StringUtil.isNotEmpty(response.result().jsonValue())) {
         LogUtils.info("\u7d22\u5f15\uff1a{} -> \u540c\u6b65\u7d22\u5f15\u6210\u529f", new Object[]{index});
      } else {
         LogUtils.error("\u7d22\u5f15\uff1a{} -> \u540c\u6b65\u7d22\u5f15\u5931\u8d25", new Object[]{index});
      }

   }

   public CompletableFuture<Boolean> asyncCreateDocument(String index, String id, Object obj) {
      return this.elasticsearchAsyncClient.index((idx) -> idx.index(index).refresh(Refresh.True).id(id).document(obj)).thenApplyAsync((resp) -> {
         if (StringUtil.isNotEmpty(resp.result().jsonValue())) {
            LogUtils.info("\u7d22\u5f15\uff1a{} -> \u5f02\u6b65\u540c\u6b65\u7d22\u5f15\u6210\u529f", new Object[]{index});
            return Boolean.TRUE;
         } else {
            LogUtils.error("\u7d22\u5f15\uff1a{} -> \u5f02\u6b65\u540c\u6b65\u7d22\u5f15\u5931\u8d25", new Object[]{index});
            return Boolean.FALSE;
         }
      });
   }

   public void bulkCreateDocument(String index, Map<String, Object> map) throws IOException {
      boolean errors = this.elasticsearchClient.bulk((bulk) -> bulk.index(index).refresh(Refresh.True).operations(this.getBulkOperations(map))).errors();
      if (errors) {
         LogUtils.error("\u7d22\u5f15\uff1a{} -> \u6279\u91cf\u540c\u6b65\u7d22\u5f15\u5931\u8d25", new Object[]{index});
      } else {
         LogUtils.info("\u7d22\u5f15\uff1a{} -> \u6279\u91cf\u540c\u6b65\u7d22\u5f15\u6210\u529f", new Object[]{index});
      }

   }

   public CompletableFuture<Boolean> asyncBulkCreateDocument(String index, Map<String, Object> map) {
      return this.elasticsearchAsyncClient.bulk((bulk) -> bulk.index(index).refresh(Refresh.True).operations(this.getBulkOperations(map))).thenApplyAsync((resp) -> {
         if (resp.errors()) {
            LogUtils.error("\u7d22\u5f15\uff1a{} -> \u5f02\u6b65\u6279\u91cf\u540c\u6b65\u7d22\u5f15\u5931\u8d25", new Object[]{index});
            return Boolean.FALSE;
         } else {
            LogUtils.info("\u7d22\u5f15\uff1a{} -> \u5f02\u6b65\u6279\u91cf\u540c\u6b65\u7d22\u5f15\u6210\u529f", new Object[]{index});
            return Boolean.TRUE;
         }
      });
   }

   public boolean exist(List<String> names) throws IOException {
      return this.elasticsearchClient.indices().exists(this.getExists(names)).value();
   }

   public CompletableFuture<Boolean> asyncExist(List<String> names) {
      return this.elasticsearchAsyncClient.indices().exists(this.getExists(names)).thenApplyAsync(BooleanResponse::value);
   }

   @SuppressWarnings("unchecked")
   private List<BulkOperation> getBulkOperations(Map<String, Object> map) {
      return map.entrySet().stream().map((entry) -> BulkOperation.of((idx) -> idx.index((fn) -> ((IndexOperation.Builder)fn.id((String)entry.getKey())).document(entry.getValue())))).toList();
   }

   private ExistsRequest getExists(List<String> names) {
      ExistsRequest.Builder existBuilder = new ExistsRequest.Builder();
      existBuilder.index(names);
      return existBuilder.build();
   }

   private GetIndexRequest getIndexRequest(List<String> names) {
      GetIndexRequest.Builder getIndexbuilder = new GetIndexRequest.Builder();
      getIndexbuilder.index(names);
      return getIndexbuilder.build();
   }

   private DeleteIndexRequest getDeleteIndexRequest(List<String> names) {
      DeleteIndexRequest.Builder deleteIndexBuilder = new DeleteIndexRequest.Builder();
      deleteIndexBuilder.index(names);
      return deleteIndexBuilder.build();
   }

   private CreateIndexRequest getCreateIndexRequest(Document document) {
      CreateIndexRequest.Builder createIndexbuilder = new CreateIndexRequest.Builder();
      createIndexbuilder.aliases(document.getAlias(), (fn) -> fn.isWriteIndex(true));
      return createIndexbuilder.index(document.getName()).mappings(this.getMappings(document)).settings(this.getSettings(document)).build();
   }

   private IndexSettings getSettings(Document document) {
      Document.Setting setting = document.getSetting();
      IndexSettings.Builder settingBuilder = new IndexSettings.Builder();
      settingBuilder.numberOfShards(String.valueOf(setting.getShards()));
      settingBuilder.numberOfReplicas(String.valueOf(setting.getReplicas()));
      settingBuilder.refreshInterval((fn) -> fn.time(setting.getRefreshInterval()));
      settingBuilder.analysis(this.getAnalysisBuilder(document));
      return settingBuilder.build();
   }

   private IndexSettingsAnalysis getAnalysisBuilder(Document document) {
      IndexSettingsAnalysis.Builder settingsAnalysisBuilder = new IndexSettingsAnalysis.Builder();
      Document.Analysis analysis = document.getAnalysis();
      List<Document.Filter> filters = analysis.getFilters();
      List<Document.Analyzer> analyzers = analysis.getAnalyzers();
      analyzers.forEach((item) -> settingsAnalysisBuilder.analyzer(item.getName(), this.getAnalyzer(item.getArgs())));
      filters.forEach((item) -> settingsAnalysisBuilder.filter(item.getName(), this.getFilter(item.getOptions())));
      return settingsAnalysisBuilder.build();
   }

   private TypeMapping getMappings(Document document) {
      TypeMapping.Builder mappingBuilder = new TypeMapping.Builder();
      mappingBuilder.dynamic(DynamicMapping.True);
      List<Document.Mapping> mappings = document.getMappings();
      mappings.forEach((item) -> this.setProperties(mappingBuilder, item));
      return mappingBuilder.build();
   }

   private TokenFilter getFilter(List<Document.Option> options) {
      TokenFilter.Builder filterBuilder = new TokenFilter.Builder();
      Map<String, String> map = options.stream().collect(Collectors.toMap(Document.Option::getKey, Document.Option::getValue));
      TokenFilter result = filterBuilder.definition((fn) -> fn.withJson(new ByteArrayInputStream(JSONUtil.toJsonStr(map).getBytes(StandardCharsets.UTF_8)))).build();
      return result;
   }

   private Analyzer getAnalyzer(Document.Args args) {
      Analyzer.Builder analyzerBuilder = new Analyzer.Builder();
      analyzerBuilder.custom((fn) -> fn.filter(args.getFilter(), new String[0]).tokenizer(args.getTokenizer()));
      return analyzerBuilder.build();
   }

   private void setProperties(TypeMapping.Builder mappingBuilder, Document.Mapping mapping) {
      Type type = mapping.getType();
      String field = mapping.getField();
      String analyzer = mapping.getAnalyzer();
      boolean fielddata = mapping.isFielddata();
      String searchAnalyzer = mapping.getSearchAnalyzer();
      boolean eagerGlobalOrdinals = mapping.isEagerGlobalOrdinals();
      switch (type) {
         case TEXT -> mappingBuilder.properties(field, (fn) -> fn.text((t) -> t.index(true).fielddata(fielddata).eagerGlobalOrdinals(eagerGlobalOrdinals).searchAnalyzer(searchAnalyzer).analyzer(analyzer)));
         case KEYWORD -> mappingBuilder.properties(field, (fn) -> fn.keyword((t) -> t.eagerGlobalOrdinals(eagerGlobalOrdinals)));
         case LONG -> mappingBuilder.properties(field, (fn) -> fn.long_((t) -> t));
      }

   }

   private <TDocument> Document convert(String name, String alias, Class<TDocument> clazz) {
      boolean annotationPresent = clazz.isAnnotationPresent(Index.class);
      if (annotationPresent) {
         Index index = (Index)clazz.getAnnotation(Index.class);
         return Document.builder().name(name).alias(StringUtil.isNotEmpty(alias) ? alias : name).mappings(this.getMappings(clazz)).setting(this.getSetting(index)).analysis(this.getAnalysis(index)).build();
      } else {
         throw new RuntimeException("Not found @Index");
      }
   }

   private Document.Analysis getAnalysis(Index index) {
      Analysis analysis = index.analysis();
      com.kuma.boot.data.elasticsearch.kcloud.annotation.Analyzer[] analyzers = analysis.analyzers();
      Filter[] filters = analysis.filters();
      return new Document.Analysis(this.getFilters(filters), this.getAnalyzer(analyzers));
   }

   private List<Document.Analyzer> getAnalyzer(com.kuma.boot.data.elasticsearch.kcloud.annotation.Analyzer[] analyzers) {
      return Arrays.stream(analyzers).map((item) -> new Document.Analyzer(item.name(), this.getArgs(item.args()))).toList();
   }

   private Document.Args getArgs(Args args) {
      return new Document.Args(args.filter(), args.tokenizer());
   }

   private List<Document.Filter> getFilters(Filter[] filters) {
      return Arrays.stream(filters).map((item) -> new Document.Filter(item.name(), this.getOptions(item))).toList();
   }

   private List<Document.Option> getOptions(Filter filter) {
      return Arrays.stream(filter.options()).map((item) -> new Document.Option(item.key(), item.value())).toList();
   }

   private Document.Setting getSetting(Index index) {
      Setting setting = index.setting();
      return new Document.Setting(setting.shards(), setting.replicas(), setting.refreshInterval());
   }

   private <TDocument> List<Document.Mapping> getMappings(Class<TDocument> clazz) {
      Field[] fields = clazz.getDeclaredFields();
      return Arrays.stream(fields).filter((item) -> item.isAnnotationPresent(com.kuma.boot.data.elasticsearch.kcloud.annotation.Field.class)).map((item) -> this.getMapping(item, (com.kuma.boot.data.elasticsearch.kcloud.annotation.Field)item.getAnnotation(com.kuma.boot.data.elasticsearch.kcloud.annotation.Field.class))).toList();
   }

   private Document.Mapping getMapping(Field item, com.kuma.boot.data.elasticsearch.kcloud.annotation.Field field) {
      String value = field.value();
      value = StringUtil.isEmpty(value) ? item.getName() : value;
      Type type = field.type();
      String searchAnalyzer = field.searchAnalyzer();
      String analyzer = field.analyzer();
      boolean fielddata = field.fielddata();
      boolean eagerGlobalOrdinals = field.eagerGlobalOrdinals();
      return new Document.Mapping(value, type, searchAnalyzer, analyzer, fielddata, eagerGlobalOrdinals);
   }
}
