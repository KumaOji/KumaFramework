package com.kuma.boot.oss.common.model;

public class OssInfo {
   private String name;
   private String path;
   private String url;
   private Long length;
   private String createTime;
   private String lastUpdateTime;
   private UploadFileInfo uploadFileInfo;

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public String getCreateTime() {
      return this.createTime;
   }

   public void setCreateTime(String createTime) {
      this.createTime = createTime;
   }

   public String getLastUpdateTime() {
      return this.lastUpdateTime;
   }

   public void setLastUpdateTime(String lastUpdateTime) {
      this.lastUpdateTime = lastUpdateTime;
   }

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public Long getLength() {
      return this.length;
   }

   public void setLength(Long length) {
      this.length = length;
   }

   public String toString() {
      return "OssInfo{name='" + this.name + "', path='" + this.path + "', url='" + this.url + "', length=" + this.length + ", createTime='" + this.createTime + "', lastUpdateTime='" + this.lastUpdateTime + "'}";
   }

   public UploadFileInfo getUploadFileInfo() {
      return this.uploadFileInfo;
   }

   public void setUploadFileInfo(UploadFileInfo uploadFileInfo) {
      this.uploadFileInfo = uploadFileInfo;
   }
}
