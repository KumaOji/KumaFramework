package com.kuma.boot.ddd.gateway.model;

public class GatewayRequest {
   private Object param;
   private GatewayRecord gatewayRecord;

   public GatewayRequest() {
   }

   public GatewayRequest(Object param, GatewayRecord gatewayRecord) {
      this.param = param;
      this.gatewayRecord = gatewayRecord;
   }

   public Object getParam() {
      return this.param;
   }

   public void setParam(Object param) {
      this.param = param;
   }

   public GatewayRecord getGatewayRecord() {
      return this.gatewayRecord;
   }

   public void setGatewayRecord(GatewayRecord gatewayRecord) {
      this.gatewayRecord = gatewayRecord;
   }
}
