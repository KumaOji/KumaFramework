package com.kuma.boot.data.elasticsearch.es.utils;

public class EsDoc<T> {
   private String id;
   private T doc;
   private String indexName;

   public EsDoc() {
   }

   public EsDoc(String id, T doc, String indexName) {
      this.id = id;
      this.doc = doc;
      this.indexName = indexName;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public T getDoc() {
      return this.doc;
   }

   public void setDoc(T doc) {
      this.doc = doc;
   }

   public String getIndexName() {
      return this.indexName;
   }

   public void setIndexName(String indexName) {
      this.indexName = indexName;
   }
}
