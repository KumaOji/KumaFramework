package com.kuma.boot.openapi.common.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.openapi.common.enums.DataType;
import com.kuma.boot.openapi.common.util.TruncateUtil;

public class InParams {
   private String uuid;
   private String callerId;
   private String api;
   private String method;
   private String body;
   private byte[] bodyBytes;
   private String bodyBytesStr;
   private String sign;
   private String symmetricCryKey;
   private boolean multiParam;
   private DataType dataType;

   public String toString() {
      InParams inParams = new InParams();
      BeanUtil.copyProperties(this, inParams, new String[0]);
      inParams.setBody(TruncateUtil.truncate(inParams.getBody()));
      inParams.setBodyBytesStr(TruncateUtil.truncate(inParams.getBodyBytes()));
      inParams.setBodyBytes((byte[])null);
      return JSONUtil.toJsonStr(inParams);
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

   public String getBody() {
      return this.body;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public byte[] getBodyBytes() {
      return this.bodyBytes;
   }

   public void setBodyBytes(byte[] bodyBytes) {
      this.bodyBytes = bodyBytes;
   }

   public String getBodyBytesStr() {
      return this.bodyBytesStr;
   }

   public void setBodyBytesStr(String bodyBytesStr) {
      this.bodyBytesStr = bodyBytesStr;
   }

   public String getSign() {
      return this.sign;
   }

   public void setSign(String sign) {
      this.sign = sign;
   }

   public String getSymmetricCryKey() {
      return this.symmetricCryKey;
   }

   public void setSymmetricCryKey(String symmetricCryKey) {
      this.symmetricCryKey = symmetricCryKey;
   }

   public boolean isMultiParam() {
      return this.multiParam;
   }

   public void setMultiParam(boolean multiParam) {
      this.multiParam = multiParam;
   }

   public DataType getDataType() {
      return this.dataType;
   }

   public void setDataType(DataType dataType) {
      this.dataType = dataType;
   }
}
