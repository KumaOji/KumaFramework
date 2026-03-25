package com.kuma.boot.oss.common.model;

public class FileOssInfo extends OssInfo {
   private String id;

   public FileOssInfo() {
   }

   public FileOssInfo(String id) {
      this.id = id;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }
}
