package com.kuma.boot.openapi.common.util;

import com.kuma.boot.openapi.common.model.Binary;
import java.util.Collection;

public class TypeUtil {
   public static boolean isPrimitiveByteArray(Class clazz) {
      if (clazz == null) {
         return false;
      } else if (clazz.isArray()) {
         Class elementClass = clazz.getComponentType();
         return Byte.TYPE.equals(elementClass);
      } else {
         return false;
      }
   }

   public static boolean isByteArray(Class clazz) {
      if (clazz == null) {
         return false;
      } else if (!clazz.isArray()) {
         return false;
      } else {
         Class elementClass = clazz.getComponentType();
         return Byte.TYPE.equals(elementClass) || Byte.class.equals(elementClass);
      }
   }

   public static boolean isBinaryArray(Class clazz) {
      if (clazz == null) {
         return false;
      } else if (clazz.isArray()) {
         Class elementClass = clazz.getComponentType();
         return Binary.class.isAssignableFrom(elementClass);
      } else {
         return false;
      }
   }

   public static boolean isBinaryCollection(Object obj) {
      Class clazz = obj.getClass();
      if (!Collection.class.isAssignableFrom(clazz)) {
         return false;
      } else {
         Collection coll = (Collection)obj;
         Object element = coll.iterator().next();
         return element != null && Binary.class.isAssignableFrom(element.getClass());
      }
   }
}
