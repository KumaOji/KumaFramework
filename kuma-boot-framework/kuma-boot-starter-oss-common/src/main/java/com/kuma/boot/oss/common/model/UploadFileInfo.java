package com.kuma.boot.oss.common.model;

public class UploadFileInfo {
   private String originalFileName;
   private String name;
   private String fileMd5;
   private Boolean isImg;
   private String contentType;
   private String fileType;
   private long size;
   private String url;

   public String getOriginalFileName() {
      return this.originalFileName;
   }

   public void setOriginalFileName(String originalFileName) {
      this.originalFileName = originalFileName;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getFileMd5() {
      return this.fileMd5;
   }

   public void setFileMd5(String fileMd5) {
      this.fileMd5 = fileMd5;
   }

   public Boolean getImg() {
      return this.isImg;
   }

   public void setImg(Boolean img) {
      this.isImg = img;
   }

   public String getContentType() {
      return this.contentType;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   public String getFileType() {
      return this.fileType;
   }

   public void setFileType(String fileType) {
      this.fileType = fileType;
   }

   public long getSize() {
      return this.size;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String url) {
      this.url = url;
   }
}
