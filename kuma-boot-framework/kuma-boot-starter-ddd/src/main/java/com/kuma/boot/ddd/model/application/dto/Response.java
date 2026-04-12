package com.kuma.boot.ddd.model.application.dto;

public class Response extends Dto {
   private static final long serialVersionUID = 1L;
   private boolean success;
   private String errCode;
   private String errMessage;

   public boolean isSuccess() {
      return this.success;
   }

   public void setSuccess(boolean success) {
      this.success = success;
   }

   public String getErrCode() {
      return this.errCode;
   }

   public void setErrCode(String errCode) {
      this.errCode = errCode;
   }

   public String getErrMessage() {
      return this.errMessage;
   }

   public void setErrMessage(String errMessage) {
      this.errMessage = errMessage;
   }

   public String toString() {
      return "Response{success=" + this.success + ", errCode='" + this.errCode + "', errMessage='" + this.errMessage + "'}";
   }

   public static Response buildSuccess() {
      Response response = new Response();
      response.setSuccess(true);
      return response;
   }

   public static Response buildFailure(String errCode, String errMessage) {
      Response response = new Response();
      response.setSuccess(false);
      response.setErrCode(errCode);
      response.setErrMessage(errMessage);
      return response;
   }
}
