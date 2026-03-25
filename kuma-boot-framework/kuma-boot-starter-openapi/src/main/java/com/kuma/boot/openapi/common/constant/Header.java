package com.kuma.boot.openapi.common.constant;

public interface Header {
   public interface Request {
      String UUID = "openapi-uuid";
      String CALLER_ID = "openapi-callerId";
      String API = "openapi-api";
      String METHOD = "openapi-method";
      String SIGN = "openapi-sign";
      String SYMMETRIC_CRY_KEY = "openapi-symmetricCryKey";
      String MULTI_PARAM = "openapi-multiParam";
      String DATA_TYPE = "openapi-dataType";
   }

   public interface Response {
      String UUID = "openapi-uuid";
      String CODE = "openapi-code";
      String MESSAGE = "openapi-message";
      String SYMMETRIC_CRY_KEY = "openapi-symmetricCryKey";
      String DATA_TYPE = "openapi-dataType";
   }
}
