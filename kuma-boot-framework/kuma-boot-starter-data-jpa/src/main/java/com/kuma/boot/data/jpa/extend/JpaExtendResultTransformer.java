package com.kuma.boot.data.jpa.extend;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.ResultTransformer;

public class JpaExtendResultTransformer implements ResultTransformer {
   private final Class resultClass;
   private boolean isInitialized;
   private String[] aliases;
   private Setter[] setters;
   private static Map<Class<?>, Function> typeConversionMap = new HashMap();

   private static Boolean parseBoolean(String arg) {
      if (StringUtils.isNumeric(arg)) {
         return "0".equals(arg) ? false : true;
      } else {
         return Boolean.parseBoolean(arg);
      }
   }

   public JpaExtendResultTransformer(Class resultClass) {
      if (resultClass == null) {
         throw new IllegalArgumentException("resultClass cannot be null");
      } else {
         this.isInitialized = false;
         this.resultClass = resultClass;
      }
   }

   public Object transformTuple(Object[] tuple, String[] aliases) {
      try {
         if (this.resultClass.isAssignableFrom(Map.class)) {
            Map result = new LinkedHashMap();

            for(int i = 0; i < tuple.length; ++i) {
               String alias = aliases[i];
               if (alias != null) {
                  result.put(alias, tuple[i]);
               }
            }

            return result;
         } else {
            aliases = this.camelAliases(aliases);
            if (!this.isInitialized) {
               this.initialize(aliases);
            } else {
               this.check(aliases);
            }

            Object result = this.resultClass.newInstance();

            for(int i = 0; i < aliases.length; ++i) {
               if (this.setters[i] != null && this.setters[i].getMethod() != null) {
                  Class<?> paramType = this.setters[i].getMethod().getParameterTypes()[0];
                  Function convertType = (Function)typeConversionMap.get(paramType);
                  Object convertedObject = convertType == null ? tuple[i] : (tuple[i] == null ? null : convertType.apply(tuple[i]));
                  this.setters[i].set(result, convertedObject);
               }
            }

            return result;
         }
      } catch (InstantiationException var8) {
         throw new HibernateException("Could not instantiate result class: " + this.resultClass.getName());
      } catch (IllegalAccessException var9) {
         throw new HibernateException("Could not instantiate result class: " + this.resultClass.getName());
      }
   }

   private String[] camelAliases(String[] aliases) {
      return aliases != null && aliases.length != 0 ? (String[])Arrays.stream(aliases).map((alias) -> this.removeSeparatorsToCamelString(alias, '_')).toArray((x$0) -> new String[x$0]) : aliases;
   }

   private void initialize(String[] aliases) {
      PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(new PropertyAccessStrategy[]{PropertyAccessStrategyBasicImpl.INSTANCE, PropertyAccessStrategyFieldImpl.INSTANCE, PropertyAccessStrategyMapImpl.INSTANCE});
      this.aliases = new String[aliases.length];
      this.setters = new Setter[aliases.length];

      for(int i = 0; i < aliases.length; ++i) {
         String alias = aliases[i];
         if (alias != null && this.checkPropertySetter(this.resultClass, alias)) {
            this.aliases[i] = alias;
            this.setters[i] = propertyAccessStrategy.buildPropertyAccess(this.resultClass, alias, true).getSetter();
         }
      }

      this.isInitialized = true;
   }

   private boolean checkPropertySetter(Class containerJavaType, String propertyName) {
      Class propertyType = null;

      try {
         Method getterMethod = ReflectHelper.findGetterMethod(containerJavaType, propertyName);
         propertyType = getterMethod.getReturnType();
      } catch (PropertyNotFoundException var7) {
         LogUtils.debug("class :{} can not find propertyName:{} getterMethod", new Object[]{containerJavaType, propertyName});

         try {
            Field field = ReflectHelper.findField(containerJavaType, propertyName);
            propertyType = field.getType();
         } catch (PropertyNotFoundException var6) {
            LogUtils.debug("class :{} can not find property:{}", new Object[]{containerJavaType, propertyName});
         }
      }

      if (propertyType == null) {
         LogUtils.debug("class :{} can not find propertyName:{} setterMethod", new Object[]{containerJavaType, propertyName});
         return false;
      } else {
         Method setterMethod = ReflectHelper.setterMethodOrNull(containerJavaType, propertyName, propertyType);
         return setterMethod != null;
      }
   }

   private void check(String[] aliases) {
      if (!Arrays.equals(aliases, this.aliases)) {
         LogUtils.debug("aliases are different from what is cached; aliases=" + String.valueOf(Arrays.asList(aliases)) + " cached=" + String.valueOf(Arrays.asList(this.aliases)), new Object[0]);
      }

   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         JpaExtendResultTransformer that = (JpaExtendResultTransformer)JpaExtendResultTransformer.class.cast(o);
         if (!this.resultClass.equals(that.resultClass)) {
            return false;
         } else {
            return Arrays.equals(this.aliases, that.aliases);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.resultClass.hashCode();
      result = 31 * result + (this.aliases != null ? Arrays.hashCode(this.aliases) : 0);
      return result;
   }

   private String removeSeparatorsToCamelString(String s, char separator) {
      StringBuilder result = new StringBuilder(s);

      for(int i = 1; i < result.length(); ++i) {
         if (result.charAt(i - 1) == separator) {
            result.deleteCharAt(i - 1);
            if (i - 1 < result.length()) {
               result.setCharAt(i - 1, Character.toUpperCase(result.charAt(i - 1)));
            }
         }
      }

      return result.toString();
   }

   static {
      typeConversionMap.put(Long.class, (Function)(v) -> Long.valueOf(v.toString()));
      typeConversionMap.put(Long.TYPE, (Function)(v) -> Long.parseLong(v.toString()));
      typeConversionMap.put(Double.class, (Function)(v) -> Double.valueOf(v.toString()));
      typeConversionMap.put(Double.TYPE, (Function)(v) -> Double.parseDouble(v.toString()));
      typeConversionMap.put(Float.class, (Function)(v) -> Float.valueOf(v.toString()));
      typeConversionMap.put(Float.TYPE, (Function)(v) -> Float.parseFloat(v.toString()));
      typeConversionMap.put(Integer.class, (Function)(v) -> Integer.valueOf(v.toString()));
      typeConversionMap.put(Integer.TYPE, (Function)(v) -> Integer.parseInt(v.toString()));
      typeConversionMap.put(Boolean.class, (Function)(v) -> parseBoolean(v.toString()) ? Boolean.TRUE : Boolean.FALSE);
      typeConversionMap.put(Boolean.TYPE, (Function)(v) -> parseBoolean(v.toString()));
      typeConversionMap.put(LocalDateTime.class, (Function)(v) -> {
         if (Timestamp.class.isInstance(v)) {
            return ((Timestamp)Timestamp.class.cast(v)).toLocalDateTime();
         } else {
            return Date.class.isInstance(v) ? ((Date)Date.class.cast(v)).toLocalDate() : v;
         }
      });
   }
}
