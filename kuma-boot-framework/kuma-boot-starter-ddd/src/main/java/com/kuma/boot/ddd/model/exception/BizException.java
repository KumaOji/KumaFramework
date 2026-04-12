package com.kuma.boot.ddd.model.exception;

public class BizException extends BaseException {
   private static final long serialVersionUID = 1L;
   public static final String DEFAULT_ERR_CODE = "BIZ_ERROR";

   public BizException(String errMessage) {
      super("BIZ_ERROR", errMessage);
   }

   public BizException(String errCode, String errMessage) {
      super(errCode, errMessage);
   }

   public BizException(String errMessage, Throwable e) {
      super("BIZ_ERROR", errMessage, e);
   }

   public BizException(String errorCode, String errMessage, Throwable e) {
      super(errorCode, errMessage, e);
   }

   public String toString() {
      String var10000 = this.getErrCode();
      return "BizException{errCode='" + var10000 + "',errMessage='" + this.getMessage() + "'}";
   }
}
