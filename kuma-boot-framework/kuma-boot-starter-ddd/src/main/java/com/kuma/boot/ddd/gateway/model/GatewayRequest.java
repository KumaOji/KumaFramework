//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.gateway.model;

public class GatewayRequest<T> {
   private T param;
   private GatewayRecord gatewayRecord;

   public GatewayRequest() {
   }

   public GatewayRequest(T param, GatewayRecord gatewayRecord) {
      this.param = param;
      this.gatewayRecord = gatewayRecord;
   }

   public T getParam() {
      return this.param;
   }

   public void setParam(T param) {
      this.param = param;
   }

   public GatewayRecord getGatewayRecord() {
      return this.gatewayRecord;
   }

   public void setGatewayRecord(GatewayRecord gatewayRecord) {
      this.gatewayRecord = gatewayRecord;
   }
}
