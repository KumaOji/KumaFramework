package com.kuma.boot.data.mongodb.mongodb.util;

import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ReflectionUtil {
   public ReflectionUtil() {
   }

   public static Object invokeGetterMethod(Object obj, String propertyName) {
      String getterMethodName = "get" + StringUtils.capitalize(propertyName);
      return invokeMethod(obj, getterMethodName, new Class[0], new Object[0]);
   }

   public static void invokeSetterMethod(Object obj, String propertyName, Object value) {
      invokeSetterMethod(obj, propertyName, value, (Class)null);
   }

   public static void invokeSetterMethod(Object obj, String propertyName, Object value, Class<?> propertyType) {
      Class<?> type = propertyType != null ? propertyType : value.getClass();
      String setterMethodName = "set" + StringUtils.capitalize(propertyName);
      invokeMethod(obj, setterMethodName, new Class[]{type}, new Object[]{value});
   }

   public static Object getFieldValue(final Object obj, final String fieldName) {
      Field field = getAccessibleField(obj, fieldName);
      if (field == null) {
         throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + String.valueOf(obj) + "]");
      } else {
         Object result = null;

         try {
            result = field.get(obj);
         } catch (IllegalAccessException e) {
            LogUtils.error("\u4e0d\u53ef\u80fd\u629b\u51fa\u7684\u5f02\u5e38{}", new Object[]{e.getMessage()});
         }

         return result;
      }
   }

   public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
      Field field = getAccessibleField(obj, fieldName);
      if (field == null) {
         throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + String.valueOf(obj) + "]");
      } else {
         try {
            field.set(obj, value);
         } catch (IllegalAccessException e) {
            LogUtils.error("\u4e0d\u53ef\u80fd\u629b\u51fa\u7684\u5f02\u5e38:{}", new Object[]{e.getMessage()});
         }

      }
   }

   public static Field getAccessibleField(final Object obj, final String fieldName) {
      Assert.notNull(obj, "object\u4e0d\u80fd\u4e3a\u7a7a");
      Assert.hasText(fieldName, "fieldName");

      for(Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
         try {
            Field field = superClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
         }
      }

      return null;
   }

   public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes, final Object[] args) {
      Method method = getAccessibleMethod(obj, methodName, parameterTypes);
      if (method == null) {
         throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + String.valueOf(obj) + "]");
      } else {
         try {
            return method.invoke(obj, args);
         } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
         }
      }
   }

   public static Method getAccessibleMethod(final Object obj, final String methodName, final Class<?>... parameterTypes) {
      Assert.notNull(obj, "object\u4e0d\u80fd\u4e3a\u7a7a");

      for(Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
         try {
            Method method = superClass.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
         }
      }

      return null;
   }

   public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
      return getSuperClassGenricType(clazz, 0);
   }

   public static Class getSuperClassGenricType(final Class clazz, final int index) {
      Type genType = clazz.getGenericSuperclass();
      if (!(genType instanceof ParameterizedType)) {
         LogUtils.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType", new Object[0]);
         return Object.class;
      } else {
         Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
         if (index < params.length && index >= 0) {
            if (!(params[index] instanceof Class)) {
               LogUtils.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter", new Object[0]);
               return Object.class;
            } else {
               return (Class)params[index];
            }
         } else {
            LogUtils.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length, new Object[0]);
            return Object.class;
         }
      }
   }

   public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
      if (!(e instanceof IllegalAccessException) && !(e instanceof IllegalArgumentException) && !(e instanceof NoSuchMethodException)) {
         if (e instanceof InvocationTargetException) {
            return new RuntimeException("Reflection Exception.", ((InvocationTargetException)e).getTargetException());
         } else {
            return e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException("Unexpected Checked Exception.", e);
         }
      } else {
         return new IllegalArgumentException("Reflection Exception.", e);
      }
   }

   public static Update getUpdateObj(final Object obj) {
      if (obj == null) {
         return null;
      } else {
         Field[] fields = obj.getClass().getDeclaredFields();
         Update update = null;
         boolean isFirst = true;

         for(Field field : fields) {
            field.setAccessible(true);

            try {
               Object value = field.get(obj);
               if (value != null && !"id".equalsIgnoreCase(field.getName()) && !"serialversionuid".equalsIgnoreCase(field.getName())) {
                  if (isFirst) {
                     update = Update.update(field.getName(), value);
                     isFirst = false;
                  } else {
                     update = update.set(field.getName(), value);
                  }
               }
            } catch (IllegalAccessException | IllegalArgumentException e) {
               LogUtils.error(e);
            }
         }

         return update;
      }
   }
}
