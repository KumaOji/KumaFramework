package com.kuma.boot.oss.common.model.download;

import java.io.Serializable;
import java.util.Date;

public class DownloadObjectStat implements Serializable {
   private static final long serialVersionUID = -2883494783412999919L;
   private long size;
   private Date lastModified;
   private String digest;

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.digest == null ? 0 : this.digest.hashCode());
      result = 31 * result + (this.lastModified == null ? 0 : this.lastModified.hashCode());
      result = 31 * result + (int)(this.size ^ this.size >>> 32);
      return result;
   }

   public long getSize() {
      return this.size;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public Date getLastModified() {
      return this.lastModified;
   }

   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
   }

   public String getDigest() {
      return this.digest;
   }

   public void setDigest(String digest) {
      this.digest = digest;
   }
}
