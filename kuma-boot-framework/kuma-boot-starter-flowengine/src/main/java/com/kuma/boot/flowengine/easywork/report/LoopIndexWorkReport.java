package com.kuma.boot.flowengine.easywork.report;

import java.util.Objects;

public class LoopIndexWorkReport extends DefaultWorkReport {
   private int index = 0;
   private int length = 0;

   public LoopIndexWorkReport() {
   }

   public LoopIndexWorkReport(int index, int length) {
      this.index = index;
      this.length = length;
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public int getLength() {
      return this.length;
   }

   public void setLength(int length) {
      this.length = length;
   }

   public static LoopIndexWorkReport aNewLoopWorkReport() {
      return new LoopIndexWorkReport();
   }

   public void with(WorkReport workReport) {
      this.error = workReport.getError();
      this.result = workReport.getResult();
      this.status = workReport.getStatus();
      this.result = workReport.getResult();
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         LoopIndexWorkReport that = (LoopIndexWorkReport)o;
         return this.index == that.index && this.length == that.length;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.index, this.length});
   }
}
