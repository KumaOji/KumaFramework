package com.kuma.boot.sensitive.sensitivelog.core.api;

import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.sensitive.sensitivelog.core.bs.SensitiveBs;
import java.util.Collection;
import java.util.List;

public final class SensitiveUtil {
   private SensitiveUtil() {
   }

   public static <T> T desCopy(T object) {
      return (T)SensitiveBs.newInstance().desCopy(object);
   }

   public static String desJson(Object object) {
      return SensitiveBs.newInstance().desJson(object);
   }

   public static <T> List<T> desCopyCollection(Collection<T> collection) {
      if (CollectionUtils.isEmpty(collection)) {
         return Lists.newArrayList();
      } else {
         List<T> resultList = Lists.newArrayList();

         for(T item : collection) {
            T sensitive = (T)desCopy(item);
            resultList.add(sensitive);
         }

         return resultList;
      }
   }

   public static List<String> desJsonCollection(Collection<?> collection) {
      if (CollectionUtils.isEmpty(collection)) {
         return Lists.newArrayList();
      } else {
         List<String> resultList = Lists.newArrayList();

         for(Object item : collection) {
            String sensitiveJson = desJson(item);
            resultList.add(sensitiveJson);
         }

         return resultList;
      }
   }
}
