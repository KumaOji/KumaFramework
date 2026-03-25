package com.kuma.boot.oss.common.model.download;

import java.io.Serializable;

public class DownloadPart implements Serializable {
   private static final long serialVersionUID = -3655925846487976207L;
   private int index;
   private long start;
   private long end;
   private boolean isCompleted;
   private long length;
   private long crc;
   private long fileStart;

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + this.index;
      result = 31 * result + (this.isCompleted ? 1231 : 1237);
      result = 31 * result + (int)(this.end ^ this.end >>> 32);
      result = 31 * result + (int)(this.start ^ this.start >>> 32);
      result = 31 * result + (int)(this.crc ^ this.crc >>> 32);
      result = 31 * result + (int)(this.fileStart ^ this.fileStart >>> 32);
      return result;
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public long getStart() {
      return this.start;
   }

   public void setStart(long start) {
      this.start = start;
   }

   public long getEnd() {
      return this.end;
   }

   public void setEnd(long end) {
      this.end = end;
   }

   public boolean isCompleted() {
      return this.isCompleted;
   }

   public void setCompleted(boolean completed) {
      this.isCompleted = completed;
   }

   public long getLength() {
      return this.length;
   }

   public void setLength(long length) {
      this.length = length;
   }

   public long getCrc() {
      return this.crc;
   }

   public void setCrc(long crc) {
      this.crc = crc;
   }

   public long getFileStart() {
      return this.fileStart;
   }

   public void setFileStart(long fileStart) {
      this.fileStart = fileStart;
   }
}
