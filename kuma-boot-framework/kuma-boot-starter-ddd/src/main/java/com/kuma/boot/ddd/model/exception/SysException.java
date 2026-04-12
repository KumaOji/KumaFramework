package com.kuma.boot.ddd.model.exception;

public class SysException extends BaseException {
   private static final long serialVersionUID = 1L;
   public static final String DEFAULT_ERR_CODE = "SYS_ERROR";

   public SysException(String errMessage) {
      super("SYS_ERROR", errMessage);
   }

   public SysException(String errCode, String errMessage) {
      super(errCode, errMessage);
   }

   public SysException(String errMessage, Throwable e) {
      super("SYS_ERROR", errMessage, e);
   }

   public SysException(String errorCode, String errMessage, Throwable e) {
      super(errorCode, errMessage, e);
   }

   public String toString() {
      String var10000 = this.getErrCode();
      return "SysException{errCode='" + var10000 + "',errMessage='" + this.getMessage() + "'}";
   }
}
