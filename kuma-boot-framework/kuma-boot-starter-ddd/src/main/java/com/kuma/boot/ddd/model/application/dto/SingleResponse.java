package com.kuma.boot.ddd.model.application.dto;

public class SingleResponse extends Response {
   private static final long serialVersionUID = 1L;
   private Object data;

   public Object getData() {
      return this.data;
   }

   public void setData(Object data) {
      this.data = data;
   }

   public static SingleResponse buildSuccess() {
      SingleResponse response = new SingleResponse();
      response.setSuccess(true);
      return response;
   }

   public static SingleResponse buildFailure(String errCode, String errMessage) {
      SingleResponse response = new SingleResponse();
      response.setSuccess(false);
      response.setErrCode(errCode);
      response.setErrMessage(errMessage);
      return response;
   }

   public static SingleResponse of(Object data) {
      SingleResponse<T> response = new SingleResponse();
      response.setSuccess(true);
      response.setData(data);
      return response;
   }

   public String toString() {
      boolean var10000 = super.isSuccess();
      return "SingleResponse{success=" + var10000 + ", data=" + String.valueOf(this.data) + ", errCode='" + super.getErrCode() + "', errMessage='" + super.getErrMessage() + "'}";
   }
}
