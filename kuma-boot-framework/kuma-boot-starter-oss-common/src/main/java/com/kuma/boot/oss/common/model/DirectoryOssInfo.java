package com.kuma.boot.oss.common.model;

import java.util.ArrayList;
import java.util.List;

public class DirectoryOssInfo extends OssInfo {
   private List fileInfos = new ArrayList();
   private List directoryInfos = new ArrayList();

   public DirectoryOssInfo() {
   }

   public DirectoryOssInfo(List fileInfos) {
      this.fileInfos = fileInfos;
   }

   public List getFileInfos() {
      return this.fileInfos;
   }

   public void setFileInfos(List fileInfos) {
      this.fileInfos = fileInfos;
   }

   public List getDirectoryInfos() {
      return this.directoryInfos;
   }

   public void setDirectoryInfos(List directoryInfos) {
      this.directoryInfos = directoryInfos;
   }
}
