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
                  throw new IllegalArgumentException("SerializableFunction不能传递lambda表达式,只能使用方法引用");
               }

               throw new IllegalArgumentException(implMethodName + "不是Getter方法引用");
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
