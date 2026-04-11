package com.kuma.boot.data.mongodb.helper.reflection;

import com.kuma.boot.common.utils.log.LogUtils;
import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class ReflectionUtil {
   private static Map<SerializableFunction<?, ?>, Field> cache = new ConcurrentHashMap();

   public ReflectionUtil() {
   }

   public static <E, R> String getFieldName(SerializableFunction<E, R> function) {
      Field field = getField(function);
      return field.getName();
   }

   public static Field getField(SerializableFunction<?, ?> function) {
      return (Field)cache.computeIfAbsent(function, ReflectionUtil::findField);
   }

   public static Field findField(SerializableFunction<?, ?> function) {
      Field field = null;
      String fieldName = null;

      try {
         Method method = function.getClass().getDeclaredMethod("writeReplace");
         method.setAccessible(Boolean.TRUE);
         SerializedLambda serializedLambda = (SerializedLambda)method.invoke(function);
         String implMethodName = serializedLambda.getImplMethodName();
         if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
            fieldName = Introspector.decapitalize(implMethodName.substring(3));
         } else {
            if (!implMethodName.startsWith("is") || implMethodName.length() <= 2) {
               if (implMethodName.startsWith("lambda$")) {
                  throw new IllegalArgumentException("SerializableFunction\u4e0d\u80fd\u4f20\u9012lambda\u8868\u8fbe\u5f0f,\u53ea\u80fd\u4f7f\u7528\u65b9\u6cd5\u5f15\u7528");
               }

               throw new IllegalArgumentException(implMethodName + "\u4e0d\u662fGetter\u65b9\u6cd5\u5f15\u7528");
            }

            fieldName = Introspector.decapitalize(implMethodName.substring(2));
         }

         String declaredClass = serializedLambda.getImplClass().replace("/", ".");
         Class<?> aClass = Class.forName(declaredClass, false, ClassUtils.getDefaultClassLoader());
         field = ReflectionUtils.findField(aClass, fieldName);
      } catch (Exception e) {
         LogUtils.error(e);
      }

      if (field != null) {
         return field;
      } else {
         throw new NoSuchFieldError(fieldName);
      }
   }
}
