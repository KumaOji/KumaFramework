package com.kuma.boot.sensitive.sensitivemvc;

public class SensitiveWrapper {
   private Object object;
   private String fieldName;
   private String fieldValue;
   private char replacer;

   public SensitiveWrapper() {
   }

   public SensitiveWrapper(Object object, String fieldName, String fieldValue, char replacer) {
      this.object = object;
      this.fieldName = fieldName;
      this.fieldValue = fieldValue;
      this.replacer = replacer;
   }

   public Object getObject() {
      return this.object;
   }

   public void setObject(Object object) {
      this.object = object;
   }

   public String getFieldName() {
      return this.fieldName;
   }

   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   public String getFieldValue() {
      return this.fieldValue;
   }

   public void setFieldValue(String fieldValue) {
      this.fieldValue = fieldValue;
   }

   public char getReplacer() {
      return this.replacer;
   }

   public void setReplacer(char replacer) {
      this.replacer = replacer;
   }
}
