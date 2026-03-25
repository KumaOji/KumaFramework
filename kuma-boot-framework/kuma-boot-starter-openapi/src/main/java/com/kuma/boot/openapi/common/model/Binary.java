package com.kuma.boot.openapi.common.model;

import cn.hutool.json.JSONUtil;
import com.kuma.boot.openapi.common.annotation.OpenApiDoc;
import com.kuma.boot.openapi.common.util.CommonUtil;
import com.kuma.boot.openapi.common.util.TruncateUtil;

@OpenApiDoc(
   cnName = "二进制对象"
)
public class Binary {
   @OpenApiDoc(
      cnName = "数据长度"
   )
   private long length;
   @OpenApiDoc(
      cnName = "数据"
   )
   private byte[] data;
   @OpenApiDoc(
      ignore = true
   )
   private String dataStr;

   public void setData(byte[] data) {
      this.data = data;
      this.length = data == null ? 0L : (long)data.length;
   }

   public String toString() {
      Binary binary = (Binary)CommonUtil.cloneInstance(this);
      long length = binary.getLength();
      binary.setDataStr(TruncateUtil.truncate(binary.getData()));
      binary.setData((byte[])null);
      binary.setLength(length);
      return JSONUtil.toJsonStr(binary);
   }

   public long getLength() {
      return this.length;
   }

   public void setLength(long length) {
      this.length = length;
   }

   public byte[] getData() {
      return this.data;
   }

   public String getDataStr() {
      return this.dataStr;
   }

   public void setDataStr(String dataStr) {
      this.dataStr = dataStr;
   }
}
