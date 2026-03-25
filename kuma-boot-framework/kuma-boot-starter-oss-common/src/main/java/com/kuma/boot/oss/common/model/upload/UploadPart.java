package com.kuma.boot.oss.common.model.upload;

import java.io.Serializable;

public class UploadPart implements Serializable {
   private static final long serialVersionUID = 6692863980224332199L;
   private int number;
   private long offset;
   private long size;
   private boolean isCompleted = false;
   private long crc;

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.isCompleted ? 1231 : 1237);
      result = 31 * result + this.number;
      result = 31 * result + (int)(this.offset ^ this.offset >>> 32);
      result = 31 * result + (int)(this.size ^ this.size >>> 32);
      result = 31 * result + (int)(this.crc ^ this.crc >>> 32);
      return result;
   }

   public int getNumber() {
      return this.number;
   }

   public void setNumber(int number) {
      this.number = number;
   }

   public long getOffset() {
      return this.offset;
   }

   public void setOffset(long offset) {
      this.offset = offset;
   }

   public long getSize() {
      return this.size;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public boolean isCompleted() {
      return this.isCompleted;
   }

   public void setCompleted(boolean completed) {
      this.isCompleted = completed;
   }

   public long getCrc() {
      return this.crc;
   }

   public void setCrc(long crc) {
      this.crc = crc;
   }
}
