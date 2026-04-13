package com.kuma.boot.data.jpa.fenix.helper;

import java.util.HashMap;
import java.util.Map;

public final class ParamWrapper {
   private Map<String, Object> paramMap;

   private ParamWrapper(Map<String, Object> paramMap) {
      this.paramMap = paramMap;
   }

   public static ParamWrapper newInstance() {
      return new ParamWrapper(new HashMap());
   }

   public static ParamWrapper newInstance(Map<String, Object> paramMap) {
      return new ParamWrapper(paramMap);
   }

   public static ParamWrapper newInstance(String key, Object value) {
      Map<String, Object> paramMap = new HashMap();
      paramMap.put(key, value);
      return new ParamWrapper(paramMap);
   }

   public ParamWrapper put(String key, Object value) {
      this.paramMap.put(key, value);
      return this;
   }

   public Map<String, Object> toMap() {
      return this.paramMap;
   }
}
