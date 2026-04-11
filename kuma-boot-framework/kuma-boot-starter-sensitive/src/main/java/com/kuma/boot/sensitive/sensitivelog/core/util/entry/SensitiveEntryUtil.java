package com.kuma.boot.sensitive.sensitivelog.core.util.entry;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.sensitive.sensitivelog.annotation.SensitiveEntry;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public final class SensitiveEntryUtil {
   private SensitiveEntryUtil() {
   }

   public static boolean hasSensitiveEntry(Field field) {
      SensitiveEntry sensitiveEntry = (SensitiveEntry)field.getAnnotation(SensitiveEntry.class);
      if (ObjectUtils.isNotNull(sensitiveEntry)) {
         return true;
      } else {
         for(Annotation annotation : field.getAnnotations()) {
            sensitiveEntry = (SensitiveEntry)annotation.annotationType().getAnnotation(SensitiveEntry.class);
            if (ObjectUtils.isNotNull(sensitiveEntry)) {
               return true;
            }
         }

         return false;
      }
   }
}
