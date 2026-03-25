package com.kuma.boot.openapi.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TruncateUtil {
   private static final String emptyArray = "[]";

   public static String truncate(String src) {
      return src != null && src.length() > 1000 ? src.substring(0, 100) + "(truncated...)" : src;
   }

   public static String truncate(Object obj) {
      return obj != null ? truncate(obj.toString()) : null;
   }

   public static String truncate(byte[] bytes) {
      if (ArrayUtil.isNotEmpty(bytes)) {
         if (bytes.length > 500) {
            byte[] subBytes = ArrayUtil.sub(bytes, 0, 50);
            return Convert.toHex(subBytes) + "(truncated...)";
         } else {
            return Convert.toHex(bytes);
         }
      } else {
         return null;
      }
   }

   public static String truncate(Object[] array) {
      if (ArrayUtil.isNotEmpty(array)) {
         String[] strArray = new String[array.length];

         for(int i = 0; i < array.length; ++i) {
            Object obj = array[i];
            if (TypeUtil.isPrimitiveByteArray(obj.getClass())) {
               byte[] bytes = (byte[])obj;
               strArray[i] = truncate(bytes);
            } else {
               strArray[i] = truncate(obj);
            }
         }

         return strArray.toString();
      } else {
         return "[]";
      }
   }

   public static String truncate(Collection coll) {
      if (CollUtil.isNotEmpty(coll)) {
         List list = new LinkedList();

         for(Object obj : coll) {
            if (TypeUtil.isPrimitiveByteArray(obj.getClass())) {
               byte[] bytes = (byte[])obj;
               list.add(truncate(bytes));
            } else {
               list.add(truncate(obj));
            }
         }

         return list.toString();
      } else {
         return "[]";
      }
   }
}
