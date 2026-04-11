package com.kuma.boot.data.elasticsearch.model;

public class IndexDto {
   private String indexName;
   private Integer numberOfShards;
   private Integer numberOfReplicas;
   private String type;
   private String mappingsSource;

   public IndexDto() {
   }

   public String getIndexName() {
      return this.indexName;
   }

   public void setIndexName(String indexName) {
      this.indexName = indexName;
   }

   public Integer getNumberOfShards() {
      return this.numberOfShards;
   }

   public void setNumberOfShards(Integer numberOfShards) {
      this.numberOfShards = numberOfShards;
   }

   public Integer getNumberOfReplicas() {
      return this.numberOfReplicas;
   }

   public void setNumberOfReplicas(Integer numberOfReplicas) {
      this.numberOfReplicas = numberOfReplicas;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getMappingsSource() {
      return this.mappingsSource;
   }

   public void setMappingsSource(String mappingsSource) {
      this.mappingsSource = mappingsSource;
   }
}
