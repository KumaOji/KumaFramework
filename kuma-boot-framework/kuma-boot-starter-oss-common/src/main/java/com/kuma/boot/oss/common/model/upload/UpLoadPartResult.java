package com.kuma.boot.oss.common.model.upload;

public class UpLoadPartResult {
   private int number;
   private long offset;
   private long length;
   private boolean failed = false;
   private Exception exception;
   private Long partCrc;
   private UpLoadPartEntityTag entityTag;

   public UpLoadPartResult(int number, long offset, long length) {
      this.number = number;
      this.offset = offset;
      this.length = length;
   }

   public UpLoadPartResult(int number, long offset, long length, long partCrc) {
      this.number = number;
      this.offset = offset;
      this.length = length;
      this.partCrc = partCrc;
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

   public long getLength() {
      return this.length;
   }

   public void setLength(long length) {
      this.length = length;
   }

   public boolean isFailed() {
      return this.failed;
   }

   public void setFailed(boolean failed) {
      this.failed = failed;
   }

   public Exception getException() {
      return this.exception;
   }

   public void setException(Exception exception) {
      this.exception = exception;
   }

   public Long getPartCrc() {
      return this.partCrc;
   }

   public void setPartCrc(Long partCrc) {
      this.partCrc = partCrc;
   }

   public UpLoadPartEntityTag getEntityTag() {
      return this.entityTag;
   }

   public void setEntityTag(UpLoadPartEntityTag entityTag) {
      this.entityTag = entityTag;
   }
}
