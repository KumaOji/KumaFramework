package com.kuma.boot.data.elasticsearch.kcloud.annotation;

import java.util.List;

public class Document {
   private String name;
   private String alias;
   private List<Mapping> mappings;
   private Setting setting;
   private Analysis analysis;

   public Document() {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getAlias() {
      return this.alias;
   }

   public void setAlias(String alias) {
      this.alias = alias;
   }

   public List<Mapping> getMappings() {
      return this.mappings;
   }

   public void setMappings(List<Mapping> mappings) {
      this.mappings = mappings;
   }

   public Setting getSetting() {
      return this.setting;
   }

   public void setSetting(Setting setting) {
      this.setting = setting;
   }

   public Analysis getAnalysis() {
      return this.analysis;
   }

   public void setAnalysis(Analysis analysis) {
      this.analysis = analysis;
   }

   public static DocumentBuilder builder() {
      return new DocumentBuilder();
   }

   public static class Mapping {
      private String field;
      private Type type;
      private String searchAnalyzer;
      private String analyzer;
      private boolean fielddata;
      private boolean eagerGlobalOrdinals;

      public Mapping() {
      }

      public Mapping(String field, Type type, String searchAnalyzer, String analyzer, boolean fielddata, boolean eagerGlobalOrdinals) {
         this.field = field;
         this.type = type;
         this.searchAnalyzer = searchAnalyzer;
         this.analyzer = analyzer;
         this.fielddata = fielddata;
         this.eagerGlobalOrdinals = eagerGlobalOrdinals;
      }

      public String getField() {
         return this.field;
      }

      public void setField(String field) {
         this.field = field;
      }

      public Type getType() {
         return this.type;
      }

      public void setType(Type type) {
         this.type = type;
      }

      public String getSearchAnalyzer() {
         return this.searchAnalyzer;
      }

      public void setSearchAnalyzer(String searchAnalyzer) {
         this.searchAnalyzer = searchAnalyzer;
      }

      public String getAnalyzer() {
         return this.analyzer;
      }

      public void setAnalyzer(String analyzer) {
         this.analyzer = analyzer;
      }

      public boolean isFielddata() {
         return this.fielddata;
      }

      public void setFielddata(boolean fielddata) {
         this.fielddata = fielddata;
      }

      public boolean isEagerGlobalOrdinals() {
         return this.eagerGlobalOrdinals;
      }

      public void setEagerGlobalOrdinals(boolean eagerGlobalOrdinals) {
         this.eagerGlobalOrdinals = eagerGlobalOrdinals;
      }
   }

   public static class Setting {
      private short shards;
      private short replicas;
      private String refreshInterval;

      public Setting() {
      }

      public Setting(short shards, short replicas, String refreshInterval) {
         this.shards = shards;
         this.replicas = replicas;
         this.refreshInterval = refreshInterval;
      }

      public short getShards() {
         return this.shards;
      }

      public void setShards(short shards) {
         this.shards = shards;
      }

      public short getReplicas() {
         return this.replicas;
      }

      public void setReplicas(short replicas) {
         this.replicas = replicas;
      }

      public String getRefreshInterval() {
         return this.refreshInterval;
      }

      public void setRefreshInterval(String refreshInterval) {
         this.refreshInterval = refreshInterval;
      }
   }

   public static class Analysis {
      private List<Filter> filters;
      private List<Analyzer> analyzers;

      public Analysis() {
      }

      public Analysis(List<Filter> filters, List<Analyzer> analyzers) {
         this.filters = filters;
         this.analyzers = analyzers;
      }

      public List<Filter> getFilters() {
         return this.filters;
      }

      public void setFilters(List<Filter> filters) {
         this.filters = filters;
      }

      public List<Analyzer> getAnalyzers() {
         return this.analyzers;
      }

      public void setAnalyzers(List<Analyzer> analyzers) {
         this.analyzers = analyzers;
      }
   }

   public static class Filter {
      private String name;
      private List<Option> options;

      public Filter() {
      }

      public Filter(String name, List<Option> options) {
         this.name = name;
         this.options = options;
      }

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public List<Option> getOptions() {
         return this.options;
      }

      public void setOptions(List<Option> options) {
         this.options = options;
      }
   }

   public static class Args {
      private String filter;
      private String tokenizer;

      public Args() {
      }

      public Args(String filter, String tokenizer) {
         this.filter = filter;
         this.tokenizer = tokenizer;
      }

      public String getFilter() {
         return this.filter;
      }

      public void setFilter(String filter) {
         this.filter = filter;
      }

      public String getTokenizer() {
         return this.tokenizer;
      }

      public void setTokenizer(String tokenizer) {
         this.tokenizer = tokenizer;
      }
   }

   public static class Option {
      private String key;
      private String value;

      public Option() {
      }

      public Option(String key, String value) {
         this.key = key;
         this.value = value;
      }

      public String getKey() {
         return this.key;
      }

      public void setKey(String key) {
         this.key = key;
      }

      public String getValue() {
         return this.value;
      }

      public void setValue(String value) {
         this.value = value;
      }
   }

   public static class Analyzer {
      private String name;
      private Args args;

      public Analyzer() {
      }

      public Analyzer(String name, Args args) {
         this.name = name;
         this.args = args;
      }

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public Args getArgs() {
         return this.args;
      }

      public void setArgs(Args args) {
         this.args = args;
      }
   }

   public static final class DocumentBuilder {
      private String name;
      private String alias;
      private List<Mapping> mappings;
      private Setting setting;
      private Analysis analysis;

      private DocumentBuilder() {
      }

      public DocumentBuilder name(String name) {
         this.name = name;
         return this;
      }

      public DocumentBuilder alias(String alias) {
         this.alias = alias;
         return this;
      }

      public DocumentBuilder mappings(List<Mapping> mappings) {
         this.mappings = mappings;
         return this;
      }

      public DocumentBuilder setting(Setting setting) {
         this.setting = setting;
         return this;
      }

      public DocumentBuilder analysis(Analysis analysis) {
         this.analysis = analysis;
         return this;
      }

      public Document build() {
         Document document = new Document();
         document.setName(this.name);
         document.setAlias(this.alias);
         document.setMappings(this.mappings);
         document.setSetting(this.setting);
         document.setAnalysis(this.analysis);
         return document;
      }
   }
}
