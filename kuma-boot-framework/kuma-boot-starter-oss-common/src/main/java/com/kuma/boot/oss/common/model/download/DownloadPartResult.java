package com.kuma.boot.oss.common.model.download;

public class DownloadPartResult {
   private int number;
   private long start;
   private long end;
   private boolean failed = false;
   private Exception exception;
   private Long clientCrc;
   private Long serverCrc;
   private long length;

   public DownloadPartResult(int number, long start, long end) {
      this.number = number;
      this.start = start;
      this.end = end;
   }

   public DownloadPartResult(int number, long start, long end, long length, long clientCrc) {
      this.number = number;
      this.start = start;
      this.end = end;
      this.length = length;
      this.clientCrc = clientCrc;
   }

   public int getNumber() {
      return this.number;
   }

   public void setNumber(int number) {
      this.number = number;
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

   public Long getClientCrc() {
      return this.clientCrc;
   }

   public void setClientCrc(Long clientCrc) {
      this.clientCrc = clientCrc;
   }

   public Long getServerCrc() {
      return this.serverCrc;
   }

   public void setServerCrc(Long serverCrc) {
      this.serverCrc = serverCrc;
   }

   public long getLength() {
      return this.length;
   }

   public void setLength(long length) {
      this.length = length;
   }
}
