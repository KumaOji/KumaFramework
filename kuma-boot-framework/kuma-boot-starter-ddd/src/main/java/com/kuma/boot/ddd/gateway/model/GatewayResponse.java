package com.kuma.boot.ddd.gateway.model;

public class GatewayResponse {
   private GatewayResponseStatus status;
   private String failCode;
   private String failMsg;
   private Object result;
   private GatewayRecord gatewayRecord;

   public GatewayResponseStatus getStatus() {
      return this.status;
   }

   public void setStatus(GatewayResponseStatus status) {
      this.status = status;
   }

   public String getFailCode() {
      return this.failCode;
   }

   public void setFailCode(String failCode) {
      this.failCode = failCode;
   }

   public String getFailMsg() {
      return this.failMsg;
   }

   public void setFailMsg(String failMsg) {
      this.failMsg = failMsg;
   }

   public Object getResult() {
      return this.result;
   }

   public void setResult(Object result) {
      this.result = result;
   }

   public GatewayRecord getGatewayRecord() {
      return this.gatewayRecord;
   }

   public void setGatewayRecord(GatewayRecord gatewayRecord) {
      this.gatewayRecord = gatewayRecord;
   }
}
