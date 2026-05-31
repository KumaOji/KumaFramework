package com.kuma.boot.data.elasticsearch.es.utils;

import java.util.ArrayList;
import java.util.List;

public class EsQuery {
   private String indexName;
   private List<EsMatch> match = new ArrayList<>();
   private Integer from;
   private Integer size = 10;
   private List<String> column;
   private List<EsSort> sorts;

   public EsQuery() {
   }

   public String getIndexName() {
      return this.indexName;
   }

   public void setIndexName(String indexName) {
      this.indexName = indexName;
   }

   public List<EsMatch> getMatch() {
      return this.match;
   }

   public void setMatch(List<EsMatch> match) {
      this.match = match;
   }

   public Integer getFrom() {
      return this.from;
   }

   public void setFrom(Integer from) {
      this.from = from;
   }

   public Integer getSize() {
      return this.size;
   }

   public void setSize(Integer size) {
      this.size = size;
   }

   public List<String> getColumn() {
      return this.column;
   }

   public void setColumn(List<String> column) {
      this.column = column;
   }

   public List<EsSort> getSorts() {
      return this.sorts;
   }

   public void setSorts(List<EsSort> sorts) {
      this.sorts = sorts;
   }
}
