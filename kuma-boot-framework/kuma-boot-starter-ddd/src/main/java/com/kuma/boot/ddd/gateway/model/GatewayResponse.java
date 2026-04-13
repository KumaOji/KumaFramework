//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.gateway.model;

public class GatewayResponse<T> {
   private GatewayResponseStatus status;
   private String failCode;
   private String failMsg;
   private T result;
   private GatewayRecord gatewayRecord;

   public GatewayResponse() {
   }

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

   public T getResult() {
      return this.result;
   }

   public void setResult(T result) {
      this.result = result;
   }

   public GatewayRecord getGatewayRecord() {
      return this.gatewayRecord;
   }

   public void setGatewayRecord(GatewayRecord gatewayRecord) {
      this.gatewayRecord = gatewayRecord;
   }
}
