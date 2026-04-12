package com.kuma.boot.ddd.gateway.model;

import java.util.HashMap;
import java.util.Map;

public class GatewayContext {
   private String traceId;
   private String tradeNo;
   private GatewayRecord gatewayRecord;
   private Exception catchedException;
   private Object rawResponse;
   private boolean reachedRoute;
   private String description;
   private Object request;
   private Map extraInfo = new HashMap();

   public String getTraceId() {
      return this.traceId;
   }

   public void setTraceId(String traceId) {
      this.traceId = traceId;
   }

   public String getTradeNo() {
      return this.tradeNo;
   }

   public void setTradeNo(String tradeNo) {
      this.tradeNo = tradeNo;
   }

   public GatewayRecord getGatewayRecord() {
      return this.gatewayRecord;
   }

   public void setGatewayRecord(GatewayRecord gatewayRecord) {
      this.gatewayRecord = gatewayRecord;
   }

   public Exception getCatchedException() {
      return this.catchedException;
   }

   public void setCatchedException(Exception catchedException) {
      this.catchedException = catchedException;
   }

   public Object getRawResponse() {
      return this.rawResponse;
   }

   public void setRawResponse(Object rawResponse) {
      this.rawResponse = rawResponse;
   }

   public boolean isReachedRoute() {
      return this.reachedRoute;
   }

   public void setReachedRoute(boolean reachedRoute) {
      this.reachedRoute = reachedRoute;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Object getRequest() {
      return this.request;
   }

   public void setRequest(Object request) {
      this.request = request;
   }

   public Map getExtraInfo() {
      return this.extraInfo;
   }

   public void setExtraInfo(Map extraInfo) {
      this.extraInfo = extraInfo;
   }
}
