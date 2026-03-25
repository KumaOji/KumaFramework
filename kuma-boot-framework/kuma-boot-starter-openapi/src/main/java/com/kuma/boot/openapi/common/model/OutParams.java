package com.kuma.boot.openapi.common.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.openapi.common.enums.DataType;
import com.kuma.boot.openapi.common.util.TruncateUtil;

public class OutParams {
   private String uuid;
   private Integer code;
   private String message;
   private String data;
   private byte[] binaryData;
   private String binaryDataStr;
   private byte[] dataBytes;
   private String dataBytesStr;
   private String symmetricCryKey;
   private DataType dataType;

   public static OutParams success() {
      OutParams outParams = new OutParams();
      outParams.code = 200;
      return outParams;
   }

   public static OutParams success(String data) {
      OutParams outParams = new OutParams();
      outParams.code = 200;
      outParams.data = data;
      return outParams;
   }

   public static OutParams error(String message) {
      OutParams outParams = new OutParams();
      outParams.code = -1;
      outParams.message = message;
      return outParams;
   }

   public static OutParams error(int code, String message) {
      OutParams outParams = new OutParams();
      outParams.code = code;
      outParams.message = message;
      return outParams;
   }

   public static boolean isSuccess(OutParams outParams) {
      return outParams != null && outParams.code != null && outParams.code == 200;
   }

   public String toString() {
      OutParams outParams = new OutParams();
      BeanUtil.copyProperties(this, outParams, new String[0]);
      outParams.setData(TruncateUtil.truncate(outParams.getData()));
      outParams.setDataBytesStr(TruncateUtil.truncate(outParams.getDataBytes()));
      outParams.setDataBytes((byte[])null);
      outParams.setBinaryDataStr(TruncateUtil.truncate(outParams.getBinaryData()));
      outParams.setBinaryData((byte[])null);
      return JSONUtil.toJsonStr(outParams);
   }

   public String getUuid() {
      return this.uuid;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   public Integer getCode() {
      return this.code;
   }

   public void setCode(Integer code) {
      this.code = code;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getData() {
      return this.data;
   }

   public void setData(String data) {
      this.data = data;
   }

   public byte[] getBinaryData() {
      return this.binaryData;
   }

   public void setBinaryData(byte[] binaryData) {
      this.binaryData = binaryData;
   }

   public String getBinaryDataStr() {
      return this.binaryDataStr;
   }

   public void setBinaryDataStr(String binaryDataStr) {
      this.binaryDataStr = binaryDataStr;
   }

   public byte[] getDataBytes() {
      return this.dataBytes;
   }

   public void setDataBytes(byte[] dataBytes) {
      this.dataBytes = dataBytes;
   }

   public String getDataBytesStr() {
      return this.dataBytesStr;
   }

   public void setDataBytesStr(String dataBytesStr) {
      this.dataBytesStr = dataBytesStr;
   }

   public String getSymmetricCryKey() {
      return this.symmetricCryKey;
   }

   public void setSymmetricCryKey(String symmetricCryKey) {
      this.symmetricCryKey = symmetricCryKey;
   }

   public DataType getDataType() {
      return this.dataType;
   }

   public void setDataType(DataType dataType) {
      this.dataType = dataType;
   }
}
