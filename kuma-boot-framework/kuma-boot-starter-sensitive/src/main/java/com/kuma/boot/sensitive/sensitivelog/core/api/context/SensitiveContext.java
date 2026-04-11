package com.kuma.boot.sensitive.sensitivelog.core.api.context;

import com.kuma.boot.sensitive.sensitivelog.api.Context;
import com.kuma.boot.sensitive.sensitivelog.core.exception.SensitiveRuntimeException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SensitiveContext implements Context {
   private Object currentObject;
   private Field currentField;
   private List<Field> allFieldList = new ArrayList();
   private Class beanClass;
   private Object entry;

   public SensitiveContext() {
   }

   public static SensitiveContext newInstance() {
      return new SensitiveContext();
   }

   public Object getCurrentObject() {
      return this.currentObject;
   }

   public void setCurrentObject(Object currentObject) {
      this.currentObject = currentObject;
   }

   public Field getCurrentField() {
      return this.currentField;
   }

   public String getCurrentFieldName() {
      return this.currentField.getName();
   }

   public Object getCurrentFieldValue() {
      try {
         return this.currentField.get(this.currentObject);
      } catch (IllegalAccessException e) {
         throw new SensitiveRuntimeException(e);
      }
   }

   public void setCurrentField(Field currentField) {
      this.currentField = currentField;
   }

   public List<Field> getAllFieldList() {
      return this.allFieldList;
   }

   public void setAllFieldList(List<Field> allFieldList) {
      this.allFieldList = allFieldList;
   }

   /** @deprecated */
   @Deprecated
   public void addFieldList(List<Field> fieldList) {
      this.allFieldList.addAll(fieldList);
   }

   public Class getBeanClass() {
      return this.beanClass;
   }

   public void setBeanClass(Class beanClass) {
      this.beanClass = beanClass;
   }

   public Object getEntry() {
      return this.entry;
   }

   public void setEntry(Object entry) {
      this.entry = entry;
   }
}
