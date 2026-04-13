//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.application.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MultiResponse<T> extends Response {
   private static final long serialVersionUID = 1L;
   private Collection<T> data;

   public MultiResponse() {
   }

   public List<T> getData() {
      if (null == this.data) {
         return Collections.emptyList();
      } else {
         return (List<T>)(this.data instanceof List ? (List)this.data : new ArrayList(this.data));
      }
   }

   public void setData(Collection<T> data) {
      this.data = data;
   }

   public boolean isEmpty() {
      return this.data == null || this.data.isEmpty();
   }

   public boolean isNotEmpty() {
      return !this.isEmpty();
   }

   public static MultiResponse buildSuccess() {
      MultiResponse response = new MultiResponse();
      response.setSuccess(true);
      return response;
   }

   public static MultiResponse buildFailure(String errCode, String errMessage) {
      MultiResponse response = new MultiResponse();
      response.setSuccess(false);
      response.setErrCode(errCode);
      response.setErrMessage(errMessage);
      return response;
   }

   public static <T> MultiResponse<T> of(Collection<T> data) {
      MultiResponse<T> response = new MultiResponse<T>();
      response.setSuccess(true);
      response.setData(data);
      return response;
   }

   public String toString() {
      boolean var10000 = super.isSuccess();
      return "MultiResponse{success=" + var10000 + ", data=" + String.valueOf(this.data) + ", errCode='" + super.getErrCode() + "', errMessage='" + super.getErrMessage() + "'}";
   }
}
