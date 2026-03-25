package com.kuma.boot.oss.common.model.upload;

import java.io.File;
import java.io.Serializable;

public class UpLoadFileStat implements Serializable {
   private static final long serialVersionUID = -1223810339796425415L;
   private long size;
   private long lastModified;
   private String digest;

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.digest == null ? 0 : this.digest.hashCode());
      result = 31 * result + (int)(this.lastModified ^ this.lastModified >>> 32);
      result = 31 * result + (int)(this.size ^ this.size >>> 32);
      return result;
   }

   public static UpLoadFileStat getFileStat(String uploadFile) {
      UpLoadFileStat fileStat = new UpLoadFileStat();
      File file = new File(uploadFile);
      fileStat.setSize(file.length());
      fileStat.setLastModified(file.lastModified());
      return fileStat;
   }

   public long getSize() {
      return this.size;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public long getLastModified() {
      return this.lastModified;
   }

   public void setLastModified(long lastModified) {
      this.lastModified = lastModified;
   }

   public String getDigest() {
      return this.digest;
   }

   public void setDigest(String digest) {
      this.digest = digest;
   }
}
