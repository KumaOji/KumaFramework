package com.kuma.boot.ddd.model.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Schema(
   name = "BaseResult",
   description = "客户端基础返回对象"
)
public abstract class BaseResult implements Serializable {
   private static final long serialVersionUID = -7605952923416404638L;
   protected Map extValues = new HashMap();

   public Object getExtField(String key) {
      return this.extValues != null ? this.extValues.get(key) : null;
   }

   public void putExtField(String fieldName, Object value) {
      this.extValues.put(fieldName, value);
   }

   public Map getExtValues() {
      return this.extValues;
   }

   public void setExtValues(Map extValues) {
      this.extValues = extValues;
   }
}
