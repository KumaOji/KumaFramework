package com.kuma.boot.data.jpa.fenix.helper;

import java.util.Collection;

public final class CollectionHelper {
   public CollectionHelper() {
   }

   public static boolean isNotEmpty(Object[] array) {
      return array != null && array.length > 0;
   }

   public static boolean isNotEmpty(Collection<?> collections) {
      return !isEmpty(collections);
   }

   public static boolean isEmpty(Collection<?> collections) {
      return collections == null || collections.isEmpty();
   }

   public static Object[] toArray(Object obj, int objType) {
      Object[] values;
      switch (objType) {
         case 1 -> values = obj;
         case 2 -> values = ((Collection)obj).toArray();
         default -> values = new Object[]{obj};
      }

      return values;
   }
}
