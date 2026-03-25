package com.kuma.boot.openapi.server.model;

import cn.hutool.json.JSONUtil;
import com.kuma.boot.openapi.common.enums.DataType;

public class OpenApiRequest {
   private String uuid;
   private String callerId;
   private String api;
   private String method;
   private DataType dataType;

   public String toString() {
      return JSONUtil.toJsonStr(this);
   }

   public String getUuid() {
      return this.uuid;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   public String getCallerId() {
      return this.callerId;
   }

   public void setCallerId(String callerId) {
      this.callerId = callerId;
   }

   public String getApi() {
      return this.api;
   }

   public void setApi(String api) {
      this.api = api;
   }

   public String getMethod() {
      return this.method;
   }

   public void setMethod(String method) {
      this.method = method;
   }

   public DataType getDataType() {
      return this.dataType;
   }

   public void setDataType(DataType dataType) {
      this.dataType = dataType;
   }
}
