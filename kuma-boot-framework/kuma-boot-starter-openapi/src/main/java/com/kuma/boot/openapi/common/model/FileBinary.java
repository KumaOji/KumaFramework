package com.kuma.boot.openapi.common.model;

import com.kuma.boot.openapi.common.annotation.OpenApiDoc;

@OpenApiDoc(
   cnName = "文件对象"
)
public class FileBinary extends Binary {
   @OpenApiDoc(
      cnName = "文件名"
   )
   private String fileName;
   @OpenApiDoc(
      cnName = "文件类型"
   )
   private String fileType;

   public String getFileName() {
      return this.fileName;
   }

   public void setFileName(String fileName) {
      this.fileName = fileName;
   }

   public String getFileType() {
      return this.fileType;
   }

   public void setFileType(String fileType) {
      this.fileType = fileType;
   }
}
