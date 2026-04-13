package com.kuma.boot.data.jpa.model;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FieldUtils {
   private static final Map<Class, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap();

   public FieldUtils() {
   }

   public static <T> SerializedLambda resolve(SFunction<T, ?> func) {
      Class clazz = func.getClass();
      return (SerializedLambda)Optional.ofNullable((WeakReference)FUNC_CACHE.get(clazz)).map(Reference::get).orElseGet(() -> {
         SerializedLambda lambda = SerializedLambda.resolve(func);
         FUNC_CACHE.put(clazz, new WeakReference(lambda));
         return lambda;
      });
   }

   public static <T> String propertyName(SFunction<T, ?> func) {
      SerializedLambda resolve = resolve(func);
      String implMethodName = resolve.getImplMethodName();
      String var10000 = implMethodName.substring(3).substring(0, 1).toLowerCase();
      return var10000 + implMethodName.substring(4);
   }
}
