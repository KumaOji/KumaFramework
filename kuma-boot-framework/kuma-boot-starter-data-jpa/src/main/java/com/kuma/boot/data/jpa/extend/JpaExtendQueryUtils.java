package com.kuma.boot.data.jpa.extend;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.data.jpa.repository.query.JpaParameters;
import org.springframework.data.repository.query.Parameter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class JpaExtendQueryUtils {
   private static final Pattern ORDERBY_PATTERN = Pattern.compile("order\\s+by.+?$", 34);

   public JpaExtendQueryUtils() {
   }

   private static String wrapCountQuery(String query) {
      return "select count(*) from (" + query + ") as temp";
   }

   private static String cleanOrderBy(String query) {
      Matcher matcher = ORDERBY_PATTERN.matcher(query);
      StringBuilder sb = new StringBuilder();

      for(int i = 0; matcher.find(); ++i) {
         String part = matcher.group(i);
         if (canClean(part)) {
            matcher.appendReplacement(sb, "");
         } else {
            matcher.appendReplacement(sb, part);
         }
      }

      matcher.appendTail(sb);
      return sb.toString();
   }

   private static boolean canClean(String orderByPart) {
      return orderByPart != null && (!orderByPart.contains(")") || StringUtils.countOccurrencesOf(orderByPart, ")") == StringUtils.countOccurrencesOf(orderByPart, "("));
   }

   public static Map<String, Object> getParams(JpaParameters parameters, Object[] values) {
      Map<String, Object> params = new HashMap();

      for(int i = 0; i < parameters.getNumberOfParameters(); ++i) {
         Object value;
         Parameter parameter;
         try {
            value = values[i];
            parameter = parameters.getParameter(i);
            if (value == null) {
               params.put((String)parameter.getName().orElse((Object)null), (Object)null);
            }
         } catch (Exception var7) {
            continue;
         }

         if (value != null && parameter.isBindable() && isValidValue(value)) {
            Class<?> clz = value.getClass();
            if (!clz.isPrimitive() && !String.class.isAssignableFrom(clz) && !Number.class.isAssignableFrom(clz) && !clz.isArray() && !Collection.class.isAssignableFrom(clz) && !clz.isEnum()) {
               params = toParams(value);
            } else {
               params.put((String)parameter.getName().orElse((Object)null), value);
            }
         }
      }

      return params;
   }

   public static Map<Integer, Object> toPositionMap(Object[] values) {
      Map<Integer, Object> valueMap = new HashMap();
      int position = 0;

      for(Object paramValue : values) {
         if (paramValue == null) {
            valueMap.put(position, (Object)null);
         } else {
            if (!isValidValue(paramValue)) {
               continue;
            }

            Class<?> clz = paramValue.getClass();
            if (!clz.isPrimitive() && !String.class.isAssignableFrom(clz) && !Number.class.isAssignableFrom(clz) && !clz.isArray() && !Collection.class.isAssignableFrom(clz) && !clz.isEnum()) {
               throw new RuntimeException("position param cannot Object");
            }

            valueMap.put(position, paramValue);
         }

         ++position;
      }

      return valueMap;
   }

   public static Map<String, Object> toParams(Object beanOrMap) {
      if (beanOrMap instanceof Map<String, Object> params) {
         ;
      } else {
         params = objectToMap(beanOrMap);
      }

      return params;
   }

   public static boolean isValidValue(Object object) {
      if (object == null) {
         return false;
      } else {
         return !(object instanceof Collection) || !CollectionUtils.isEmpty((Collection)object);
      }
   }

   public static Map<String, Object> objectToMap(Object bean) {
      if (bean == null) {
         return Collections.emptyMap();
      } else {
         try {
            Map<String, Object> description = new HashMap();
            if (bean instanceof DynaBean) {
               DynaProperty[] descriptors = ((DynaBean)DynaBean.class.cast(bean)).getDynaClass().getDynaProperties();

               for(DynaProperty descriptor : descriptors) {
                  String name = descriptor.getName();
                  description.put(name, BeanUtils.getProperty(bean, name));
               }
            } else {
               PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(bean);

               for(PropertyDescriptor descriptor : descriptors) {
                  String name = descriptor.getName();
                  if (PropertyUtils.getReadMethod(descriptor) != null) {
                     description.put(name, PropertyUtils.getNestedProperty(bean, name));
                  }
               }
            }

            return description;
         } catch (Exception var8) {
            return Collections.emptyMap();
         }
      }
   }

   public static String toCountQuery(String query) {
      return wrapCountQuery(cleanOrderBy(query));
   }
}
