package com.kuma.boot.data.elasticsearch.model;

public class LogicDelDto {
   private String logicDelField;
   private String logicNotDelValue;

   public LogicDelDto() {
   }

   public String getLogicDelField() {
      return this.logicDelField;
   }

   public void setLogicDelField(String logicDelField) {
      this.logicDelField = logicDelField;
   }

   public String getLogicNotDelValue() {
      return this.logicNotDelValue;
   }

   public void setLogicNotDelValue(String logicNotDelValue) {
      this.logicNotDelValue = logicNotDelValue;
   }
}
