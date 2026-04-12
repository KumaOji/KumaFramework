package com.kuma.boot.ddd.gateway.model;

public class GatewayRecord {
   private String tradeNo;
   private String traceId;
   private String businessCode;
   private String businessFlow;
   private String businessEvent;
   private String status;
   private String changedSubTraceId;
   private String failcode;
   private String failMsg;
   private String extInfo;

   public String getTradeNo() {
      return this.tradeNo;
   }

   public void setTradeNo(String tradeNo) {
      this.tradeNo = tradeNo;
   }

   public String getTraceId() {
      return this.traceId;
   }

   public void setTraceId(String traceId) {
      this.traceId = traceId;
   }

   public String getBusinessCode() {
      return this.businessCode;
   }

   public void setBusinessCode(String businessCode) {
      this.businessCode = businessCode;
   }

   public String getBusinessFlow() {
      return this.businessFlow;
   }

   public void setBusinessFlow(String businessFlow) {
      this.businessFlow = businessFlow;
   }

   public String getBusinessEvent() {
      return this.businessEvent;
   }

   public void setBusinessEvent(String businessEvent) {
      this.businessEvent = businessEvent;
   }

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getChangedSubTraceId() {
      return this.changedSubTraceId;
   }

   public void setChangedSubTraceId(String changedSubTraceId) {
      this.changedSubTraceId = changedSubTraceId;
   }

   public String getFailcode() {
      return this.failcode;
   }

   public void setFailcode(String failcode) {
      this.failcode = failcode;
   }

   public String getFailMsg() {
      return this.failMsg;
   }

   public void setFailMsg(String failMsg) {
      this.failMsg = failMsg;
   }

   public String getExtInfo() {
      return this.extInfo;
   }

   public void setExtInfo(String extInfo) {
      this.extInfo = extInfo;
   }
}
