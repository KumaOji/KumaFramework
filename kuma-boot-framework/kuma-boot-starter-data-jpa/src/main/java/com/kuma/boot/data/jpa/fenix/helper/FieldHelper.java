package com.kuma.boot.data.jpa.fenix.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.util.Assert;

public final class FieldHelper {
   public FieldHelper() {
   }

   public static Field[] getAllFields(final Class<?> cls) {
      return (Field[])getAllFieldsList(cls).toArray(new Field[0]);
   }

   public static List<Field> getAllFieldsList(final Class<?> cls) {
      Assert.notNull(cls, "The class must not be null");
      List<Field> allFields = new ArrayList();

      for(Class<?> currentClass = cls; currentClass != null; currentClass = currentClass.getSuperclass()) {
         Collections.addAll(allFields, currentClass.getDeclaredFields());
      }

      return allFields;
   }
}
